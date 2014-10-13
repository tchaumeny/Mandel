package com.mandel.sets;

import java.util.Map;
import java.util.TreeMap;

import com.mandel.complex.Complex;
import com.mandel.parser.AST;
import com.mandel.parser.BaseNode;


/**
* CustomTransform -- A transformation based on some formula.
*
* This transformation parses the formula into an AST, which is then
* used as the transformer to generate the sequence.
*/
class CustomTransform implements BaseTransform {
    private final BaseNode root;

    public CustomTransform(String input) { 
        this.root = AST.parse(input).reduce();
    }
    @Override
    public Complex apply(Complex z, Complex c) {
        Map<String, Complex> context = new TreeMap<String, Complex>();
        context.put("c", c);
        context.put("z", z);
        return this.root.compute(context);
    }
}
