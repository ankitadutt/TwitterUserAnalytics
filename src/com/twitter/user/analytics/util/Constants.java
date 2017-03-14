/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.util;

public class Constants {
    //Memory Configuration
    public static final long MAX_MEMORY_IN_MB = 500;
    public static final long MAX_NUMBER_OF_USERS = 100000;
    public static final int NUM_SHARDS = 5;
    public static final int MAX_MEMORY_PER_USER_RECORD_IN_BYTES = 80; //bytes value

    // File Paths
    public static final String OUTPUT_FILE="MetricsOutput.txt";
    public static final String OUTPUT_PATH="/tmp/test/";
    public static final String SHARD_OUTPUT_PATH="/tmp/test/";

    //Policy
    public static final String DEFAULT_POLICY= "IGNORE_MISSING";
    public static final String POLICY2 = "UPDATE_WITH_CURRENT";

    //Others
    public static final String SEPARATOR = ",";
}
