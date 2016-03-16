package com.company;

import com.company.color.ColorPicker;

/**
 * Created by bigbl on 6/10/2015.
 */
public abstract class MapCreator {
    protected ColorPicker colorPicker;

    protected MapCreator(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }
}
