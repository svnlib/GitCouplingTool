package com.svnlib.gitcouplingtool;

import com.diogonunes.jcolor.Attribute;
import com.svnlib.gitcouplingtool.model.Algorithm;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static com.diogonunes.jcolor.Ansi.colorize;

public class Config {

    public static Git git;

    public static int          threads;
    public static Algorithm    algorithm;
    public static File         output;
    public static String       branch;
    public static List<String> fileTypes;
    public static String       author;
    public static LocalDate    fromDate;
    public static LocalDate    toDate;
    public static String       fromCommit;
    public static String       toCommit;
    public static String       fromTag;
    public static String       toTag;
    public static int          largeThreshold;
    public static boolean      excludeLargeCommits;
    public static boolean      includeMergeCommits;
    public static int          combineConsecutive;
    public static boolean      followRenames;

    public static void print() {
        final String s = colorize("================================================\n", Attribute.BOLD()) +
                         colorize("Running with config:\n", Attribute.BOLD()) +
                         colorize("------------------------------------------------\n", Attribute.BOLD()) +
                         colorize("Performance\n", Attribute.UNDERLINE()) +
                         "Threads:                " + threads + "\n" +
                         colorize("\nGeneral\n", Attribute.UNDERLINE()) +
                         "Repository:             " + git.getRepository().getDirectory() + "\n" +
                         "Output:                 " + output + "\n" +
                         "Algorithm:              " + algorithm + "\n" +
                         "Combine consecutive:    " + combineConsecutive + "\n" +
                         "Follow renames:         " + followRenames + "\n" +
                         colorize("\nFiltering\n", Attribute.UNDERLINE()) +
                         "Branch:                 " + printNull(branch) + "\n" +
                         "File types:             " + printNull(fileTypes) + "\n" +
                         "Author:                 " + printNull(author) + "\n" +
                         "From date:              " + printNull(fromDate) + "\n" +
                         "To date:                " + printNull(toDate) + "\n" +
                         "From commit:            " + printNull(fromCommit) + "\n" +
                         "To commit:              " + printNull(toCommit) + "\n" +
                         "From tag:               " + printNull(fromTag) + "\n" +
                         "To tag:                 " + printNull(toTag) + "\n" +
                         "Exclude large commits:  " + excludeLargeCommits + "\n" +
                         "Include merge commits:  " + includeMergeCommits + "\n" +
                         colorize("\nValues\n", Attribute.UNDERLINE()) +
                         "Large commit threshold: " + largeThreshold + "\n" +
                         colorize("================================================\n\n", Attribute.BOLD());
        System.out.print(s);
    }

    private static String printNull(final Object o) {
        if (o == null) {
            return "-";
        } else {
            return o.toString();
        }
    }

}
