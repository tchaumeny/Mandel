package com.mandel.plotter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mandel.colorizer.Colorizer;
import com.mandel.sets.BaseSet;


/**
* PlotTask -- A task that plot workers should run in order to build the plot.
*/
class PlotTask implements Runnable {
    private PlotArea area;
    private BufferedImage img;
    private Colorizer colorizer;
    private LinesSet lines;
    private BaseSet set;
    private int height, width, bufferSize;

    /**
    * LinesSet -- A structure allowing simple iteration over the range [0, size -1]
    */
    private static class LinesSet {
        private final int init_size;
        private int pos;

        public LinesSet(int size) {
            this.init_size = size;
            this.pos = 0;
        }

        /**
         * Pop a batch of lines for further processing.
         *
         * This method can be called concurrently by multiple worker threads.
         */
        public synchronized List<Integer> popLines(int count) {
            if (this.pos >= this.init_size) {
                return null;
            }
            List<Integer> L = new ArrayList<Integer>();
            while (L.size() <= count && this.pos != this.init_size) {
                L.add(this.pos);
                this.pos++;
            }
            if (this.init_size >= 100 && this.pos % (this.init_size / 100) == 0) {
                Plotter.info('.');
            }
            return L;
        }
    }

    public PlotTask(BaseSet set, PlotArea area, Colorizer colorizer, BufferedImage img,
                      int linesCnt, int height, int width, int bufferSize) {
        this.area = area;
        this.bufferSize = bufferSize;
        this.colorizer = colorizer;
        this.height = height;
        this.img = img;
        this.lines = new LinesSet(linesCnt);
        this.set = set;
        this.width = width;
    }

    @Override
    public void run() {
        List<Integer> lines;
        while ((lines = this.lines.popLines(this.bufferSize)) != null) {
            for (int i: lines) {
                for (int j = 0; j < this.width; ++j) {
                    float indice = this.set.contains(
                        this.area.left + (this.area.right - this.area.left) * j / this.width,
                        this.area.top + (this.area.bottom - this.area.top) * i / this.height);
                    this.img.setRGB(j, i, colorizer.getColor(indice).getRGB());
                }
            }
        }
    }

    /**
     * Executes the task on multiple threads.
     */
    public List<String> startParallel(int numThreads) {
        Thread[] threads = new Thread[numThreads];

        final List<String> threadErrs = Collections.synchronizedList(new ArrayList<String>());
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                threadErrs.add(e.getMessage());
            }
        });
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(this);
            threads[i].start();
        }
        for (Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                threadErrs.add(e.getMessage());
            }
        }
        return threadErrs;
    }
}
