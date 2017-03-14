/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.core;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.twitter.user.analytics.Model.AggregateModel;
import com.twitter.user.analytics.Policy.Policy;
import com.twitter.user.analytics.util.BufferedReaderIterator;
import com.twitter.user.analytics.util.FileUtil;
import com.twitter.user.analytics.util.LogParserUtil;
import com.twitter.user.analytics.Sharding.IShardingSpillCallback;

/**
 * Responsible for reading given shard, computes average value for each user present in shard, and
 * stores aggregated data in map. If number of users is more than memory available, then service calls
 * the appropriate shard spillage handler
 */
public class AggregatorService {
  /**
   * Shard spillage handler
   */
  private final IShardingSpillCallback shardingSpilllCallbackManager;
  /**
   * Map stores aggregated value per user
   */
  private final Map<Long, AggregateModel> aggregateMap;
  /**
   * Current shard file being processed
   */
  private final String shardFile;
  /**
   * Policy used for calculating aggregation
   */
  private final Policy policy;
  /**
   * Total number of users allowed to store in map, based on memory provided
   */
  private final long capacity;
  /**
   * Reference to current queue from which data is being processed
   */
  private final Queue<String> queue;

  public AggregatorService(IShardingSpillCallback callback, String shardFile, Policy policy, Long capacity, Queue<String> queue) {
    this.shardingSpilllCallbackManager = callback;
    this.aggregateMap = new HashMap<>();
    this.shardFile = shardFile;
    this.policy = policy;
    this.capacity = capacity;
    this.queue = queue;
  }

  /**
   * Reads given shard file, computes average, and stored output in file.
   * Calls shard spill manager in case shard needs re-sharding.
   */
  public void aggregateData() {
    AggregateModel aggregateModel;
    BufferedReaderIterator iter;
    BufferedReader reader = null;
    try {
      reader = FileUtil.openFile(shardFile);
      iter = new BufferedReaderIterator(reader);
      for (String line : iter) {
        try {
          aggregateModel = LogParserUtil.parseAvgLogFile(line);
          if (aggregateModel == null) {
            System.err.println("Skipping line due to parsing error: " + line);
          }
          //When the in-memory capacity should not be overloaded, we re-shard the sharded file
          if (aggregateMap.size() == capacity && !aggregateMap.containsKey(aggregateModel.getUserId())) {
            //Request shard re-distribution via callback
            shardingSpilllCallbackManager.handleSpillage(this.shardFile, line, this.queue);
          } else {
            userTickManager(aggregateModel);
          }
        } catch (Exception e) {
          System.err.println("Skipping line due to parsing error for line: " + line);
          System.err.println(e.getMessage());
        }
      }
      // Write output to file
      FileUtil.createOutputFile(null, aggregateMap);
    } catch (Exception ex) {
      System.out.println("Unable to process data for file - " + shardFile);
    } finally {
      FileUtil.closeFile(reader);
    }
  }

  /**
   * Computes average value for user, based on provided policy
   * All the aggregated value is stored in hashmap
   */
  private void userTickManager(AggregateModel log) {
    if (log == null) {
      return;
    }
    AggregateModel newModel;
    try {
      if (!aggregateMap.containsKey(log.getUserId())) {
        //First log entry for userid
        aggregateMap.put(log.getUserId(), log);
        return;
      }
      newModel = policy.applyPolicy(aggregateMap.get(log.getUserId()), log);
      newModel.setUserId(log.getUserId());
      System.out.println(newModel.toString());
      aggregateMap.put(newModel.getUserId(), newModel);
    } catch (Exception ex) {
      System.out.println("Error updating data in map " + ex.getMessage());
    }
  }
}
