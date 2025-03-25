import managers.ColorModelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    private static JLabel imageLabel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Color models!");
        frame.setLayout(new BorderLayout());
        initializeUI(frame);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private static void initializeUI(JFrame frame) {
        initializeInputPanel(frame);
        initializeImagePanel(frame);
    }

    private static void initializeInputPanel(JFrame frame) {
        JPanel inputPanel = new JPanel();
        JButton uploadButton = new JButton("Upload file");
        JButton convertHSVButton = new JButton("Convert to HSV");

        final BufferedImage[] originalImage = new BufferedImage[1];

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    originalImage[0] = javax.imageio.ImageIO.read(selectedFile);
                    displayImage(originalImage[0]);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        convertHSVButton.addActionListener(e -> {
            if (originalImage[0] != null) {
                BufferedImage hsvImage = ColorModelManager.convertToHSV(originalImage[0]);
                ImageIcon hsvIcon = new ImageIcon(hsvImage.getScaledInstance(800, 800, Image.SCALE_SMOOTH));
                imageLabel.setIcon(hsvIcon);
            }
        });

        inputPanel.add(uploadButton);
        inputPanel.add(convertHSVButton);
        frame.add(inputPanel, BorderLayout.NORTH);
    }

    private static void initializeImagePanel(JFrame frame) {
        JPanel imagePanel = new JPanel();
        imageLabel = new JLabel();
        imagePanel.add(imageLabel);
        frame.add(imagePanel, BorderLayout.CENTER);
    }

    private static void displayImage(BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(800, 800, Image.SCALE_SMOOTH));
        imageLabel.setIcon(imageIcon);
    }
}