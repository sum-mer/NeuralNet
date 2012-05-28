/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import imageProcessing.ImageProcessing;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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

    public MainWindow(String car_Recognition) throws IOException {
        super(car_Recognition);
        //BufferedImage image;
        //image = javax.imageio.ImageIO.read(new File("images/Fiat/fiat1.jpg"));
        //ImagePanel realImg = new ImagePanel(ImageProcessing.getScaledImage(image, 300, 200));

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

        jMenuItem = new JMenuItem("Marka 1");
        jMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            }
        });
        removeImageJMenu.add(jMenuItem);

        browseImagesJMenu = new JMenuItem("Przeglądaj zdjęcia");

        jMenuItem = new JMenuItem("Marka 2");
        jMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
            }
        });
        removeImageJMenu.add(jMenuItem);


        jMenuItem = new JMenuItem("Dodaj nową ...");
        jMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
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
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setSize(600, 600);
    }
}
