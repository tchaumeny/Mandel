package com.mandel.parser;

import java.util.Map;

import com.mandel.complex.Complex;

/**
* ConstantNode -- A node of an abstract syntax tree holding a constant value.
*/
public class ConstantNode extends BaseNode {

    private final Complex value;

    public ConstantNode(Complex value) {
        this.value = value;
    }

    @Override
    public Complex compute(Map<String,Complex> context) {
        return this.value;
    }

    @Override
    public String getDisplay() {
        return this.value.toString();
    }
}
