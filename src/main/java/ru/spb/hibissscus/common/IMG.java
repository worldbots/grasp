package ru.spb.hibissscus.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

/**
 * Load image by name using resource folder
 */
public final class IMG {

    private static final Logger LOG = LoggerFactory.getLogger(IMG.class);

    public static ImageIcon icon(final String fileName) {
        try {
            return new ImageIcon(img(fileName), "");
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.debug(fileName);
            return null;
            //throw new Error(e);
        }
    }

    private static Image img(final String fileName) {
        try {
            final URL url = IMG.class.getResource(String.format("/icons/%s", fileName));
            if (url != null) {
                return Toolkit.getDefaultToolkit().getImage(url);
            }
            LOG.debug(fileName);
            return null;
            //throw new Error(fileName);
        } catch (final Throwable e) {
            e.printStackTrace();
            LOG.debug(fileName);
            return null;
            //throw new Error(e);
        }
    }

    /**
     * Build Image by the  Icon
     *
     * @param one icon
     * @return new Image
     */
    public static Image iconToImage(Icon one) {
        if (one instanceof ImageIcon) {
            return ((ImageIcon) one).getImage();
        } else {
            int w = one.getIconWidth();
            int h = one.getIconHeight();
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            one.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }

    /**
     * Combine two icons in ine Image for TrayIcon
     *
     * @param one first icon
     * @param two second icon
     * @return finish Image for TrayIcon
     */
    public static Image combineIcons(ImageIcon one, ImageIcon two) {
        int w1 = one.getIconWidth();
        int h1 = one.getIconHeight();

        int w2 = two.getIconWidth();
        int h2 = two.getIconHeight();

        int maxH = Math.max(h1, h2);

        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        BufferedImage combineImage = gc.createCompatibleImage(w1 + w2, maxH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combineImage.createGraphics();
        one.paintIcon(null, g, 0, 0);
        two.paintIcon(null, g, w1, 0);
        g.dispose();
        return combineImage;
    }

}
