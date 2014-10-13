package com.mandel.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.StringTokenizer;
import java.util.Stack;

import com.mandel.complex.Complex;
import com.mandel.plotter.Plotter;


/**
* AST -- A node of an abstract syntax tree holding an operator.
*
* Any non-leaf node of the tree will be an AST. This class is simply
* called AST as it defines the logic for building and evaluating an AST,
* although the root of the tree can be any BaseNode instance.
*/
public class AST extends BaseNode {

    public static final int NEG = 1, ADD = 2, SUB = 3, MULT = 4, DIV = 5, POW = 6, FACT = 7;
    public static final int LPAR = -1, RPAR = -2;
    public static final String[] OPS = {"-", "+", "-", "*", "/", "^", "!"};
    public static final int[] OPS_OPDS = {1, 2, 2, 2, 2, 2, 1};
    public static final int[] OPS_PREC = {25, 5, 5, 15, 15, 20, 30};

    private final int op;
    private final List<BaseNode> operands;

    public static class InvalidFormula extends IllegalArgumentException {
        public InvalidFormula(String message) {
            super(message);
        }
    }

    public AST(int op) {
        this.op = op;
        this.operands = new ArrayList<BaseNode>();
    }

    /**
     * Parse input and generate an abstract syntax tree.
     *
     * A two stacks variant of the Shunting-yard algorithm is used.
     */
    public static BaseNode parse(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input, " +-*/^!()", true);
        Stack<Integer> operators = new Stack<Integer>();
        Stack<BaseNode> operands = new Stack<BaseNode>();

        boolean first = true;
        int operator = 0, previous = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim();
            if (token.equals("")) {
                continue;
            }
            previous = operator;
            operator = 0;
            switch (token) {
                case "+":
                    operator = ADD;
                    break;
                case "-":
                    if (!first
                        && (previous == 0 || previous == RPAR
                            || (previous >= 1
                                && OPS_PREC[previous - 1] >= OPS_PREC[NEG - 1]))) {
                        operator = SUB;
                    } else {
                        operator = NEG;
                    }
                    break;
                case "*":
                    operator = MULT;
                    break;
                case "/":
                    operator = DIV;
                    break;
                case "^":
                    operator = POW;
                    break;
                case "!":
                    operator = FACT;
                    break;
                case "(":
                    operator = LPAR;
                    break;
                case ")":
                    operator = RPAR;
                    break;
            }
            if (operator == LPAR) {
                operators.push(LPAR);
            } else if (operator == RPAR) {
                unstackOperators(operators, operands, -1, false, false);
                operators.pop();
            } else if (operator != 0) {
                unstackOperators(operators, operands, OPS_PREC[operator - 1], true, false);
                operators.push(operator);
            } else  {
                BaseNode node = null;
                if (token.equals("i")) {
                    node = new ConstantNode(Complex.I);
                } else if (token.equals("e")) {
                    node = new ConstantNode(Complex.E);
                } else if (Pattern.matches("[a-zA-Z]+", token)) {
                    node = new VariableNode(token);
                } else {
                    try {
                        double real = Double.parseDouble(token);
                        node = new ConstantNode(new Complex(real, 0));
                    } catch (NumberFormatException e) {
                        // Dealt with later
                    }
                }
                if (node == null) {
                    throw new InvalidFormula(String.format("Unrecognized token '%s'", token));
                }
                operands.push(node);
            }
            first = false;
        }
        unstackOperators(operators, operands, -1, true, true);
        BaseNode root = operands.pop();
        if (!operands.empty()) {
            throw new InvalidFormula("Unmatched operands!");
        }
        return root;
    }

    private static void unstackOperators(Stack<Integer> operators, Stack<BaseNode> operands,
                                         int prec, boolean checkEmpty, boolean skipLPar) {
        while (!(checkEmpty && operators.empty())
            && (skipLPar || operators.peek() != LPAR)
            && (prec == -1 || OPS_PREC[operators.peek() - 1] >= prec)) {
            int topOp = operators.pop();
            AST node = new AST(topOp);
            for (int i = 0; i < OPS_OPDS[topOp - 1]; i++) {
                if (operands.empty()) {
                    throw new InvalidFormula("Missing operands!");
                }
                node.pushOperand(operands.pop());
            }
            operands.push(node);
        }
    }

    private void pushOperand(BaseNode operand) {
        if (this.operands.size() > OPS_OPDS[this.op - 1]) {
            throw new InvalidFormula(
                String.format("Operator %s takes %d operands, not %d.",
                              OPS[this.op], OPS_OPDS[this.op - 1], this.operands.size()));
        }
        this.operands.add(operand);
    }


    /**
     * Evaluate a node using some context.
     */
    @Override
    public Complex compute(Map<String,Complex> context) {
        switch (this.op) {
            case NEG:
                return Complex.neg(this.operands.get(0).compute(context));
            case ADD:
                return Complex.add(this.operands.get(1).compute(context),
                                   this.operands.get(0).compute(context));
            case SUB:
                return Complex.sub(this.operands.get(1).compute(context),
                                   this.operands.get(0).compute(context));
            case MULT:
                return Complex.mult(this.operands.get(1).compute(context),
                                    this.operands.get(0).compute(context));
            case DIV:
                return Complex.div(this.operands.get(1).compute(context),
                                   this.operands.get(0).compute(context));
            case POW:
                return Complex.pow(this.operands.get(1).compute(context),
                                   this.operands.get(0).compute(context));
            case FACT:
                return Complex.fact(this.operands.get(0).compute(context));
        }
        throw new RuntimeException(String.format("Invalid node %d", this.op));
    }

    /**
     * Recursive constant folding.
     */
    @Override
    public BaseNode reduce() {
        boolean allConstants = true;
        for (int i = 0; i < this.operands.size(); i++) {
            BaseNode operand = this.operands.get(i).reduce();
            this.operands.set(i, operand);
            allConstants = allConstants && operand instanceof ConstantNode;
        }
        if (allConstants) {
            return new ConstantNode(this.compute(null));
        }
        return this;
    }

    @Override
    public String getDisplay() {
        return OPS[this.op - 1];
    }

    @Override
    public void prettyPrint(int depth, boolean isLast) {
        super.prettyPrint(depth, isLast);
        Iterator<BaseNode> it = this.operands.iterator();
        while (it.hasNext()) {
            it.next().prettyPrint(depth + 1, !it.hasNext());
        }
    }
}
