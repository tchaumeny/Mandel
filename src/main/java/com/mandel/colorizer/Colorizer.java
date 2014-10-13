package com.mandel.colorizer;

import java.awt.Color;


/**
* Colorizer -- A Colorizer should associate a color to a float in [0, 1] range
*/
public interface Colorizer {
    public Color getColor(float val);
}
