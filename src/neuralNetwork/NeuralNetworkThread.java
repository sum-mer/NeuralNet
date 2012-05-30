/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralNetwork;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alek
 */
public class NeuralNetworkThread extends ImageNeuralNetwork implements Runnable {

    ImageNeuralNetwork program;
    boolean isReady;

    public NeuralNetworkThread(ImageNeuralNetwork program) {
        this.program = program;
    }

    public void run() {
        program.processCreateTraining(15, 10, "noRGB");
        try {
            program.loadImages();
            program.processNetwork();
            program.processTrain();
            isReady = true;
        } catch (IOException ex) {
            Logger.getLogger(NeuralNetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
