package com.svnlib.gitcouplingtool;

import com.svnlib.gitcouplingtool.commands.Analyser;
import picocli.CommandLine;

public class Main {

    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Analyser()).setUsageHelpAutoWidth(true).execute(args);
        System.exit(exitCode);
    }

}
