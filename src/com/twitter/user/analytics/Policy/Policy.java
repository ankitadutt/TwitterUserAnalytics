package com.twitter.user.analytics.Policy;

import com.twitter.user.analytics.core.CalculatorService;
import com.twitter.user.analytics.Model.AggregateModel;

/**
 * Abstract class for defining policy for handling various operation types.
 * Provides implementation for certain open/close cases.
 */
public abstract class Policy {
  abstract public AggregateModel applyPolicy(AggregateModel prevLog, AggregateModel currLog);

  /**
   * Provides implementation for handling open-close tick pair.
   */
  protected AggregateModel handleOpenCloseTick(AggregateModel prevLog, AggregateModel currLog) {
    AggregateModel newModel = new AggregateModel();
    Long newDuration = CalculatorService.calculateDuration(prevLog, currLog) + prevLog.getTotalDuration();
    if (newDuration == -1) {
      newDuration = prevLog.getTotalDuration(); //ignore the wrong entry
    }
    Long entries = prevLog.getTotalEntries() + 1;
    newModel.setTotalDuration(newDuration);
    newModel.setTotalEntries(entries);
    newModel.setTimestamp(currLog.getTimestamp());
    newModel.setOperationType(currLog.getOperationType());
    return newModel;
  }


  /**
   * Provides implementation for handling close-open tick pair.
   */
  protected AggregateModel handleCloseOpenTick(AggregateModel prevLog, AggregateModel currLog) {
    AggregateModel newModel = new AggregateModel();
    newModel.setTotalDuration(prevLog.getTotalDuration());
    newModel.setTotalEntries(prevLog.getTotalEntries());
    newModel.setTimestamp(currLog.getTimestamp());
    newModel.setOperationType(currLog.getOperationType());
    return newModel;
  }
}
