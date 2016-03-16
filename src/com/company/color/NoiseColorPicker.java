package com.company.color;

import java.awt.*;

/**
 * Created by bigbl on 6/10/2015.
 */
public class NoiseColorPicker implements ColorPicker {
    @Override
    public Color getColor(float value) {
        if (value < WATER_THRESHOLD) {
            return WATER_COLOR;
        } else {
            return new Color(0, value, 0);
        }
    }
}
