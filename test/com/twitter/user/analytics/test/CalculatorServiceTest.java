/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.test;

/**
 *
 * @author ankita
 */
import com.twitter.user.analytics.core.CalculatorService;
import com.twitter.user.analytics.Model.AggregateModel;
import org.junit.Test;
import org.junit.Assert;

public class CalculatorServiceTest {

    @Test
    public void testCalculateAverage() {
        AggregateModel obj1 = new AggregateModel();
        obj1.setUserId(1L);
        obj1.setTotalDuration(100L);
        obj1.setTotalEntries(5L);
        Assert.assertEquals(20.0, CalculatorService.calculateAverage(obj1), 0.0);
    }
    
    @Test
    public void testCalculateAverageWithZeros() {
        AggregateModel obj1 = new AggregateModel();
        obj1.setUserId(1L);
        obj1.setTotalDuration(100L);
        obj1.setTotalEntries(0L);
        Assert.assertEquals(100, CalculatorService.calculateAverage(obj1), 0.0);
    }

    @Test
    public void testCalculateDuration() {
        AggregateModel obj2 = new AggregateModel();
        AggregateModel obj1 = new AggregateModel();
        obj1.setUserId(1L);
        obj2.setUserId(1L);
        obj1.setTimestamp(0L);
        obj2.setTimestamp(Long.MAX_VALUE);
        Assert.assertEquals(Long.MAX_VALUE, CalculatorService.calculateDuration(obj1, obj2), 0.0);
    }
    
    @Test
    public void testCalculateDurationNulls() {
        AggregateModel obj2 = null;
        AggregateModel obj1 = null;
        Assert.assertEquals(-1, CalculatorService.calculateDuration(obj1, obj2), 0.0);
    }

}
