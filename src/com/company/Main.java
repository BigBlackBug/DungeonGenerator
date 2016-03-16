package com.company;

import com.company.terrain.DiamondSquare;
import com.company.terrain.NoiseGenerator;

import java.io.IOException;

/**
 * Created by bigbl on 6/10/2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        float value = (float) (Math.pow(2, 10) + 1);
        DiamondSquare diamondSquare = new DiamondSquare(value);
        float[][] generate = diamondSquare.generate(0.9f);
        Utils.render(generate, 1024, 1024, "diamond.png", diamondSquare.getColorPicker());

        NoiseGenerator noiseGenerator = new NoiseGenerator();
        float[][] noise = noiseGenerator.generateNoise(1024, 1024, 10, 0.6f);
        Utils.render(noise, 1024, 1024, "noise.png", noiseGenerator.getColorPicker());

        DungeonGenerator dungeonGenerator = new DungeonGenerator(200);
        dungeonGenerator.generate(30);
        dungeonGenerator.render("dungeon.png");
    }
}
