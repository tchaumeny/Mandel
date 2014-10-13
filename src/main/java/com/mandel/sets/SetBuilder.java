package com.mandel.sets;


public class SetBuilder {

    public static class InvalidSetError extends IllegalArgumentException {
        public InvalidSetError(String message) {
            super(message);
        }
    }

    public static BaseSet build(String set, String raw_transform, String param, int iterations) {

        BaseTransform transform;

        if (raw_transform.equals("")) {
            transform = new MandelbrotTransform();
        } else {
            transform = new CustomTransform(raw_transform);
        }

        switch (set.toLowerCase()) {

            case MandelbrotSet.ID:

                if (!param.equals("")) {
                    throw new InvalidSetError("Mandelbrot set does not take a parameter!");
                }
                return new MandelbrotSet(transform, iterations);

            case JuliaSet.ID:

                final double x, y;
                if (param.equals("")) {
                    // That one is nice :)
                    x = -0.74543;
                    y = 0.11301;
                } else {
                    String[] parts = param.split(";");
                    if (parts.length != 2) {
                        throw new InvalidSetError(String.format("Invalid parameter '%s' for Julia set!", param));
                    }
                    try {
                        x = Double.parseDouble(parts[0]);
                        y = Double.parseDouble(parts[1]);
                    } catch (NumberFormatException e) {
                        throw new InvalidSetError(String.format("Invalid parameter '%s' for Julia set!", param));
                    }
                }
                return new JuliaSet(transform, x, y, iterations);
        }

        throw new InvalidSetError(String.format("Unrecognized set '%s'!", set));
    }
}
