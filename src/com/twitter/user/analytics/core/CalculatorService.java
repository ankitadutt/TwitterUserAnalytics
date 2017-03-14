/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.core;

import com.twitter.user.analytics.Model.AggregateModel;

public class CalculatorService {

    /**
     * Maintains a list of all file names and runs the aggregation on each file to calculate the output
     * @param oldModel previous tick value from log.
     * @param newModel new tick value from log.
     * @return duration value between 2 ticks
     */
    public static long calculateDuration(AggregateModel oldModel, AggregateModel newModel) {
        Long duration = 0L;
        if (oldModel == null || newModel == null) {
            return -1L;
        }
        try {
            Long closeTick = newModel.getTimestamp();
            Long openTick = oldModel.getTimestamp();
            duration = closeTick - openTick;
        } catch (Exception ex) {
            System.out.println("Error calculating duration for the user " + ex.getMessage());
        }
        return duration;
    }

    /**
     * Calculates average value for a given AggregateModel instance
     * @param model
     * @return new average value
     */
    public static double calculateAverage(AggregateModel model) {
        double avg = 0;
        try {
            if (model.getTotalEntries() == 0) {
                avg = model.getTotalDuration();
            } else {
                avg = model.getTotalDuration() / model.getTotalEntries();
            }
        } catch (Exception ex) {
            System.err.println("Error calculating average " + ex.getMessage());
        }
        return avg;
    }
}
