package com.mandel.colorizer;

import java.awt.Color;


/**
* GradientColorizer -- A linear-gradient Colorizer
*/
public class GradientColorizer implements Colorizer {

    private final float low_red, low_green, low_blue,
                        high_red, high_green, high_blue;
    private final Color minColor, maxColor;

    public GradientColorizer(float[] low, float[] high) {
        if (low.length != 3 || high.length != 3) {
            throw new IllegalArgumentException("Invalid RGB array.");
        }
        this.low_red = low[0];
        this.low_green = low[1];
        this.low_blue = low[2];
        this.high_red = high[0];
        this.high_green = high[1];
        this.high_blue = high[2];
        this.minColor = new Color(low_red, low_green, low_blue);
        this.maxColor = new Color(high_red, high_green, high_blue);
    }

    public GradientColorizer(Color low, Color high) {
        this(low.getRGBColorComponents(null), high.getRGBColorComponents(null));
    }

    public Color getColor(float val) {
        if (val == 0.f) {
            return this.minColor;
        } else if (val == 1.f) {
            return this.maxColor;
        }
        return new Color(low_red + val * (high_red - low_red),
                         low_green + val * (high_green - low_green),
                         low_blue + val * (high_blue - low_blue));
    }
}
