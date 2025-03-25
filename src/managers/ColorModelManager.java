package managers;

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
                Color color = new Color(rgb);
                float[] hsv = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                int newRGB = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
                hsvImage.setRGB(x, y, newRGB);
            }
        }
        return hsvImage;
    }
}
