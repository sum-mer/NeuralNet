package neuralNetwork;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.encog.EncogError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.image.ImageNeuralData;
import org.encog.neural.data.image.ImageNeuralDataSet;


import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.ResetStrategy;
import org.encog.neural.networks.training.strategy.SmartMomentum;
import org.encog.util.downsample.Downsample;
import org.encog.util.downsample.RGBDownsample;
import org.encog.util.downsample.SimpleIntensityDownsample;
import org.encog.util.simple.EncogUtility;

public class ImageNeuralNetwork {

    class ImagePair {

        private final File file;
        private final int identity;

        public ImagePair(final File file, final int identity) {
            super();
            this.file = file;
            this.identity = identity;
        }

        public File getFile() {
            return this.file;
        }

        public int getIdentity() {
            return this.identity;
        }
    }

    public static void main(final String[] args) throws IOException {

        final ImageNeuralNetwork program = new ImageNeuralNetwork();

        program.processCreateTraining(32, 32, "dupa");

        // pobieranie wszystkich obrazów z folderu images do uczenia sieci
        // folder test jest pomijany - w nim przechowywane mają być zdjęcia do zapytań
        File folderName = new File("images");
        File brandDirs[] = folderName.listFiles();

        for (File brandName : brandDirs) {
            if (!brandName.getName().equals("test")) {
                if (brandName.isDirectory()) {
                    File brandFiles[] = brandName.listFiles();
                    for (File brandImg : brandFiles) {
                        //System.out.println("Marka: " + brandName.getName() + " plik: " + brandImg.getName() );
                        program.processInput(brandName.getName(), folderName + "/" + brandName.getName() + "/" + brandImg.getName());
                    }
                }
            }
        }

//        program.processInput("fiat", "images/Fiat/fiat1small.jpg");  
//        program.processInput("fiat", "images/Fiat/fiat1.jpg");          // srebrna maska
//        program.processInput("fiat", "images/Fiat/fiat2.jpg");          //niebieska maska
//        program.processInput("fiat", "images/Fiat/fiat3.jpg");          // plastikowy zderzak
//
//        program.processInput("renault", "images/Renault/renault1.jpg"); // srebrna maska, czarne wywietrzniki po bokach    
//        program.processInput("renault", "images/Renault/renault2.jpg"); // srebrna maska, czarne wywietrzniki po bokach, inny kąt padania słońca
//        program.processInput("renault", "images/Renault/renault1small.jpg");
//        
//        program.processInput("toyota", "images/Toyota/toyota1small.jpg");
//        program.processInput("toyota", "images/Toyota/toyota1.jpg");    //metaliczne tło
//        program.processInput("toyota", "images/Toyota/toyota2.jpg");    //przedni zderzak, srebrne elementy
//        program.processInput("toyota", "images/Toyota/toyota3.jpg");    //srebrna maska
//        program.processInput("toyota", "images/Toyota/toyota4.jpg");    //srebrny zderzak, nie pod katem prostym


        program.processNetwork();
        program.processTrain();
        program.processWhatIs("images/test/toyota1.jpg");

    }
    private final List<ImagePair> imageList =
            new ArrayList<ImagePair>();
    private final Map<String, String> args =
            new HashMap<String, String>();
    private final Map<String, Integer> identity2neuron =
            new HashMap<String, Integer>();
    private final Map<Integer, String> neuron2identity =
            new HashMap<Integer, String>();
    private ImageNeuralDataSet training;
    private String line;
    private int outputCount;
    private int downsampleWidth;
    private int downsampleHeight;
    private BasicNetwork network;
    private Downsample downsample;

    private int assignIdentity(final String identity) {
        if (this.identity2neuron.containsKey(identity.toLowerCase())) {
            return this.identity2neuron.get(identity.toLowerCase());
        }
        final int result = this.outputCount;
        this.identity2neuron.put(identity.toLowerCase(), result);
        this.neuron2identity.put(result, identity.toLowerCase());
        this.outputCount++;
        return result;
    }

    /**
     * public void execute(final String file) throws IOException { final
     * FileInputStream fstream = new FileInputStream(file); final
     * DataInputStream in = new DataInputStream(fstream);
     *
     * final BufferedReader br = new BufferedReader( new InputStreamReader(in));
     * while ((this.line = br.readLine()) != null) { executeLine(); }
     * in.close(); }
     */
    /**
     * private void executeCommand(final String command, final Map<String,
     * String> args) throws IOException { if (command.equals("input")) {
     * processInput(); } else if (command.equals("createtraining")) {
     * processCreateTraining(); } else if (command.equals("train")) {
     * processTrain(); } else if (command.equals("network")) { processNetwork();
     * } else if (command.equals("whatis")) { processWhatIs(); } }
     */
    /**
     * public void executeLine() throws IOException { final int index =
     * this.line.indexOf(':'); if (index == -1) { throw new EncogError("Invalid
     * command: " + this.line); } final String command = this.line.substring( 0,
     * index).toLowerCase().trim(); final String argsStr =
     * this.line.substring(index + 1).trim(); final StringTokenizer tok = new
     * StringTokenizer(argsStr, ","); this.args.clear(); while
     * (tok.hasMoreTokens()) { final String arg = tok.nextToken(); final int
     * index2 = arg.indexOf(':'); if (index2 == -1) { throw new
     * EncogError("Invalid command: " + this.line); } final String key =
     * arg.substring(0, index2).toLowerCase().trim(); final String value =
     * arg.substring(index2 + 1).trim(); this.args.put(key, value); }
     * executeCommand(command, this.args); }
     */
    private String getArg(final String name) {
        final String result = this.args.get(name);
        if (result == null) {
            throw new EncogError("Missing argument " + name
                    + " on line: " + this.line);
        }
        return result;
    }

    private void processCreateTraining(int width, int height, String strType) {

        this.downsampleWidth = width;
        this.downsampleHeight = height;
        if (strType.equals("RGB")) {
            this.downsample = new RGBDownsample();
        } else {
            this.downsample = new SimpleIntensityDownsample();
        }
        this.training = new ImageNeuralDataSet(this.downsample, false, 1, -1);
        System.out.println("Training set created");
    }

    /**
     * Funkcja dodaje do sieci zdjęcie
     *
     * @param identity nazwa identyfikująca znak, np: ferrari, maclaren..
     * @param image ścieżka do pliku ze zdjęciem
     * @throws IOException
     */
    private void processInput(String identity, String image) throws IOException {

        final int idx = assignIdentity(identity);
        final File file = new File(image);
        this.imageList.add(new ImagePair(file, idx));
        System.out.println("Added input image:" + image);
    }

    private void processNetwork() throws IOException {
        System.out.println("Downsampling images...");
        for (final ImagePair pair : this.imageList) {

            final NeuralData ideal =
                    new BasicNeuralData(this.outputCount);
            final int idx = pair.getIdentity();
            for (int i = 0; i < this.outputCount; i++) {
                if (i == idx) {
                    ideal.setData(i, 1);
                } else {
                    ideal.setData(i, -1);
                }
            }
            final Image img = ImageIO.read(pair.getFile());
            final ImageNeuralData data = new ImageNeuralData(img);
            this.training.add(data, ideal);
        }
        this.training.downsample(
                this.downsampleHeight, this.downsampleWidth);

        this.network = EncogUtility.simpleFeedForward(this.training.getInputSize(), 100, 0, this.training.getIdealSize(), true);
        System.out.println("Created network: " + this.network.toString());
    }

    private void processTrain() throws IOException {
        final String strMode = "gui";
        System.out.println("Training Beginning... Output patterns="
                + this.outputCount);
        final double strategyError = 0.25;
        final int strategyCycles = 50;
        //final Backpropagation train = new Backpropagation(this.network, this.training);
        final ResilientPropagation train = new ResilientPropagation(this.network, this.training);
        train.addStrategy(new ResetStrategy(strategyError, strategyCycles));
        //train.addStrategy(new SmartMomentum());
        if (strMode.equalsIgnoreCase("gui")) {
            EncogUtility.trainDialog(train, this.network, this.training);
        } else {

            EncogUtility.trainConsole(train, this.network, this.training, 1);
        }
        System.out.println("Training Stopped...");
    }

    public void processWhatIs(String filename) throws IOException {

        final File file = new File(filename);
        final Image img = ImageIO.read(file);
        final ImageNeuralData input = new ImageNeuralData(img);
        input.downsample(this.downsample, false,
                this.downsampleHeight, this.downsampleWidth, 1, -1);
        final int winner = this.network.winner(input);
        System.out.println("What is: "
                + filename + ", it seems to be: "
                + this.neuron2identity.get(winner));
    }
}