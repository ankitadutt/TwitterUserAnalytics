package com.twitter.user.analytics.util;


public class MemoryUtil {

  /**
   * /**
   * Helper method to find total number of shards
   * @param totalUsers
   * @param maxMemoryInMb
   * @return Rounds off the shard value to next upper long value. In case shard value is 1.0001, then it is rounded off to 2
   */
  public static int calculateNumberOfShards(long totalUsers, long maxMemoryInMb) {
    long heapSize = Runtime.getRuntime().totalMemory();
    double max = (double) maxMemoryInMb * 1024 * 1024;
    //While processing a shard in-memory, the system stores approx. 56 bytes of data per user
    //The calculation below approximates the 56 bytes to 80 bytes
    if (maxMemoryInMb == 0 || max>heapSize) {
      return Constants.NUM_SHARDS;
    }
    
    return (int) Math.ceil(((double) totalUsers * Constants.MAX_MEMORY_PER_USER_RECORD_IN_BYTES) / max);
  }
}
