package com.company.color;

import java.awt.*;

/**
 * Created by bigbl on 6/10/2015.
 */
public class DiamondSquareColorPicker implements ColorPicker {
    private float maxValue;

    public DiamondSquareColorPicker(float maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public Color getColor(float value) {
        if (value < WATER_THRESHOLD) {
            return WATER_COLOR;
        } else {
            float abs = value / maxValue;
            return new Color(0, Math.min(1, abs + WATER_THRESHOLD), 0);
        }
    }
}
