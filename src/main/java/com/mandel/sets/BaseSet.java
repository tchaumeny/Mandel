package com.mandel.sets;

import java.lang.UnsupportedOperationException;

import com.mandel.complex.Complex;
import com.mandel.plotter.PlotArea;


/**
* BaseSet -- Base class for fractals constructed through recursive transformations.
*/
public abstract class BaseSet {

    protected static final float BOUND = 4.0f;
    protected int iterations;
    protected BaseTransform transformation;
    protected abstract Complex transform(Complex cur, Complex z);

    public abstract String toString();

    public abstract PlotArea getDefaultArea();

    /**
     * Iterate the transformation until the escape condition is reached.
     * The closer to 1.0, the most likely (x, y) belongs to the set.
     */
    public float contains(double x, double y) {
        Complex z , cur;
        z = new Complex(x, y);
        cur = z;
        for (int i = 0; i < this.iterations; ++i) {
            if (cur.square_norm() > this.BOUND) {
                return (float)i / this.iterations;
            }
            cur = this.transform(cur, z);
        }
        return 1.f;
    }
}
