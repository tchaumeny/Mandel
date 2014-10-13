package com.mandel.colorizer;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
* ColorizerBuilder -- Parses a string to construct a Colorizer
*/
public class ColorizerBuilder {
    static final Pattern
        bin = Pattern.compile("(#[0-9a-fA-F]{6})[,;](#[0-9a-fA-F]{6})"),
        grad = Pattern.compile("(#[0-9a-fA-F]{6})\\.{2,3}(#[0-9a-fA-F]{6})");

    public static class UnrecognizedColor extends IllegalArgumentException {
        public UnrecognizedColor(String message) {
            super(message);
        }
    }

    public static Colorizer parse(String definition) {
        Matcher mbin = bin.matcher(definition);
        Matcher mgrad = grad.matcher(definition);
        if (mbin.matches()) {
            return new BinaryColorizer(Color.decode(mbin.group(1)), Color.decode(mbin.group(2)));
        } else if (mgrad.matches()) {
            return new GradientColorizer(Color.decode(mgrad.group(1)), Color.decode(mgrad.group(2)));
        } else {
            throw new UnrecognizedColor(String.format("Could not parse color option '%s'", definition));
        }
    }
}