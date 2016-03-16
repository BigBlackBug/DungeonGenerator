package com.company;


import com.company.color.ColorPicker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by bigbl on 6/10/2015.
 */
public class Utils {

    public static void render(float[][] map, int width, int height, String fileName, ColorPicker colorPicker) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        for (int i = 0; i < map.length; i++) {
            float[] floats = map[i];
            for (int j = 0; j < floats.length; j++) {
                graphics.setColor(colorPicker.getColor(map[i][j]));
                graphics.drawLine(i, j, i, j);
            }
        }
        image.flush();
        ImageIO.write(image, "png", new File(fileName));
    }
}
