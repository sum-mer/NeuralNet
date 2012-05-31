package gui;

import javax.swing.*;

import java.awt.*;

public class ThreadPlugin extends Thread {

    int Delay = 620;
    JProgressBar pb;

    public ThreadPlugin(JProgressBar progressbar) {
        pb = progressbar;
    }

    public void run() {
        int minimum = 0;
        int maximum = 100;

        for (int i = minimum; i < maximum; i++) {
            try {
                int value = pb.getValue();
                pb.setValue(value + 1);
                if (pb.getValue() >= maximum) {
                }

                Thread.sleep(Delay);
            } catch (InterruptedException ignoredException) {
            }
        }
    }
}