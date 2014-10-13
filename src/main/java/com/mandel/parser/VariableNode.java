package com.mandel.parser;

import java.util.Map;

import com.mandel.complex.Complex;

/**
* VariableNode -- A node of an abstract syntax tree holding a variable.
*/
public class VariableNode extends BaseNode {

    private final String key;

    public static class UndefinedReference extends IllegalArgumentException {
        public UndefinedReference(String message) {
            super(message);
        }
    }

    public VariableNode(String key) {
        this.key = key;
    }

    @Override
    public Complex compute(Map<String,Complex> context) {
        Complex res = context.get(this.key);
        if (res == null) {
            throw new UndefinedReference(String.format("No such variable %s!", this.key));
        }
        return res;
    }

    @Override
    public String getDisplay() {
        return this.key;
    }
}
