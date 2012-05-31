package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import neuralNetwork.ImageNeuralNetwork;
import neuralNetwork.NeuralNetworkThread;
import org.apache.commons.io.FileUtils;

public class MainWindow extends JFrame {

    private JMenuBar jMenuBar;
    private JMenu jMenu;
    private JMenu removeImageJMenu;
    private JMenuItem addBrandJMenuItem;
    private JMenuItem browseImagesJMenu;
    private JMenuItem jMenuItem;
    private JFileChooser fc;
    private File folderName;
    private File brandDirs[];
    private static BufferedImage img;
    private ImageIcon iconOrg;
    private JLabel labelForImgOrg;
    private JButton startButton;
    private JProgressBar progressBar;
    private java.util.Timer theTimer;
    private ImageNeuralNetwork program;
    private JButton checkButton;
    private JLabel resultLabel;
    private JSeparator jSeparator;
    private JLabel descriptionLabel;

    public MainWindow(String car_Recognition) throws IOException {
        super(car_Recognition);
        program = new ImageNeuralNetwork();
        folderName = new File("images");
        brandDirs = folderName.listFiles();
        startButton = new JButton("Trenuj sieć");
        checkButton = new JButton("Sprawdź zdjęcie");
        checkButton.setEnabled(false);
        progressBar = new javax.swing.JProgressBar();
        resultLabel = new JLabel();
        descriptionLabel = new JLabel("Wynik");
        jSeparator = new JSeparator();
        startButton.setBounds(10, 20, 100, 20);
        progressBar.setBounds(120, 20, 450, 20);
        jSeparator.setBounds(10, 60, 560, 2);
        this.add(jSeparator);
        labelForImgOrg = new JLabel();
        labelForImgOrg.setHorizontalAlignment(JLabel.CENTER);
        labelForImgOrg.setBorder(BorderFactory.createLineBorder(Color.black, 3));

        this.add(startButton);
        this.add(progressBar);
        this.add(jSeparator);

        labelForImgOrg.setBounds(60, 100, 300, 200);
        this.add(labelForImgOrg);
        checkButton.setBounds(400, 120, 160, 20);
        this.add(checkButton);
        descriptionLabel.setBounds(400, 150, 50, 30);
        resultLabel.setBounds(460, 150, 50, 30);
        descriptionLabel.setText("Wynik: ");
        resultLabel.setText("brak");
        this.add(descriptionLabel);
        this.add(resultLabel);
        

        checkButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (img != null) {
                        resultLabel.setText(program.processWhatIs(img));
                        //System.out.println(program.processWhatIs(img));
                    } else {
                        JOptionPane.showMessageDialog(null, "Dodaj zdjecie !", "Błąd", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                Thread run = new ThreadPlugin(progressBar); //Calling the class "threadPlugin" we created that extends with Thread
                run.start();
                NeuralNetworkThread r = new NeuralNetworkThread(program);
                Thread t = new Thread(r);
                t.start();
            }
        });

        setCanvas();

        jMenuBar = new JMenuBar();
        jMenu = new JMenu("Plik");
        jMenu.setMnemonic(KeyEvent.VK_P);
        jMenu.getAccessibleContext().setAccessibleDescription(
                "Main menu with settings");
        jMenuBar.add(jMenu);

        browseImagesJMenu = new JMenuItem("Wybierz zdjęcie");
        browseImagesJMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                fc = new JFileChooser();
                fc.setCurrentDirectory(new File("images"));
                FileFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp");
                fc.setAcceptAllFileFilterUsed(false);
                fc.addChoosableFileFilter(filter);
                int ret = fc.showDialog(null, "Wybierz plik do rozpoznania");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        File fileToRecognize = fc.getSelectedFile();

                        JOptionPane.showMessageDialog(null, "Wybrano plik: " + fileToRecognize.getName(),
                                "Plik do rozpoznania", 1);

                        img = ImageIO.read(fileToRecognize);

                        iconOrg = new ImageIcon(getScaledImage(img, 300, 200));

                        labelForImgOrg.setIcon(iconOrg);

                    } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }


            }
        });

        jMenu.add(browseImagesJMenu);

        jMenu = new JMenu("Baza");
        jMenu.setMnemonic(KeyEvent.VK_B);
        jMenu.getAccessibleContext().setAccessibleDescription(
                "Main menu with settings");
        jMenuBar.add(jMenu);

        jMenuItem = new JMenuItem("Usuń zdjęcie");

        jMenuItem.getAccessibleContext().setAccessibleDescription(
                "This option is doing simple check, by checking ping");

        jMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            }
        });

        removeImageJMenu = new JMenu("Dodaj zdjęcie do marki");
        removeImageJMenu.setMnemonic(KeyEvent.VK_S);
        removeImageJMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            }
        });
        createBrandListMenu();


        addBrandJMenuItem = new JMenuItem("Dodaj nową markę");
        addBrandJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                String newBrandName = JOptionPane.showInputDialog(null, "Wpisz nazwę marki : ",
                        "Nowa marka", 1);
                if (newBrandName != null) {

                    File newDirectory = new File("images/" + newBrandName);
                    boolean status = newDirectory.mkdir();
                    JOptionPane.showMessageDialog(null, "Stworzyłeś folder dla marki : " + newBrandName,
                            "Nowa marka", 1);

                    brandDirs = folderName.listFiles();

                    createBrandListMenu();
                } else {
                    JOptionPane.showMessageDialog(null, "Anulowano dodawanie marki.",
                            "Nowa marka", 1);
                }
            }
        });
        jMenu.add(removeImageJMenu);
        jMenu.add(addBrandJMenuItem);

        jMenu = new JMenu("About");

        jMenu.getAccessibleContext().setAccessibleDescription(
                "Help menu");
        jMenuItem = new JMenuItem("O programie");
        jMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null, "Projekt z przedmiotu Sztuczne Sieci Neuronowe ",
                        "O programie", 1);
            }
        });
        jMenu.add(jMenuItem);

        jMenuItem = new JMenuItem("Autorzy");
        jMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                JOptionPane.showMessageDialog(null, "Aleksander Sobol & Olga Zachariasz",
                        "Autorzy", 1);
            }
        });

        jMenu.add(jMenuItem);
        jMenuBar.add(jMenu);

        this.setJMenuBar(jMenuBar);

        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(600, 400);
    }

    class ThreadPlugin extends Thread {

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

                    //Testing the progress bar if it already reaches to its maximum value
                    if (pb.getValue() >= maximum) {
                        checkButton.setEnabled(true);
                    }

                    Thread.sleep(Delay);
                } catch (InterruptedException ignoredException) {
                }
            }
        }
    }

    public void startProgress(int seconds) {
        progressBar.setMaximum(seconds * 10);
        theTimer = new java.util.Timer();
        theTimer.schedule(new ProgressTimerTask(), new Date(), 100);
    }

    private class ProgressTimerTask extends TimerTask {

        public void run() {
            int currentProgressBarValue = progressBar.getValue();
            if (currentProgressBarValue == progressBar.getMaximum()) {
                theTimer.cancel();
                theTimer = null;
            } else {
                progressBar.setValue(currentProgressBarValue + 1);
            }
        }
    }

    private void createBrandListMenu() {

        List<File> brandList = Arrays.asList(brandDirs);
        Collections.sort(brandList);
        for (final File brandName : brandList) {
            if (!brandName.getName().equals("test") && brandName.isDirectory()) {
                if (notExist(brandName)) {
                    jMenuItem = new JMenuItem(brandName.getName());
                    jMenuItem.setName(brandName.getName());
                    jMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent ae) {
                            fc = new JFileChooser();
                            FileFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp");
                            fc.setAcceptAllFileFilterUsed(false);
                            fc.addChoosableFileFilter(filter);
                            int ret = fc.showDialog(null, "Wybierz plik");

                            if (ret == JFileChooser.APPROVE_OPTION) {
                                File file = fc.getSelectedFile();
                                File source = file.getAbsoluteFile();
                                File desc = new File(brandName.getAbsolutePath());
                                try {
                                    FileUtils.copyFileToDirectory(source, desc);
                                    JOptionPane.showMessageDialog(null, "Dodano plik " + source.getName() + " do folderu marki " + brandName.getName(),
                                            "Nowe zdjęcie", 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
                    removeImageJMenu.add(jMenuItem);
                }

            }
        }
    }

    public boolean notExist(File f) {
        int count = removeImageJMenu.getItemCount();
        for (int i = 0; i < count; i++) {
            if (f.getName().equals(removeImageJMenu.getItem(i).getName())) {
                return false;
            }
        }
        return true;
    }

    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    private void setCanvas() {
        BufferedImage image;
        try {
            image = ImageIO.read(new File("images/canvas.jpg"));
            ImageIcon canvas = new ImageIcon(getScaledImage(image, 300, 200));
            labelForImgOrg.setIcon(canvas);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
