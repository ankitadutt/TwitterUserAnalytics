/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.test;

import com.twitter.user.analytics.Model.AggregateModel;
import com.twitter.user.analytics.Policy.IgnoreMissingPolicy;
import com.twitter.user.analytics.Policy.UpdateWithCurrentPolicy;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author ankita
 */
public class PolicyTest {

    @Test
    public void TestIgnoreWrongEntry() {
        AggregateModel obj1 = new AggregateModel();
        AggregateModel obj2 = new AggregateModel();
        obj1.setUserId(1L);
        obj1.setOperationType("open");
        obj2.setOperationType("open");
        obj1.setTotalDuration(2000L);
        obj1.setTotalEntries(3L);
        obj1.setTimestamp(123456789L);
        obj2.setTimestamp(123456799L);
        obj2.setTotalDuration(2010L);
        obj2.setTotalEntries(4L);
        IgnoreMissingPolicy policy = new IgnoreMissingPolicy();
        AggregateModel resultObj = policy.applyPolicy(obj1, obj2);
        AggregateModel expectedObj = new AggregateModel();
        expectedObj.setTotalDuration(2010L);
        expectedObj.setTotalEntries(4L);
        expectedObj.setTimestamp(123456799L);
        expectedObj.setOperationType("open");
        System.out.println("result " + resultObj.toString());
        System.out.println("expected " + expectedObj.toString());
        Assert.assertEquals(resultObj, obj1);
    }

    @Test
    public void UpdateWithCurrentOpen() {
        AggregateModel obj1 = new AggregateModel();
        AggregateModel obj2 = new AggregateModel();
        obj1.setUserId(1L);
        obj2.setUserId(1L);
        obj1.setOperationType("open");
        obj2.setOperationType("open");
        obj1.setTotalDuration(2000L);
        obj1.setTotalEntries(3L);
        obj1.setTimestamp(123456789L);
        obj2.setTimestamp(123456799L);

        UpdateWithCurrentPolicy policy = new UpdateWithCurrentPolicy();
        AggregateModel resultObj = policy.applyPolicy(obj1, obj2);
        AggregateModel expectedObj = new AggregateModel();
        expectedObj.setTotalDuration(2010L);
        expectedObj.setTotalEntries(4L);
        expectedObj.setTimestamp(123456799L);
        expectedObj.setOperationType("open");
        Assert.assertEquals(resultObj, expectedObj);
    }

      @Test
    public void TesIgnoreMissingOpenClose() {
        AggregateModel obj1 = new AggregateModel();
        AggregateModel obj2 = new AggregateModel();
        obj1.setUserId(1L);
        obj2.setUserId(1L);
        obj1.setOperationType("close");
        obj2.setOperationType("open");
        obj1.setTotalDuration(2000L);
        obj1.setTotalEntries(3L);
        obj1.setTimestamp(123456789L);
        obj2.setTimestamp(123456799L);
        
        IgnoreMissingPolicy policy = new IgnoreMissingPolicy();
        AggregateModel resultObj = policy.applyPolicy(obj1, obj2);
        AggregateModel expectedObj = new AggregateModel();
        expectedObj.setOperationType("open");
        expectedObj.setTimestamp(123456799L);
        expectedObj.setTotalDuration(2010L);
        expectedObj.setTotalDuration(4L);
        Assert.assertEquals(resultObj, expectedObj);

    }
    
    @Test
    public void TestUpdateWithCurrentOpenClose() {
        AggregateModel obj1 = new AggregateModel();
        AggregateModel obj2 = new AggregateModel();
        obj1.setUserId(1L);
        obj2.setUserId(1L);
        obj1.setOperationType("close");
        obj2.setOperationType("open");
        obj1.setTotalDuration(2000L);
        obj1.setTotalEntries(3L);
        obj1.setTimestamp(123456789L);
        obj2.setTimestamp(123456799L);
        
        UpdateWithCurrentPolicy policy = new UpdateWithCurrentPolicy();
        AggregateModel resultObj = policy.applyPolicy(obj1, obj2);
        AggregateModel expectedObj = new AggregateModel();
        expectedObj.setOperationType("open");
        expectedObj.setTimestamp(123456799L);
        expectedObj.setTotalDuration(2010L);
        expectedObj.setTotalDuration(4L);
        Assert.assertEquals(resultObj, expectedObj);

    }
}
