package com.twitter.user.analytics.Sharding;

import java.io.IOException;
import java.util.Queue;

/**
 * Concrete implementation of handling spillage use cases while reading user activity file
 */
public class AverageShardingSpillCallback implements IShardingSpillCallback {

  @Override
  public void handleSpillage(String currentShard, String line, Queue<String> queue) throws IOException {
    ShardingService obj = ShardingService.getInstance();
    if (!obj.isReshardedCreated(currentShard)) {
      obj.writeToReshardFile(currentShard, line);
      // If re-shard file is successfully created, only then we add file to queue. In case of error
      // file is not added to queue for processing.
      System.out.println("Adding new shard for processing: " + obj.getReshardfileName(currentShard));
      queue.offer(obj.getReshardfileName(currentShard));
      return;
    }
    obj.writeToReshardFile(currentShard, line);
  }
}
