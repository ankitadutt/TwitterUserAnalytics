/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.twitter.user.analytics.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class BufferedReaderIterator implements Iterable<String> {

  private static final int MAX_CHARS = 50;
  private BufferedReader r;

  public BufferedReaderIterator(BufferedReader r) {
    this.r = r;
  }

  @Override
  public Iterator<String> iterator() {
    return new Iterator<String>() {

      @Override
      public boolean hasNext() {
        try {
          r.mark(MAX_CHARS);
          if (r.read() < 0) {
            return false;
          }
          r.reset();
          return true;
        } catch (IOException e) {
          e.printStackTrace();
          return false;
        }
      }

      @Override
      public String next() {
        try {
          return r.readLine();
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

}
