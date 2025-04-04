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
    private static boolean isRGB = false;

    private static JLabel rgbLabel = new JLabel("RGB: ");
    private static JLabel hsvLabel = new JLabel("HSV: ");

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
        JButton saveButton = new JButton("Save Image");

        JSlider valueSlider = new JSlider(0, 100, 100);

        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    originalImage = javax.imageio.ImageIO.read(selectedFile);
                    displayedImage = ColorModelManager.convertToHSV(originalImage);
                    isRGB = false;
                    repaintImage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        convertHSVButton.addActionListener(e -> {
            if (originalImage != null) {
                displayedImage = ColorModelManager.convertToHSV(originalImage);
                isRGB = false;
                selectionRect.setBounds(0, 0, 0, 0);
                repaintImage();
            }
        });

        convertRGBButton.addActionListener(e -> {
            if (originalImage != null) {
                displayedImage = ColorModelManager.convertToRGB(originalImage);
                isRGB = true;
                selectionRect.setBounds(0, 0, 0, 0);
                repaintImage();
            }
        });

        valueSlider.setMajorTickSpacing(10);
        valueSlider.setMinorTickSpacing(1);
        valueSlider.setPaintTicks(true);
        valueSlider.setPaintLabels(true);

        valueSlider.addChangeListener(e -> {
            if (!isRGB) {
                value = valueSlider.getValue() / 100.0f;
                if (originalImage != null) {
                    if (isSelectionComplete) {
                        displayedImage = ColorModelManager.convertToHSVWithValueAndSelection(originalImage, value, selectionRect);
                    } else {
                        displayedImage = ColorModelManager.convertToHSVWithValueAndSelection(originalImage, value, new Rectangle(0, 0, originalImage.getWidth(), originalImage.getHeight()));
                    }
                    repaintImage();
                }
            }
        });

        saveButton.addActionListener(e -> {
            if (displayedImage != null) {
                JFileChooser saveChooser = new JFileChooser();
                int returnValue = saveChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File saveFile = saveChooser.getSelectedFile();
                    try {
                        javax.imageio.ImageIO.write(displayedImage, "PNG", saveFile); // Збереження у форматі PNG
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(uploadButton);
        inputPanel.add(convertHSVButton);
        inputPanel.add(convertRGBButton);
        inputPanel.add(valueSlider);
        inputPanel.add(rgbLabel);
        inputPanel.add(hsvLabel);
        inputPanel.add(saveButton);
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

        imagePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (displayedImage != null) {
                    int panelWidth = imagePanel.getWidth();
                    int panelHeight = imagePanel.getHeight();
                    int imageWidth = displayedImage.getWidth();
                    int imageHeight = displayedImage.getHeight();

                    int offsetX = (panelWidth - imageWidth) / 2;
                    int offsetY = (panelHeight - imageHeight) / 2;

                    int imageX = x - offsetX;
                    int imageY = y - offsetY;

                    if (imageX >= 0 && imageX < displayedImage.getWidth() && imageY >= 0 && imageY < displayedImage.getHeight()) {
                        Color color = new Color(displayedImage.getRGB(imageX, imageY));
                        float[] hsv = new float[3];
                        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);

                        rgbLabel.setText(String.format("RGB: (%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue()));
                        hsvLabel.setText(String.format("HSV: (%.2f, %.2f, %.2f)", hsv[0] * 360, hsv[1], hsv[2]));
                    }
                }
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