package com.mandel.complex;


/**
* Complex -- Basic complex numbers implementation
*/
public class Complex {

    public static final Complex One = new Complex(1, 0),
                                E = new Complex(Math.E, 0),
                                I = new Complex(0, 1);
    private final double x, y;
    private Boolean _isPosInteger = null;

    public static class IllegalOperation extends IllegalArgumentException {
        public IllegalOperation(String message) {
            super(message);
        }
    }

    public Complex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double real() {
        return this.x;
    }

    public double imag() {
        return this.y;
    }

    // Complex number operations

    public double square_norm() {
        return this.x * this.x + this.y * this.y;
    }

    public static Complex neg(Complex a) {
        return new Complex(-a.x, -a.y);
    }

    public static Complex add(Complex a, Complex b) {
        return new Complex(a.x + b.x, a.y + b.y);
    }

    public static Complex sub(Complex a, Complex b) {
        return new Complex(a.x - b.x, a.y - b.y);
    }

    public static Complex mult(Complex a, Complex b) {
        return new Complex(a.x * b.x - a.y * b.y,
                           a.x * b.y + a.y * b.x);
    }

    public static Complex div(Complex a, Complex b) {
        double b_square = b.square_norm();
        if (b_square == 0) {
            throw new IllegalOperation("Cannot divide by zero!");
        }
        // (a + ib) / (c + id) = (a + ib) * (c - id) / (c^2 + d^2)
        return new Complex((a.x * b.x + a.y * b.y) / b_square,
                           (a.y * b.x - a.x * b.y) / b_square);
    }

    public static Complex pow(Complex a, Complex b) {
        if (b.isPosInteger()) {
            // b is an integer, we assume it is not too big and we
            // just repeat multiplications
            Complex res = a;
            for (int i = 1; i < (int)(b.x); i++) {
                res = mult(res, a);
            }
            return res;
        } else if (a.equals(Complex.E)) {
            double r = Math.exp(b.x);
            return new Complex(r * Math.cos(b.y), r * Math.sin(b.y));
        } else {
            throw new IllegalOperation("Unsupported operation!");
        }
    }

    public static Complex fact(Complex a) {
        if (a.isPosInteger()) {
            int res = 1;
            // Assume small a, no need to be very clever here.
            for (int i = 2; i <= (int)(a.x); i++) {
                res *= i;
            }
            return new Complex(res, 0);
        }
        throw new IllegalOperation("Factorial requires a positive integer!");
    }

    private boolean isPosInteger() {
        if (this._isPosInteger == null) {
            this._isPosInteger = this.y == 0 && this.x == Math.rint(this.x);
        }
        return this._isPosInteger;
    }

    @Override
    public String toString() {
        return String.format("%g + %gi", this.x, this.y);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Complex)) {
            return false;
        } else if (other == this) {
            return true;
        }
        return (this.x == ((Complex)other).x && this.y == ((Complex)other).y);
    }
}
