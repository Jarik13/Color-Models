import managers.ColorModelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    private static BufferedImage originalImage;
    private static BufferedImage displayedImage;
    private static Rectangle selectionRect = new Rectangle();
    private static Point2D.Double startPoint = null;
    private static float value = 1.0f;
    private static boolean isSelectionComplete = false;

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
        JButton convertRGBButton = new JButton("Convert to RGB");

        JSlider valueSlider = new JSlider(0, 100, 100);

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    originalImage = javax.imageio.ImageIO.read(selectedFile);
                    displayedImage = originalImage;
                    repaintImage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        convertHSVButton.addActionListener(e -> {
            if (originalImage != null) {
                displayedImage = ColorModelManager.convertToHSV(originalImage);
                repaintImage();
            }
        });

        convertRGBButton.addActionListener(e -> {
            if (originalImage != null) {
                displayedImage = ColorModelManager.convertToRGB(originalImage);
                repaintImage();
            }
        });

        valueSlider.setMajorTickSpacing(10);
        valueSlider.setMinorTickSpacing(1);
        valueSlider.setPaintTicks(true);
        valueSlider.setPaintLabels(true);

        valueSlider.addChangeListener(e -> {
            value = valueSlider.getValue() / 100.0f;
            if (originalImage != null) {
                displayedImage = ColorModelManager.convertToHSVWithValueAndSelection(originalImage, value, selectionRect);
                repaintImage();
            }
        });

        inputPanel.add(uploadButton);
        inputPanel.add(convertHSVButton);
        inputPanel.add(convertRGBButton);
        inputPanel.add(valueSlider);
        frame.add(inputPanel, BorderLayout.NORTH);
    }

    private static void initializeImagePanel(JFrame frame) {
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (displayedImage != null) {
                    int panelWidth = getWidth();
                    int panelHeight = getHeight();
                    int imageWidth = displayedImage.getWidth();
                    int imageHeight = displayedImage.getHeight();

                    int x = (panelWidth - imageWidth) / 2;
                    int y = (panelHeight - imageHeight) / 2;

                    g.drawImage(displayedImage, x, y, this);

                    if (selectionRect.width > 0 && selectionRect.height > 0) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(isSelectionComplete ? Color.BLUE : new Color(0, 0, 255, 50));

                        int adjustedX = selectionRect.x + x;
                        int adjustedY = selectionRect.y + y;

                        if (isSelectionComplete) {
                            float[] dashPattern = {10f, 10f};
                            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f));
                            g2d.drawRect(adjustedX, adjustedY, selectionRect.width, selectionRect.height);
                        } else {
                            g2d.fillRect(adjustedX, adjustedY, selectionRect.width, selectionRect.height);
                            g2d.setColor(Color.BLUE);
                            g2d.drawRect(adjustedX, adjustedY, selectionRect.width, selectionRect.height);
                        }
                    }
                }
            }
        };

        imagePanel.setPreferredSize(new Dimension(800, 800));

        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isSelectionComplete = false;

                int panelWidth = imagePanel.getWidth();
                int panelHeight = imagePanel.getHeight();
                int imageWidth = displayedImage.getWidth();
                int imageHeight = displayedImage.getHeight();

                int offsetX = (panelWidth - imageWidth) / 2;
                int offsetY = (panelHeight - imageHeight) / 2;

                startPoint = new Point2D.Double(e.getX() - offsetX, e.getY() - offsetY);
                selectionRect.setBounds((int) startPoint.getX(), (int) startPoint.getY(), 0, 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isSelectionComplete = true;
                imagePanel.repaint();
            }
        });

        imagePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int panelWidth = imagePanel.getWidth();
                int panelHeight = imagePanel.getHeight();
                int imageWidth = displayedImage.getWidth();
                int imageHeight = displayedImage.getHeight();

                int offsetX = (panelWidth - imageWidth) / 2;
                int offsetY = (panelHeight - imageHeight) / 2;

                int x = (int) Math.min(startPoint.getX(), e.getX() - offsetX);
                int y = (int) Math.min(startPoint.getY(), e.getY() - offsetY);
                int width = Math.abs((int) (startPoint.getX() - (e.getX() - offsetX)));
                int height = Math.abs((int) (startPoint.getY() - (e.getY() - offsetY)));

                selectionRect.setBounds(x, y, width, height);
                imagePanel.repaint();
            }
        });

        frame.add(imagePanel, BorderLayout.CENTER);
    }

    private static void repaintImage() {
        for (Window window : JFrame.getWindows()) {
            if (window instanceof JFrame) {
                window.repaint();
            }
        }
    }
}