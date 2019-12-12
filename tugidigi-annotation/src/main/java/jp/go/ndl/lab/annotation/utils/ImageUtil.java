package jp.go.ndl.lab.annotation.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class ImageUtil {

    public static double min(double... values) {
        if (values.length == 0) return 0;
        Arrays.sort(values);
        return values[0];
    }

    public static double max(double... values) {
        if (values.length == 0) return 0;
        Arrays.sort(values);
        return values[values.length - 1];
    }

    public static BufferedImage toCompatibleImage(GraphicsConfiguration gc, BufferedImage img) {
        if (img.getColorModel().equals(gc.getColorModel())) {
            return img;
        }
        BufferedImage compatibleImage = gc.createCompatibleImage(img.getWidth(), img.getHeight(), img.getTransparency());
        Graphics g = compatibleImage.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        img.flush();
        return compatibleImage;
    }

    /**
     * TargetWidthやTargetHeightが-1の時には、もう片方の値を基準に元の画像と同じ比率を保って拡大縮小
     */
    public static BufferedImage getFasterScaledInstance(BufferedImage img, int targetWidth, int targetHeight, boolean progressiveBilinear) {
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        if (targetWidth == -1) {
            targetWidth = (int) (1.0d * targetHeight / img.getHeight() * img.getWidth());
        }

        if (targetHeight == -1) {
            targetHeight = (int) (1.0d * targetWidth / img.getWidth() * img.getHeight());
        }

        int w, h;
        int prevW = ret.getWidth();
        int prevH = ret.getHeight();

        if (progressiveBilinear) {
            w = img.getWidth();
            h = img.getHeight();
        } else {
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (progressiveBilinear && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (progressiveBilinear && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            if (scratchImage == null) {
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }
//            System.out.println(g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
//            System.out.println(g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION));
//            System.out.println(g2.getRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION));
//            System.out.println(g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            prevW = w;
            prevH = h;
            ret = scratchImage;
            // System.out.println(w + " " + h);
        } while (w != targetWidth || h != targetHeight);

        if (g2 != null) {
            g2.dispose();
        }

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            ret = scratchImage;
        }
        return ret;
    }
}
