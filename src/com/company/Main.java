package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private int size;
    private int max;
    private float[][] map;

    private final Random random = new Random();

    public Main(float detail) {
        this.size = (int) (Math.pow(2, detail) + 1);
        this.max = size - 1;
        this.map = new float[size][size];
    }

    private float get(int x, int y) {
        if (x < 0 || x > this.size - 1 || y < 0 || y > this.size - 1) return -1;
        return this.map[x][y];
    }

    private void set(int x, int y, float val) {
        this.map[x][y] = val;
    }

    private void divide(int size, float roughness) {
        int x, y, half = size / 2;
        float scale = roughness * size;
        if (half < 1) return;
        for (y = half; y < max; y += size) {
            for (x = half; x < max; x += size) {
                square(x, y, half, random.nextFloat() * scale * 2 - scale);
            }
        }
        for (y = 0; y <= max; y += half) {
            for (x = (y + half) % size; x <= max; x += size) {
                diamond(x, y, half, random.nextFloat() * scale * 2 - scale);
            }
        }
        divide(size / 2, roughness);
    }

    private float average(List<Float> list) {
        float total = 0;
        int size = 0;
        for (Float f : list) {
            if (f != -1) {
                total += f;
                size++;
            }
        }
        return total / size;
    }

    private void square(int x, int y, int size, float offset) {
        List<Float> items = new ArrayList<Float>();
        items.add(get(x - size, y - size));      // top
        items.add(get(x + size, y - size));    // right
        items.add(get(x + size, y + size));     // bottom
        items.add(get(x - size, y + size));      // left
        float ave = average(items);
        set(x, y, ave + offset);
    }

    private void diamond(int x, int y, int size, float offset) {
        List<Float> items = new ArrayList<Float>();
        items.add(get(x, y - size));      // top
        items.add(get(x + size, y));    // right
        items.add(get(x, y + size));     // bottom
        items.add(get(x - size, y));      // left
        float ave = average(items);
        set(x, y, ave + offset);
    }

    public static void main(String[] args) throws IOException {
        Main main = new Main(10);
        main.generate(0.7f);
        main.render();
        float[][] map1 = main.getMap();
        for (int i = 0; i < map1.length; i++) {
            float[] floats = map1[i];
            for (int j = 0; j < floats.length; j++) {
                System.out.print(String.format("%3.0f ", map1[i][j]));
            }
            System.out.println();
        }
    }

    private void generate(float roughness) {
        this.set(0, 0, 0);
        this.set(max, 0, max / 2);
        this.set(max, max, 0);
        this.set(0, max, max / 2);
        divide(max, roughness);
    }

    public void render() throws IOException {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        for (int i = 0; i < map.length; i++) {
            float[] floats = map[i];
            for (int j = 0; j < floats.length; j++) {
//                float h=  (30 + 240 * (max - map[i][j]) / max*2);
//                graphics.setColor(Color.getHSBColor(h,0.5f,0.5f));
                if (map[i][j] < 0) {
                    float abs = Math.abs(Math.min(0, map[i][j]) / max);
                    graphics.setColor(new Color(abs, abs, abs));
                } else {
                    float abs = Math.max(0, map[i][j]) / max;
                    graphics.setColor(new Color(abs, abs, abs));
                }

                graphics.drawLine(i, j, i, j);
            }
        }
        image.flush();
        ImageIO.write(image, "png", new File("test.png"));

    }

    public float[][] getMap() {
        return map;
    }

    ;
//    Terrain.prototype.draw = function(ctx, width, height) {
//        var self = this;
//        var waterVal = this.size * 0.3;
//        for (var y = 0; y < this.size; y++) {
//            for (var x = 0; x < this.size; x++) {
//                var val = this.get(x, y);
//                var top = project(x, y, val);
//                var bottom = project(x + 1, y, 0);
//                var water = project(x, y, waterVal);
//                var style = brightness(x, y, this.get(x + 1, y) - val);
//                rect(top, bottom, style);
//                rect(water, bottom, 'rgba(50, 150, 200, 0.15)');
//            }
//        }
//        function rect(a, b, style) {
//            if (b.y < a.y) return;
//            ctx.fillStyle = style;
//            ctx.fillRect(a.x, a.y, b.x - a.x, b.y - a.y);
//        }
//        function brightness(x, y, slope) {
//            if (y === self.max || x === self.max) return '#000';
//            var b = ~~(slope * 50) + 128;
//            return ['rgba(', b, ',', b, ',', b, ',1)'].join('');
//        }
//        function iso(x, y) {
//            return {
//                    x: 0.5 * (self.size + x - y),
//                    y: 0.5 * (x + y)
//            };
//        }
//        function project(flatX, flatY, flatZ) {
//            var point = iso(flatX, flatY);
//            var x0 = width * 0.5;
//            var y0 = height * 0.2;
//            var z = self.size * 0.5 - flatZ + point.y * 0.75;
//            var x = (point.x - self.size * 0.5) * 6;
//            var y = (self.size - point.y) * 0.005 + 1;
//            return {
//                    x: x0 + x / y,
//                    y: y0 + z / y
//            };
//        }
//    };
}
