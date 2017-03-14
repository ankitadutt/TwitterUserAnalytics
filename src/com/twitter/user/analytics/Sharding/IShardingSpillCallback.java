package com.twitter.user.analytics.Sharding;

import java.io.IOException;
import java.util.Queue;

/**
 * Interface for handling sharding spillage cases (cases where current shard is too big to fit in memory)
 * Method adds a new shard files for processing in the queue, in case user-ids are unevenly distributed in a shard file
 */
public interface IShardingSpillCallback {
  void handleSpillage(String currentShard, String line, Queue<String> queue) throws IOException;
}
