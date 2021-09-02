package com.svnlib.gitcouplingtool;

import com.svnlib.gitcouplingtool.commands.AnalyseCommand;
import picocli.CommandLine;

public class Main {

    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new AnalyseCommand()).setUsageHelpAutoWidth(true).execute(args);
        System.exit(exitCode);
    }

}
