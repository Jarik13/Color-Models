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

                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                float rNorm = r / 255.0f;
                float gNorm = g / 255.0f;
                float bNorm = b / 255.0f;

                float cMax = Math.max(rNorm, Math.max(gNorm, bNorm));
                float cMin = Math.min(rNorm, Math.min(gNorm, bNorm));
                float delta = cMax - cMin;

                float h = 0;
                if (delta != 0) {
                    if (cMax == rNorm) {
                        h = 60 * (((gNorm - bNorm) / delta) % 6);
                    } else if (cMax == gNorm) {
                        h = 60 * (((bNorm - rNorm) / delta) + 2);
                    } else {
                        h = 60 * (((rNorm - gNorm) / delta) + 4);
                    }
                }
                if (h < 0) h += 360;

                float s = (cMax == 0) ? 0 : (delta / cMax);
                float v = cMax;

                int newRGB = hsvToRGB(h, s, v);
                hsvImage.setRGB(x, y, newRGB);
            }
        }
        return hsvImage;
    }

    private static int hsvToRGB(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float r = 0, g = 0, b = 0;
        if (h >= 0 && h < 60) {
            r = c; g = x; b = 0;
        } else if (h >= 60 && h < 120) {
            r = x; g = c; b = 0;
        } else if (h >= 120 && h < 180) {
            r = 0; g = c; b = x;
        } else if (h >= 180 && h < 240) {
            r = 0; g = x; b = c;
        } else if (h >= 240 && h < 300) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }

        int R = (int) ((r + m) * 255);
        int G = (int) ((g + m) * 255);
        int B = (int) ((b + m) * 255);

        return new Color(R, G, B).getRGB();
    }
}
