package managers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorModelManager {
    public static BufferedImage convertToHSV(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage hsvImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                float[] hsv = rgbToHSV(rgb);
                int newRGB = hsvToRGB(hsv[0], hsv[1], hsv[2]);
                hsvImage.setRGB(x, y, newRGB);
            }
        }

        checkColorChange(image, hsvImage, "HSV");
        return hsvImage;
    }

    public static BufferedImage convertToHSVWithValueAndSelection(BufferedImage image, float value, Rectangle selectionRect) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage hsvImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        if (selectionRect.width <= 0 || selectionRect.height <= 0) {
            return image;
        }

        int startX = Math.max(selectionRect.x, 0);
        int startY = Math.max(selectionRect.y, 0);
        int endX = Math.min(startX + selectionRect.width, width);
        int endY = Math.min(startY + selectionRect.height, height);

        value = Math.max(0, Math.min(1, value));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                float[] hsv = rgbToHSV(rgb);

                if (hsv[0] >= 170 && hsv[0] <= 190 && x >= startX && x < endX && y >= startY && y < endY) {
                    hsv[2] = value;
                }

                int newRGB = hsvToRGB(hsv[0], hsv[1], hsv[2]);
                hsvImage.setRGB(x, y, newRGB);
            }
        }

        return hsvImage;
    }

    public static BufferedImage convertToRGB(BufferedImage hsvImage) {
        int width = hsvImage.getWidth();
        int height = hsvImage.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rgbImage.setRGB(x, y, hsvImage.getRGB(x, y));
            }
        }

        checkColorChange(hsvImage, rgbImage, "RGB");

        return rgbImage;
    }

    private static float[] rgbToHSV(int rgb) {
        Color color = new Color(rgb);
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;

        float cMax = Math.max(r, Math.max(g, b));
        float cMin = Math.min(r, Math.min(g, b));
        float delta = cMax - cMin;

        float h = 0;
        if (delta != 0) {
            if (cMax == r) {
                h = 60 * (((g - b) / delta) % 6);
            } else if (cMax == g) {
                h = 60 * (((b - r) / delta) + 2);
            } else {
                h = 60 * (((r - g) / delta) + 4);
            }
        }
        if (h < 0) h += 360;

        float s = (cMax == 0) ? 0 : (delta / cMax);
        float v = cMax;

        return new float[]{h, s, v};
    }

    private static int hsvToRGB(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float r = 0, g = 0, b = 0;
        if (h >= 0 && h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (h >= 60 && h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (h >= 120 && h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (h >= 180 && h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (h >= 240 && h < 300) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        int R = (int) ((r + m) * 255);
        int G = (int) ((g + m) * 255);
        int B = (int) ((b + m) * 255);

        return new Color(R, G, B).getRGB();
    }

    private static void checkColorChange(BufferedImage originalImage, BufferedImage convertedImage, String modelType) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        boolean isSame = true;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int originalPixel = originalImage.getRGB(x, y);
                int convertedPixel = convertedImage.getRGB(x, y);

                if (originalPixel != convertedPixel) {
                    isSame = false;
                }
            }
        }

        if (isSame) {
            JOptionPane.showMessageDialog(null, "No color change during conversion to " + modelType + ".", "No Change", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Converted to " + modelType + " successfully.", "Conversion Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
