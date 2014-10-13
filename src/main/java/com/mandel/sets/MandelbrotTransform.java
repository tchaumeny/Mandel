package com.mandel.sets;

import com.mandel.complex.Complex;


/**
* MandelbrotTransform -- The transformation used to construct the Mandelbrot fractal.
*
* It is the transformation f(z, c) -> z^2 + c
* We provide this built-in definition as it is slightly faster than
* using CustomTransform.
*/
class MandelbrotTransform implements BaseTransform {
    @Override
    public Complex apply(Complex z, Complex c) {
        return Complex.add(c, Complex.mult(z, z));
    }
}
