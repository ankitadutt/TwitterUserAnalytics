package com.twitter.user.analytics.test;

import com.twitter.user.analytics.util.BufferedReaderIterator;
import com.twitter.user.analytics.util.FileUtil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ankita
 */
public class IteratorTest {
    
    @Test
    public void testBufferedReaderIterator() throws FileNotFoundException {
        BufferedReader reader = FileUtil.openFile("D://testInput.txt");
        BufferedReaderIterator iterable = new BufferedReaderIterator(reader);
        String[] list = {"1, 1, open",
                "2, 2, open",
                "1, 3, close",
                "3, 4, open",
                "3, 5, close",
                "2, 6, open"};
        int index = 0;
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Assert.assertEquals(list[index++], iterator.next());
        }
    }
}
