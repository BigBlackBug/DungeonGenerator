package com.company.color;

import java.awt.*;

/**
 * Created by bigbl on 6/10/2015.
 */
public interface ColorPicker {
    final Color WATER_COLOR = Color.decode("0x00BFFF");
    final float WATER_THRESHOLD = 0.3f;

    public Color getColor(float value);
}
