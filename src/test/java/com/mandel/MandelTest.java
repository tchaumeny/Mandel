package com.mandel.tests;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mandel.plotter.Plotter;
import com.mandel.sets.SetBuilder;


public class MandelTest {

    private PrintStream out, err;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        out = System.out;
        err = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(out);
        System.setErr(err);
        outContent.reset();
        errContent.reset();
    }

    public static void call(String s) {
        String[] args = s.split("\\s+");
        Plotter.run(args);
    }

    public static void assertImageEquals(String actual, String expected) throws AssertionError, IOException {
        BufferedImage actualImg = ImageIO.read(new FileInputStream(actual));
        BufferedImage expectedImg = ImageIO.read(MandelTest.class.getClassLoader().getResourceAsStream(expected));

        int width = expectedImg.getWidth();
        int height = expectedImg.getHeight();

        assertEquals(actualImg.getWidth(), width);
        assertEquals(actualImg.getHeight(), height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                assertEquals(actualImg.getRGB(i, j), expectedImg.getRGB(i, j));
            }
        }
    }

    public static void assertOutContains(String expected) throws AssertionError {
        String output = outContent.toString();
        assertTrue(
            String.format("\"%s\" does not contain \"%s\"", output, expected),
            output.contains(expected));
    }

    public static void assertErrContains(String expected) throws AssertionError {
        String output = errContent.toString();
        assertTrue(
            String.format("\"%s\" does not contain \"%s\"", output, expected),
            output.contains(expected));
    }

    @Test
    public void testJulia() throws IOException {
        try {
            call("-f test.png -set julia -param \"-0.4;0.6\" -resolution 10");
            assertImageEquals("test.png", "expected_test_julia.png");
            assertOutContains("Result saved in test.png");
        } finally {
            File file = new File("test.png");
            file.delete();
        }
    }

    @Test
    public void testCustomJulia() throws IOException {
        try {
            // With the Mandelbrot set formula that should produce the same output
            call("-f test.png -set julia -param \"-0.4;0.6\" -resolution 10 -transformation \"z^2+c\"");
            assertImageEquals("test.png", "expected_test_julia.png");
            assertOutContains("Result saved in test.png");
        } finally {
            File file = new File("test.png");
            file.delete();
        }
    }

    @Test
    public void testMandel() throws IOException {
        try {
            call("-f test.png -set mandelbrot -resolution 10");
            assertImageEquals("test.png", "expected_test_mandel.png");
            assertOutContains("Result saved in test.png");
        } finally {
            File file = new File("test.png");
            file.delete();
        }
    }

    @Test
    public void testCustomMandel1() throws IOException {
        try {
            // With the Mandelbrot set formula that should produce the same output
            call("-f test.png -set mandelbrot -resolution 10 -transformation \"z^2+c\"");
            assertImageEquals("test.png", "expected_test_mandel.png");
            assertOutContains("Result saved in test.png");
        } finally {
            File file = new File("test.png");
            file.delete();
        }
    }

    @Test
    public void testCustomMandel2() throws IOException {
        try {
            call("-f test.png -set mandelbrot -resolution 10 -transformation \"z^4+c\"");
            assertImageEquals("test.png", "expected_test_mandel2.png");
            assertOutContains("Result saved in test.png");
        } finally {
            File file = new File("test.png");
            file.delete();
        }
    }

    @Test
    public void testHelp() throws IOException {
        call("-h");
        assertOutContains("usage: java -jar Mandel.jar");
    }

    @Test
    public void testInvalidSet() throws IOException {
        call("-set iam_no_set");
        assertErrContains("Unrecognized set 'iam_no_set'!");
    }
}
