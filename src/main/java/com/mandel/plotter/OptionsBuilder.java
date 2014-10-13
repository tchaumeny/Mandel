package com.mandel.plotter;

import java.util.HashMap;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
* OptionsBuilder -- Handle options definition and parsing.
*
* This is a thin wrapper around org.apache.commons.cli with support for default values.
*/
public class OptionsBuilder {
    
    private HashMap<String,PlotOption> optionsMap;
    private CommandLine cmd;
    private Options options;

    class PlotOption {

        public String name, description, defVal;
        public boolean hasVal;

        public PlotOption(String name, String description, boolean hasVal, String defVal) {
            this.name = name;
            this.description = description;
            this.hasVal = hasVal;
            this.defVal = defVal;
        }
    }

    public OptionsBuilder() {
        this.options = new Options();
        this.optionsMap = new HashMap<String,PlotOption>();
    }

    public OptionsBuilder addOption(String name, String description, String defVal) {
        // In our case, options are expecting a value if and only if
        // they have a default value
        description += String.format(" — default \"%s\"", defVal);
        this.optionsMap.put(name, new PlotOption(name, description, true, defVal));
        return this;
    }

    public OptionsBuilder addOption(String name, String description) {
        this.optionsMap.put(name, new PlotOption(name, description, false, null));
        return this;
    }

    public void parse(String[] args) throws ParseException {
        for (PlotOption opt: this.optionsMap.values()) {
            this.options.addOption(opt.name, opt.hasVal, opt.description);
        }
        CommandLineParser parser = new BasicParser();
        this.cmd = parser.parse(this.options, args);
    }

    public String get(String name) {
        if (this.cmd.hasOption(name)) {
            return this.cmd.getOptionValue(name);
        } else if (this.optionsMap.containsKey(name)) {
            return this.optionsMap.get(name).defVal;
        } else {
            return null;
        }
    }

    public boolean getBool(String name) {
        return this.cmd.hasOption(name);
    }

    public Integer getInteger(String name) throws NumberFormatException {
        String s;
        if (this.cmd.hasOption(name)) {
            s = this.cmd.getOptionValue(name);
        } else if (this.optionsMap.containsKey(name)) {
            s = this.optionsMap.get(name).defVal;
        } else {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Float getFloat(String name) throws NumberFormatException {
        String s;
        if (this.cmd.hasOption(name)) {
            s = this.cmd.getOptionValue(name);
        } else if (this.optionsMap.containsKey(name)) {
            s = this.optionsMap.get(name).defVal;
        } else {
            return null;
        }
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void showHelp(String syntax, String header, String footer) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, syntax, header, this.options, footer, true);
    }
}
