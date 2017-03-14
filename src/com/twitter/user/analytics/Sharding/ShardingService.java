/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.Sharding;

import com.twitter.user.analytics.Model.AggregateModel;
import com.twitter.user.analytics.util.BufferedReaderIterator;
import com.twitter.user.analytics.util.Constants;
import com.twitter.user.analytics.util.FileUtil;
import com.twitter.user.analytics.util.LogParserUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for managing sharding needs of the application. Only a single instance should be
 * instantiated of this class.
 */
public class ShardingService {

  //private final long NUMBER_OF_SHARDS;
  /**
   * Holds a single instance of the ShardingService class.
   */
  private static ShardingService instance;

  /**
   * Map contains mapping between shard file name vs writer instance.
   * The writer instance is kept open, so that data can be written faster.
   */
  private Map<Integer, Map<String, BufferedWriter>> shardFileVsWriter;

  /**
   * Map contains mapping between re-sharded file name vs writer instance.
   */
  private Map<String, BufferedWriter> reshardFileVsWriter;

  private ShardingService() {
    shardFileVsWriter = new HashMap<>();
    reshardFileVsWriter = new HashMap<>();
  }

  public static ShardingService getInstance() {
    if (instance == null) {
      instance = new ShardingService();
    }
    return instance;
  }


    /**
     * This method is reponsible for populating the multiple shards
     * 
     * @param inputFile
     * @param numberOfShards
     */

  public List<String> createShards(String inputFile, int numberOfShards) throws IOException {
    BufferedReader reader;
    BufferedReaderIterator readerIter;
    try {
      reader = FileUtil.openFile(inputFile);
      readerIter = new BufferedReaderIterator(reader);
      createShardFiles(numberOfShards);
      for (String line : readerIter) {
        try {
          AggregateModel obj = LogParserUtil.parseAvgLogFile(line);
          if (obj == null) {
            System.err.println("Skipping line due to parsing error: " + line);
            continue;
          }
          addToShard(obj.getUserId(), line);
        } catch (Exception e) {
          System.err.println("Skipping line due to parsing error: " + line);
          System.err.println(e.getMessage());
        }
      }
    } catch (Exception ex) {
      System.out.println("Error creating shards " + ex.getMessage());
    } finally {
      for (Integer shardId : shardFileVsWriter.keySet()) {
        try {
          shardFileVsWriter.get(shardId).get(getShardFileName(shardId)).flush();
          shardFileVsWriter.get(shardId).get(getShardFileName(shardId)).close();
        } catch (IOException e) {
          System.err.println("Error closing file: " + getShardFileName(shardId));
        }
      }
    }
    return getShardFilePathList();
  }

  /**
   * Writes data to new re-sharded file.
   *
   * @param inputShardfile Original shard file which needs to be re-sharded
   * @param line Line to be written in new shard
   */
  public void writeToReshardFile(String inputShardfile, String line) throws IOException {
    String fileName = getReshardfileName(inputShardfile);
    if (!reshardFileVsWriter.containsKey(fileName)) {
      reShardFile(inputShardfile);
    }
    reshardFileVsWriter.get(fileName).write(line);
    reshardFileVsWriter.get(fileName).newLine();
  }

  /**
   * Close new re-sharded file
   */
  public void closeReShardFile(String inputFile) {
    String fileName = getReshardfileName(inputFile);
    if (reshardFileVsWriter.containsKey(fileName)) {
      try {
        reshardFileVsWriter.get(fileName).flush();
        reshardFileVsWriter.get(fileName).close();
      } catch (IOException e) {
        System.err.print("Error while closing file: " + fileName);
      }
    }
  }

  /**
   * Creates a new shard file from an existing sharded file
   *
   * @param inputShardfile Original shard file which needs to be re-sharded
   */
  private void reShardFile(String inputShardfile) throws IOException {
    String fileName = getReshardfileName(inputShardfile);
    reshardFileVsWriter.put(fileName, new BufferedWriter(new FileWriter(fileName)));
  }

    /**
     * Checks if for a given file, a re-shard has already been created
     * @param inputShardFile
     */
    public boolean isReshardedCreated(String inputShardFile) {
    return reshardFileVsWriter.containsKey(getReshardfileName(inputShardFile));
  }

    /**
     * Generates a name for the newly created re-shard
     * 
     * @param inputShardFile
     */
    public String getReshardfileName(String inputShardFile) {
    return inputShardFile + "_1";
  }

    /*
     * This method creates the first set of shards for the given file
     */
  private void createShardFiles(int numberOfShards) {
    String fileName;
    int shardIdOffset = shardFileVsWriter.size();
    try {
      for (int i = 0; i < numberOfShards; i++) {
        fileName = getShardFileName(shardIdOffset + i);
        Map<String, BufferedWriter> temp = new HashMap<>();
        temp.put(fileName, new BufferedWriter(new FileWriter(fileName)));
        shardFileVsWriter.put(i, temp);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  private void addToShard(Long userId, String data) {
    try {
      BufferedWriter writer = getShardWriter(userId);
      writer.write(data);
      writer.newLine();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private BufferedWriter getShardWriter(long userId) {
    int shardId = (int) (userId % shardFileVsWriter.size());
    return shardFileVsWriter.get(shardId).get(getShardFileName(shardId));
  }

  private String getShardFileName(int shardNum) {
    return Constants.SHARD_OUTPUT_PATH + "shard" + Integer.toString(shardNum);
  }

  private List<String> getShardFilePathList() {
    List<String> list = new ArrayList<>();
    for (int i = 0; i < shardFileVsWriter.size(); i++) {
      list.add(getShardFileName(i));
    }
    return list;
  }

}
