package com.mandel.sets;

import com.mandel.complex.Complex;


/**
* BaseTransform -- Interface for defining Complex transformations.
*/
interface BaseTransform {
    public Complex apply(Complex z, Complex c);
}
