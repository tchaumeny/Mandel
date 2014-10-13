package com.mandel.colorizer;

import java.awt.Color;


/**
* BinaryColorizer -- A basic two-colors Colorizer
*/
public class BinaryColorizer implements Colorizer {

    private final Color in, out;

    public BinaryColorizer(Color out, Color in) {
        this.out = out;
        this.in = in;
    }

    public Color getColor(float val) {
        return (val < 1.f) ? out : in;
    }
}
