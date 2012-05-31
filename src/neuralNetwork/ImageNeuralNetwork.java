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
import org.encog.neural.data.NeuralDataPair;
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

        program.processCreateTraining(15, 10, "noRGB");

        program.loadImages();
        program.processNetwork();
        program.processTrain();
        System.out.println(program.processWhatIs(ImageIO.read(new File("images/test/test_renault.jpg"))));

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

    public void loadImages() throws IOException {
        File folderName = new File("images");
        File brandDirs[] = folderName.listFiles();

        for (File brandName : brandDirs) {
            if (!brandName.getName().equals("test")) {
                if (brandName.isDirectory()) {
                    File brandFiles[] = brandName.listFiles();
                    for (File brandImg : brandFiles) {
                        //System.out.println("Marka: " + brandName.getName() +" plik: " + brandImg.getName() );
                        this.processInput(brandName.getName(), folderName + "/" + brandName.getName() + "/" + brandImg.getName());
                    }
                }
            }
        }
    }

    public void processCreateTraining(int width, int height, String strType) {

        this.downsampleWidth = height;
        this.downsampleHeight = width;
        if (strType.equals("RGB")) {
            this.downsample = new RGBDownsample();
        } else {
            this.downsample = new SimpleIntensityDownsample();
        }
        this.training = new ImageNeuralDataSet(this.downsample, true, 1, -1);
        System.out.println("Training set created");
    }

    public void processInput(String identity, String image) throws IOException {

        final int idx = assignIdentity(identity);
        final File file = new File(image);
        this.imageList.add(new ImagePair(file, idx));
        System.out.println("Added input image:" + image);
    }

    public void processNetwork() throws IOException {
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

    public void processTrain() throws IOException {
        final String strMode = "nogui";
        System.out.println("Training Beginning... Output patterns="
                + this.outputCount);
        final double strategyError = 0.2;
        final int strategyCycles = 80;
        final ResilientPropagation train = new ResilientPropagation(this.network, this.training);
        train.addStrategy(new ResetStrategy(strategyError, strategyCycles));
        if (strMode.equalsIgnoreCase("gui")) {
            EncogUtility.trainDialog(train, this.network, this.training);
        } else {

            EncogUtility.trainConsole(train, this.network, this.training, 1);
        }
        System.out.println("Training Stopped...");
    }

    public String processWhatIs(Image img) throws IOException {

        final ImageNeuralData input = new ImageNeuralData(img);
        input.downsample(this.downsample, false,
                this.downsampleHeight, this.downsampleWidth, 1, -1);
        final int winner = this.network.winner(input);
        return this.neuron2identity.get(winner);
    }
}
