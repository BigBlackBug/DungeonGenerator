package com.company.terrain;

import com.company.MapCreator;
import com.company.color.DiamondSquareColorPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An implementation of the Diamond Square Algorithms
 */
public class DiamondSquare extends MapCreator {

    private final Random random = new Random();
    //length of the map's one side
    private int length;
    //maximum value
    private int maxValue;
    private float[][] map;

    public DiamondSquare(float value) {
        super(new DiamondSquareColorPicker(value - 1));
        this.length = (int) value;
        this.maxValue = length - 1;
    }

    private void init() {
        this.map = new float[length][length];
        set(0, 0, 0);
        set(maxValue, 0, 0);
        set(maxValue, maxValue, 0);
        set(0, maxValue, 0);
    }

    /**
     * generates a height map using the DSA
     * @param roughness higher values make the map be higher
     * @return
     */
    public float[][] generate(float roughness) {
        init();
        diamondSquare(length - 1, roughness);
        return map;
    }

    private float get(int x, int y) {
        if (x < 0 || x > this.length - 1 || y < 0 || y > this.length - 1) {
            return -1;
        }
        return this.map[x][y];
    }

    private void set(int x, int y, float value) {
        this.map[x][y] = value;
    }

    private void diamondSquare(int size, float roughness) {
        int half = size / 2;
        if (half < 1) {
            return;
        }
        float scale = roughness * size;

        for (int y = half; y < maxValue; y += size) {
            for (int x = half; x < maxValue; x += size) {
                square(x, y, half, random.nextFloat() * scale * 2 - scale);
            }
        }
        for (int y = 0; y <= maxValue; y += half) {
            //wrapping
            for (int x = (y + half) % size; x <= maxValue; x += size) {
                diamond(x, y, half, random.nextFloat() * scale * 2 - scale);
            }
        }
        diamondSquare(size / 2, roughness);
    }

    /**
     * Calculates the average value of sum of four corner
     * values of the square with size 'size'
     * surrounding the item at index [x,y]
     * @param x
     * @param y
     * @param size
     * @param offset
     */
    private void square(int x, int y, int size, float offset) {
        List<Float> items = new ArrayList<Float>();
        items.add(get(x - size, y - size));
        items.add(get(x + size, y - size));
        items.add(get(x + size, y + size));
        items.add(get(x - size, y + size));
        set(x, y, average(items) + offset);
    }

    /**
     * Calculates the average value of sum of four corner
     * values of the diamond with size 'size'
     * surrounding the item at index [x,y]
     * @param x
     * @param y
     * @param size
     * @param offset
     */
    private void diamond(int x, int y, int size, float offset) {
        List<Float> items = new ArrayList<>();
        items.add(get(x, y - size));    // top
        items.add(get(x + size, y));    // right
        items.add(get(x, y + size));    // bottom
        items.add(get(x - size, y));    // left
        set(x, y, average(items) + offset);
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
}
