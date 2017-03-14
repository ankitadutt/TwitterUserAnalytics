package com.twitter.user.analytics.test;

import com.twitter.user.analytics.core.AggregatorService;
import com.twitter.user.analytics.Policy.IgnoreMissingPolicy;
import com.twitter.user.analytics.Policy.Policy;
import com.twitter.user.analytics.Policy.UpdateWithCurrentPolicy;
import com.twitter.user.analytics.Sharding.AverageShardingSpillCallback;
import com.twitter.user.analytics.Sharding.IShardingSpillCallback;
import com.twitter.user.analytics.Sharding.ShardingService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ankita
 */
public class AggregatorServicesTest {

    @Test
    public void TestShardingService() throws IOException {

        List<String> shardFiles = ShardingService.getInstance().createShards("D://generated.txt", 2);
        String[] actual = shardFiles.toArray(new String[shardFiles.size()]);
        String[] expected = {"/tmp/test/shard0", "/tmp/test/shard1", "/tmp/test/shard2"};

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void RecursiveShardingWithPolicy2() {
        IShardingSpillCallback callback = new AverageShardingSpillCallback();
        List<String> shardFiles = new ArrayList<>();
        shardFiles.add("D:/tmp/test/shard0");
        shardFiles.add("D:/tmp/test/shard1");
        shardFiles.add("D:/tmp/test/shard2");
        Queue<String> fileToBeProcessedQueue = new LinkedList<>();
        fileToBeProcessedQueue.addAll(shardFiles);
        Long capacity = 1L;
        UpdateWithCurrentPolicy policy = new UpdateWithCurrentPolicy();
        //Test where the aggregator service completes running in a single shard
        AggregatorService ag = new AggregatorService(callback, "D:/tmp/test/shard0", policy, capacity,fileToBeProcessedQueue);
        ag.aggregateData();
        String nextActual = fileToBeProcessedQueue.poll();
        String nextExpected = "D:/tmp/test/shard0";
        System.out.println(nextActual);
        Assert.assertEquals(nextExpected, nextActual);
    }
    
    @Test
    public void RecursiveShardingWithPolicy1() {
        IShardingSpillCallback callback = new AverageShardingSpillCallback();
        List<String> shardFiles = new ArrayList<>();
        shardFiles.add("D:/tmp/test/shard0");
        shardFiles.add("D:/tmp/test/shard1");
        shardFiles.add("D:/tmp/test/shard2");
        Queue<String> fileToBeProcessedQueue = new LinkedList<>();
        fileToBeProcessedQueue.addAll(shardFiles);
        Long capacity = 1L;
        IgnoreMissingPolicy policy = new IgnoreMissingPolicy();
        //Test where the aggregator service completes running in a single shard
        AggregatorService ag = new AggregatorService(callback, "D:/tmp/test/shard0", policy, capacity,fileToBeProcessedQueue);
        ag.aggregateData();
        String nextActual = fileToBeProcessedQueue.poll();
        String nextExpected = "D:/tmp/test/shard0";
        System.out.println(nextActual);
        Assert.assertEquals(nextExpected, nextActual);
    }
}
