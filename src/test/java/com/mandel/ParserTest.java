package com.mandel.tests;

import java.util.Map;
import java.util.TreeMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.mandel.complex.Complex;
import com.mandel.parser.AST;
import com.mandel.parser.BaseNode;


public class ParserTest {

    @Test
    public void testComputation1() {
        BaseNode root = AST.parse("1").reduce();
        assertEquals(new Complex(1, 0), root.compute(null));
    }

    @Test
    public void testComputation2() {
        BaseNode root = AST.parse("e").reduce();
        assertEquals(new Complex(Math.E, 0), root.compute(null));
    }

    @Test
    public void testComputation3() {
        BaseNode root = AST.parse("2^2").reduce();
        assertEquals(new Complex(4, 0), root.compute(null));
    }

    @Test
    public void testComputation4() {
        BaseNode root = AST.parse("(3+2*i)/(5-i)").reduce();
        assertEquals(new Complex(0.5, 0.5), root.compute(null));
    }

    @Test
    public void testComputation5() {
        BaseNode root = AST.parse("1.4+3*(4!-2.5)").reduce();
        assertEquals(new Complex(65.9, 0), root.compute(null));
    }

    @Test
    public void testComputation6() {
        BaseNode root = AST.parse("(-1+i)*(1-2*i)").reduce();
        assertEquals(new Complex(1, 3), root.compute(null));
    }

    @Test
    public void testComputation7() {
        BaseNode root = AST.parse("(1.4+z)*(1-2*c)").reduce();
        TreeMap<String,Complex> context = new TreeMap<String,Complex>();
        context.put("z", new Complex(0, 0.23));
        context.put("c", new Complex(2.2, -4));
        assertEquals(new Complex(-6.6, 10.418), root.compute(context));
    }

    @Test
    public void testComputation8() {
        BaseNode root = AST.parse("1+(z+2*c)^2/3").reduce();
        TreeMap<String,Complex> context = new TreeMap<String,Complex>();
        context.put("z", new Complex(1, 1));
        context.put("c", new Complex(-3, 0));
        assertEquals(new Complex(9, -10./3), root.compute(context));
    }

    @Test(expected = AST.InvalidFormula.class)
    public void testMissingOperand() {
        AST.parse("1+3+");
    }

    @Test(expected = AST.InvalidFormula.class)
    public void testMissingOperator() {
        AST.parse("1 3");
    }

    @Test(expected = AST.InvalidFormula.class)
    public void testExtraOperand() {
        AST.parse("3!2");
    }
}
