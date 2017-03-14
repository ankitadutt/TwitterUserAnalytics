/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.test;

import com.twitter.user.analytics.Model.AggregateModel;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author ankit
 */
public class AggregateModelTest {
    @Test
    public void TestAggregateModel(){
    AggregateModel obj1 = new AggregateModel();
    obj1.setOperationType("open");
    obj1.setTimestamp(123456L);
    obj1.setTotalDuration(10L);
    obj1.setTotalEntries(3L);
    obj1.setUserId(1L);
    AggregateModel obj2 = new AggregateModel();
    obj2.setOperationType("open");
    obj2.setTimestamp(123456L);
    obj2.setTotalDuration(10L);
    obj2.setTotalEntries(3L);
    obj2.setUserId(1L);
    Assert.assertEquals(obj1.equals(obj2), true);
    
    }
}
