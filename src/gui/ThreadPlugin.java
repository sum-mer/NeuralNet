package gui;

import javax.swing.*;

import java.awt.*;
 
public class ThreadPlugin extends Thread {

  int Delay = 620;
  JProgressBar pb;

  public  ThreadPlugin(JProgressBar progressbar) {
   pb = progressbar;
  }
  

  public void run() {
      int minimum = 0; 
      int maximum = 100; 
      
      for (int i = minimum; i < maximum; i++) { 
        try {
          int value = pb.getValue();
          pb.setValue(value + 1);
                      
          //Testing the progress bar if it already reaches to its maximum value
          if (pb.getValue() >= maximum) {
          
          //Test confirmation if it runs perfectly
           
          }
           
          Thread.sleep(Delay); //Implementing the speed of the progress bar
        } catch (InterruptedException ignoredException) { //Catch an error if there is any
        }
      }
    }
 }