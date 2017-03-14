package com.twitter.user.analytics.Policy;

import com.twitter.user.analytics.Model.AggregateModel;

public class IgnoreMissingPolicy extends Policy {

    /*
        * Policy - Ignore the current entry in order to compensate for the missing entry.
        * Helps with not messing with the current average duration
     */
    @Override
    public AggregateModel applyPolicy(AggregateModel prevLog, AggregateModel currLog) {
        AggregateModel newModel = new AggregateModel();
        if (currLog.getOperationType().equals("close") && prevLog.getOperationType().equals("open")) {
            newModel = this.handleOpenCloseTick(prevLog, currLog);
        } else if (currLog.getOperationType().equals(prevLog.getOperationType())) {
            newModel = prevLog;
        } else {
            newModel = this.handleCloseOpenTick(prevLog, currLog);
        }
        return newModel;
    }
}
