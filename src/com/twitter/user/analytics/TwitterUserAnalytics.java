/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics;

import com.twitter.user.analytics.core.AggregatorService;
import com.twitter.user.analytics.Policy.IgnoreMissingPolicy;
import com.twitter.user.analytics.Policy.Policy;
import com.twitter.user.analytics.Policy.UpdateWithCurrentPolicy;
import com.twitter.user.analytics.Sharding.AverageShardingSpillCallback;
import com.twitter.user.analytics.Sharding.ShardingService;
import com.twitter.user.analytics.util.Constants;
import com.twitter.user.analytics.util.MemoryUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import com.twitter.user.analytics.Sharding.IShardingSpillCallback;

public class TwitterUserAnalytics {

  private static String INPUT_FILE;
  private static Long MAX_USERS;
  private static Long MEMORY;
  private static int SHARDS;
  private static String POLICY;
  private static Long ALLOC_PER_SHARD;

  /**
   * Setting default values
   *
   * @codeCoverageIgnore
   */
   
  static {
    MAX_USERS = Constants.MAX_NUMBER_OF_USERS;
    MEMORY = Constants.MAX_MEMORY_IN_MB;
    POLICY = Constants.DEFAULT_POLICY;
  }

  /**
    * @codeCoverageIgnore
    */
  public static void main(String[] args) {
    handleInput(args);

    try {
      ShardingService ss = ShardingService.getInstance();
      List<String> shardFiles = ss.createShards(INPUT_FILE, SHARDS);

      //Loop until all shards have been processed
      Queue<String> fileToBeProcessedQueue = new LinkedList<>();
      fileToBeProcessedQueue.addAll(shardFiles);

      AggregatorService avgAggregationService;

      //AverageShardingSpillCallback handles sharding re-distribution cases when shard is too big to fit in memory
      IShardingSpillCallback callback = new AverageShardingSpillCallback();

      //Instantiate appropriate policy
      Policy policy = handlePolicy(POLICY);
      while (fileToBeProcessedQueue.size() > 0) {
        String fileToBeProcessed = fileToBeProcessedQueue.poll();
        System.out.println("Processing file: " + fileToBeProcessed);
        avgAggregationService = new AggregatorService(callback, fileToBeProcessed, policy, ALLOC_PER_SHARD , fileToBeProcessedQueue);
        avgAggregationService.aggregateData();
        //Closing any re-sharded file if created
        ShardingService.getInstance().closeReShardFile(fileToBeProcessed);
      }
    } catch (Exception e) {
      System.err.print("Error while processing: " + e.getMessage());
      e.printStackTrace();
    } finally {
      System.out.println("Exiting application...");
    }
  }

  /**
   * Handle user entered input values
   *
   * @codeCoverageIgnore
  */
 
  private static void handleInput(String args[]) {
    if (args.length < 1) {
      throw new IllegalArgumentException("Please specify input file path. Optional params: Users, memory (in MB) and policy. \n Example: {input param} 100 10 IGNORE_MISSING");
    }
    INPUT_FILE = args[0].trim();
    try {
      if (args.length >= 2) {
        MAX_USERS = Long.parseLong(args[1].trim());
      }
      if (args.length >= 3) {
        MEMORY = Long.parseLong(args[2].trim());
      }
      if (args.length >= 4) {
        POLICY = args[3].trim();
      }
      SHARDS = MemoryUtil.calculateNumberOfShards(MAX_USERS, MEMORY);
      if (SHARDS != 0) {
        ALLOC_PER_SHARD = MAX_USERS / SHARDS;
      }

      //Testing
      //SHARDS = 2;
      //ALLOC_PER_SHARD = 2L;

      System.out.println("Max-Users = " + MAX_USERS);
      System.out.println("Memory Available (MB)= " + MEMORY);
      System.out.println("Policy = " + POLICY);
      System.out.println("Shards = " + SHARDS);
      System.out.println("ALLOC_PER_SHARD = " + ALLOC_PER_SHARD);
    } catch (Exception e) {
      throw new RuntimeException("Unable to initialize system properties. Please check input values provided! Error: " + e.getMessage());
    }
  }

    /**
     * @codeCoverageIgnore
    */
  private static Policy handlePolicy(String policyName) {
    if (policyName == null || policyName.trim().isEmpty() || policyName.equalsIgnoreCase(Constants.DEFAULT_POLICY)) {
      return new IgnoreMissingPolicy();
    } else if (policyName.equalsIgnoreCase(Constants.POLICY2)) {
      return new UpdateWithCurrentPolicy();
    }
    return new IgnoreMissingPolicy();
  }

}
