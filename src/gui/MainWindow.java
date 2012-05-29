/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Alek
 */
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
    private JPanel panelForImg;
    private JProgressBar progressBar;
    private java.util.Timer theTimer;
    private ImageNeuralNetwork program;
    private boolean isReady;

    public MainWindow(String car_Recognition) throws IOException {
        super(car_Recognition);
        //BufferedImage image;
        //image = javax.imageio.ImageIO.read(new File("images/Fiat/fiat1.jpg"));
        //ImagePanel realImg = new ImagePanel(ImageProcessing.getScaledImage(image, 300, 200));
        program = new ImageNeuralNetwork();
        folderName = new File("images");
        brandDirs = folderName.listFiles();
        startButton = new JButton("Start");
        panelForImg = new JPanel();
        progressBar = new javax.swing.JProgressBar();

        labelForImgOrg = new JLabel();
        labelForImgOrg.setHorizontalAlignment(JLabel.CENTER);
        labelForImgOrg.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        panelForImg.setLayout(new FlowLayout(FlowLayout.CENTER));
        panelForImg.add(labelForImgOrg);
        panelForImg.add(startButton);

        panelForImg.add(progressBar);
        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                startProgress(60);
                try {
                    program.processCreateTraining(15, 10, "noRGB");
                    program.loadImages();
                    program.processNetwork();
                    program.processTrain();
                    isReady = true;
                } catch (IOException ex) {
                    System.out.println("dupa");;
                }
                

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

                        iconOrg = new ImageIcon(getScaledImage(img, 256, 256));

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

        //jMenu.addSeparator();
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
                    // tworzenie pustego folderu dla nowej marki
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
        //removeImageJMenu.add(jMenuItem);
        //jMenu.add(browseImagesJMenu);
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


        //this.add(jMenuBar);
        this.setLayout(new BorderLayout());
        this.getContentPane().add(panelForImg, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(600, 600);
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
            ImageIcon canvas = new ImageIcon(getScaledImage(image, 256, 256));
            labelForImgOrg.setIcon(canvas);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
