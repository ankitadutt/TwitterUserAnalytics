package com.twitter.user.analytics.Policy;

import com.twitter.user.analytics.core.CalculatorService;
import com.twitter.user.analytics.Model.AggregateModel;

public class UpdateWithCurrentPolicy extends Policy {

    /*
        * Policy - Assume that the missing operation happened at the same timestamp and calculate the toatl accordingly
        * Eg. : If 2 consecutive "opens" are encountered then assume that a previous "close" happened at the same time as the new "open"
     */

    @Override
    public AggregateModel applyPolicy(AggregateModel prevLog, AggregateModel currLog) {
        AggregateModel newModel = new AggregateModel();
        if (currLog.getOperationType().equals("close") && prevLog.getOperationType().equals("open")) {
            newModel = this.handleOpenCloseTick(prevLog, currLog);
        } else if (currLog.getOperationType().equals(prevLog.getOperationType())) {
            //Assume the previous operation to have closed now and add it to the aggregate
            Long duration = CalculatorService.calculateDuration(prevLog, currLog) + prevLog.getTotalDuration();
            newModel.setTotalDuration(duration);
            newModel.setTimestamp(currLog.getTimestamp());
            newModel.setOperationType(currLog.getOperationType());
            newModel.setTotalEntries(prevLog.getTotalEntries() + 1);
            System.out.println("using update duration with current");
        } else {
            newModel = this.handleCloseOpenTick(prevLog, currLog);
        }
        return newModel;
    }
}
