package com.company.terrain;

import com.company.MapCreator;
import com.company.color.NoiseColorPicker;

import java.util.Random;

/**
 * Created by bigbl on 6/10/2015.
 */
public class NoiseGenerator extends MapCreator {
    private final Random random = new Random();

    public NoiseGenerator() {
        super(new NoiseColorPicker());
    }

    private float[][] generateWhiteNoise(int width, int height) {
        float[][] noise = new float[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                noise[i][j] = random.nextFloat();
            }
        }
        return noise;
    }

    private float getInterpolatedNoiseValue(float[][] parentNoise, int i, int j, int octave) {
        int width = parentNoise.length;
        int height = parentNoise[0].length;

        int period = (int) Math.pow(2, octave);
        float frequency = 1.0f / period;

//        i0 is the largest multiple of the period smaller than i
        int i0 = (i / period) * period;
        int i1 = (i0 + period) % width;
        float horizontal = (i - i0) * frequency;


        int j0 = (j / period) * period;
        int j1 = (j0 + period) % height;
        float vertical = (j - j0) * frequency;

        float top = lerp(parentNoise[i0][j0], parentNoise[i1][j0], horizontal);
        float bottom = lerp(parentNoise[i0][j1], parentNoise[i1][j1], horizontal);

        return lerp(top, bottom, vertical);
    }

    private float lerp(float x0, float x1, float alpha) {
        return x0 * (1 - alpha) + alpha * x1;
    }

    public float[][] generateNoise(int width, int height, int octaveCount, float persistence) {
        float[][] sourceNoise = generateWhiteNoise(width, height);
        float[][] noise = new float[width][height];

        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        //merge noise together
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistence;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    noise[i][j] += getInterpolatedNoiseValue(sourceNoise, i, j, octave) * amplitude;
                }
            }
        }

        //normalize to 0-1
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                noise[i][j] /= totalAmplitude;
            }
        }

        return noise;
    }

}
