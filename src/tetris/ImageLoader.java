package tetris;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader {

    public static BufferedImage loadImage(String path, int width, int height) {
        try {
            BufferedImage original = ImageIO.read(new File("Data" + path));
            Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(scaled, 0, 0, null);
            g2d.dispose();
            return resized;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
