/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Alek
 */
public class MainWindow extends JFrame {

    public JMenuBar jMenuBar;
    public JMenu jMenu;
    public JMenu removeImageJMenu;
    public JMenuItem browseImagesJMenu;
    public JMenuItem jMenuItem;
    public JFileChooser fc;
    public File folderName;
    public File brandDirs[];

    public MainWindow(String car_Recognition) throws IOException {
        super(car_Recognition);
        //BufferedImage image;
        //image = javax.imageio.ImageIO.read(new File("images/Fiat/fiat1.jpg"));
        //ImagePanel realImg = new ImagePanel(ImageProcessing.getScaledImage(image, 300, 200));

        folderName = new File("images");
        brandDirs = folderName.listFiles();

        jMenuBar = new JMenuBar();
        jMenu = new JMenu("Baza");
        jMenu.setMnemonic(KeyEvent.VK_A);
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
        removeImageJMenu = new JMenu("Dodaj zdjęcie");
        removeImageJMenu.setMnemonic(KeyEvent.VK_S);
        removeImageJMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            }
        });
        createBrandListMenu();



        browseImagesJMenu = new JMenuItem("Przeglądaj zdjęcia");
        browseImagesJMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            }
        });



        jMenuItem = new JMenuItem("Dodaj nową ...");
        jMenuItem.addActionListener(new ActionListener() {

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
        removeImageJMenu.add(jMenuItem);
        jMenu.add(browseImagesJMenu);
        jMenu.add(removeImageJMenu);

        jMenu = new JMenu("Informacje");

        jMenu.getAccessibleContext().setAccessibleDescription(
                "Help menu");
        jMenuItem = new JMenuItem("Jakieś informacje");
        jMenu.add(jMenuItem);
        jMenuBar.add(jMenu);

        this.setJMenuBar(jMenuBar);


        //this.add(jMenuBar);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(600, 600);
    }

    private void createBrandListMenu() {

        List<File> brandList =  Arrays.asList(brandDirs);  
        Collections.sort(brandList);
        for (final File brandName : brandList) {
            if (!brandName.getName().equals("test")) {
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
}
