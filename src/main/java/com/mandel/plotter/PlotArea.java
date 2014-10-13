package com.mandel.plotter;


/**
* PlotArea -- A quadruplet of numbers defining a plot rectangulare area.
*/
public class PlotArea {

    public double left, right, bottom, top;

    public static class InvalidArea extends IllegalArgumentException {
        public InvalidArea(String message) {
            super(message);
        }
    }

    public PlotArea(double left, double right, double bottom, double top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
    }

    /**
     * Build a PlotArea defined by a string parameter.
     */
    public static PlotArea parse(String arg) {
        String[] parts = arg.split(";");
        if (parts.length != 4) {
            throw new InvalidArea("Area should consist of four numbers!");
        }
        double left, right, bottom, top;
        try {
            left = Double.parseDouble(parts[0]);
            right = Double.parseDouble(parts[1]);
            bottom = Double.parseDouble(parts[2]);
            top = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            throw new InvalidArea("Could not parse plot area!");
        }
        return new PlotArea(left, right, bottom, top);
    }
}
