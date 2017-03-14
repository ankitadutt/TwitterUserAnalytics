/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testproject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 *
 * @author ankita
 */
public class FileGenerator {
    
  private static void fileGenerator() throws IOException{
      Long i = 1000000L;
          Long timestamp = 123456780L;

      FileWriter fw = new FileWriter("D:/generated.txt",true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
    Random rand = new Random();
    Long userId;
    while(i>0){
     userId = (long)(rand.nextDouble()*9999)%500;
    String operation;
    if(timestamp%2L==0)
        operation = "open";
    else
        operation = "close";
    timestamp=timestamp+5;
    String line = userId+","+timestamp+","+operation;
    out.println(line);
    i--;
    
    }
  }
  
    public static void main(String[] args) throws IOException{
       fileGenerator();
    }
}
