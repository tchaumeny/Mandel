package com.mandel.sets;

import com.mandel.complex.Complex;
import com.mandel.plotter.PlotArea;


/**
* JuliaSet -- The Julia fractal.
*
* This set is defined as the set of complex numbers z_0 for which
* the sequence (f_c(z_0), f_c(f_c(z_0)), ...) do not diverge.
* Generally, f_c(z) = z^2 + c but other transformations can be used.
*
* This family of fractals are defined by a complex number c, for instance:
*  -0.4+0.6i
*  -0.74543+0.11301*i
*  -0.75+0.11*i
*  -0.1+0.651*i
*/
public class JuliaSet extends BaseSet {

    public static final String ID = "julia";
    private final Complex c;

    public JuliaSet(BaseTransform transformation, Complex c, int iterations) {
        this.c = c;
        this.iterations = iterations;
        this.transformation = transformation;
    }

    public JuliaSet(BaseTransform transformation, double x, double y, int iterations) {
        this(transformation, new Complex(x, y), iterations);
    }

    @Override
    public String toString() {
        return String.format("Julia set (%g + %gi)", this.c.real(), this.c.imag());
    }

    @Override
    public PlotArea getDefaultArea() {
        return new PlotArea(-1.5, 1.5, -1.0, 1.0);
    }

    @Override
    protected Complex transform(Complex cur, Complex z) {
        return this.transformation.apply(cur, this.c);
    }
}
