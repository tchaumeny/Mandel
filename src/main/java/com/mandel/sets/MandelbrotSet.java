package com.mandel.sets;

import com.mandel.complex.Complex;
import com.mandel.plotter.PlotArea;


/**
* MandelbrotSet -- The original Mandelbrot fractal.
*
* This set is defined as the set of complex numbers c for which
* the sequence (f_c(0), f_c(f_c(0)), ...) do not diverge.
* Generally, f_c(z) = z^2 + c but other transformations can be used.
*/
public class MandelbrotSet extends BaseSet {

    public static final String ID = "mandelbrot";

    public MandelbrotSet(BaseTransform transformation, int iterations) {
        this.iterations = iterations;
        this.transformation = transformation;
    }

    @Override
    public String toString() {
        return "Mandelbrot set";
    }

    @Override
    public PlotArea getDefaultArea() {
        return new PlotArea(-2, 1, -1, 1);
    }

    @Override
    protected Complex transform(Complex cur, Complex z) {
        return this.transformation.apply(cur, z);
    }
}
