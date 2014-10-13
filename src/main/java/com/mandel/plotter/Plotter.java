package com.mandel.plotter;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

import org.apache.commons.cli.ParseException;

import com.mandel.colorizer.*;
import com.mandel.sets.*;


/**
* Plotter -- Main class responsible for plotting.
*/
public class Plotter {
    static boolean verbose = false;

    public static void run(String[] args) {

        OptionsBuilder options = new OptionsBuilder();
        options.addOption("h", "display this help menu")
               .addOption("f", "name of the PNG file to generate", "output.png")
               .addOption("set", "name of the set to plot", MandelbrotSet.ID)
               .addOption("resolution", "resolution in pixels per unit", "250.0")
               .addOption("iter", "number of iterations", "80")
               .addOption("area", "area to plot (e.g. \"-1.5;1.5;1;1\"), or \"auto\" for autoscaling", "auto")
               .addOption("color", "plot color, either '#xxxxxx,#xxxxxx' for a bicolor plot or '#xxxxxx..#xxxxxx' for a gradient", "#000000..#ffffff")
               .addOption("param", "extra parameter defining the fractal to plot (e.g. \"-0.4;0.6\" to define Julia set parameter)", "")
               .addOption("transformation", "custom transformation to be used instead of `z^2 + c`", "")
               .addOption("j", "control parallelism (number of threads)", "4")
               .addOption("v", "verbose mode");

        try {
            options.parse(args);
        } catch (ParseException e) {
            fail(e.getMessage());
            return;
        }

        verbose = options.getBool("v");

        if (options.getBool("h")) {
            options.showHelp("java -jar Mandel.jar",
                             "",
                             "\nExample:\n java -jar Mandel.jar -f julia.png -set julia -param \"-0.4;0.6\" -resolution 800"
                             );
            return;
        }

        Float res = options.getFloat("resolution");
        if (res == null) {
            fail("could not parse supplied value for 'resolution'");
            return;
        }

        Integer iterations = options.getInteger("iter");
        if (iterations == null) {
            fail("could not parse supplied value for 'iter'");
            return;
        }

        Integer numThreads = options.getInteger("j");
        if (numThreads == null) {
            fail("could not parse supplied value for 'j'");
            return;
        }

        try {
            BaseSet set = SetBuilder.build(options.get("set"),
                                           options.get("transformation"),
                                           options.get("param"),
                                           iterations);
            PlotArea area = (options.get("area").equals("auto"))
                                ? set.getDefaultArea() : PlotArea.parse(options.get("area"));

            Colorizer colorizer = ColorizerBuilder.parse(options.get("color"));
            plot(set, area, res, colorizer, options.get("f"), numThreads);

        } catch (IllegalArgumentException e) {
            fail(e.getMessage());
            return;
        }
    }

    public static void fail(String msg) {
        System.err.println("Error: " + msg);
    }

    public static void info(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    public static void info(char c) {
        if (verbose) {
            System.out.print(c);
        }
    }

    private static void plot(BaseSet set, PlotArea area, float res,
                             Colorizer colorizer, String f, int numThreads) {
        info("Plotting " + set + "...");

        long startedAt = System.currentTimeMillis();

        int width = (int)Math.floor((area.right - area.left) * res);
        int height = (int)Math.floor((area.top - area.bottom) * res);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        PlotTask task = new PlotTask(set, area, colorizer, img, height, height, width, 1);

        info(String.format("Computing plot with %d threads...", numThreads));
        final List<String> threadErrs = task.startParallel(numThreads);

        if (!threadErrs.isEmpty()) {
            // Just display the first error
            fail(threadErrs.get(0));
            return;
        }

        info("\nGenerating output file...");

        File fil = new File(f);
        try {
            ImageIO.write(img, "png", fil);
        } catch (IOException e) {
            fail("could not generate image file\n" + e.getMessage());
            return;
        }

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.open(fil);
                } catch (IOException e) {
                    // It's fine - happens in headless mode
                }
            }
        }

        System.out.println("Result saved in " + fil);
        info(String.format("Plot ended in %d ms",
             System.currentTimeMillis() - startedAt));
    }
}
