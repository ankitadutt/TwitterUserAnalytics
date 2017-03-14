package com.twitter.user.analytics.util;

import com.twitter.user.analytics.Model.AggregateModel;

/**
 * Utility class for parsing various log files.
 * All log files can be added in this class.
 */
public class LogParserUtil {
  public static AggregateModel parseAvgLogFile(String line) {
    if (line == null || line.trim().isEmpty()) return null;
    String[] currLine = line.split(Constants.SEPARATOR);
    if (currLine.length < 3) {
      return null;
    }
    AggregateModel aggregateModel = new AggregateModel();
    try {
      aggregateModel.setUserId(Long.parseLong(currLine[0].trim()));
      aggregateModel.setTimestamp(Long.parseLong(currLine[1].trim()));
      if ("open".equalsIgnoreCase(currLine[2].trim()) || "close".equalsIgnoreCase(currLine[2].trim())) {
        aggregateModel.setOperationType(currLine[2].trim());
      } else {
        return null;
      }
    } catch (Exception ex) {
      System.out.println("Unable to read line from file: " + ex.getMessage());
      return null;
    }
    return aggregateModel;
  }
}
