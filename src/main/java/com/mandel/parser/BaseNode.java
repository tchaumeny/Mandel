package com.mandel.parser;

import java.util.Map;

import com.mandel.complex.Complex;


/**
* BaseNode -- A node of an abstract syntax tree.
*
* Child classes should define how to compute the node value.
*/
public abstract class BaseNode {

    public abstract Complex compute(Map<String,Complex> context);

    public BaseNode reduce() {
        return this;
    }

    public abstract String getDisplay();


    /**
     * Print the AST with one node per row.
     *
     * This is mainly used for debugging purpose.
     */
    public void prettyPrint(int depth, boolean isLast) {
        StringBuilder sb = new StringBuilder();
        if (depth != 0) {
            sb.append("    ");
        }
        for (int i = 0; i < depth - 1; i++) {
            sb.append("│   ");
        }
        sb.append(isLast ? "└── " : "├── ");
        sb.append(this.getDisplay());
        System.out.println(sb);
    }

    public void prettyPrint() {
        this.prettyPrint(0, true);
    }
}
