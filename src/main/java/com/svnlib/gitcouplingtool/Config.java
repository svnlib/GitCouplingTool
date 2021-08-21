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

        final StringBuilder sb = new StringBuilder();

        sb.append(colorize("================================================\n", Attribute.BOLD()))
          .append(colorize("Running with config:\n", Attribute.BOLD()))
          .append(colorize("------------------------------------------------\n", Attribute.BOLD()))
          .append(colorize("Performance\n", Attribute.UNDERLINE()))
          .append("Threads:                ").append(threads).append("\n")
          .append(colorize("\nGeneral\n", Attribute.UNDERLINE()))
          .append("Repository:             ").append(git.getRepository().getDirectory()).append("\n")
          .append("Output:                 ").append(output).append("\n")
          .append("Algorithm:              ").append(algorithm).append("\n")
          .append("Combine consecutive:    ").append(combineConsecutive).append("\n")
          .append("Follow renames:         ").append(followRenames).append("\n")
          .append(colorize("\nFiltering\n", Attribute.UNDERLINE()))
          .append("Branch:                 ").append(branch).append("\n")
          .append("File types:             ").append(fileTypes).append("\n")
          .append("Author:                 ").append(author).append("\n")
          .append("From date:              ").append(fromDate).append("\n")
          .append("To date:                ").append(toDate).append("\n")
          .append("From commit:            ").append(fromCommit).append("\n")
          .append("To commit:              ").append(toCommit).append("\n")
          .append("From tag:               ").append(fromTag).append("\n")
          .append("To tag:                 ").append(toTag).append("\n")
          .append("Exclude large commits:  ").append(excludeLargeCommits).append("\n")
          .append("Include merge commits:  ").append(includeMergeCommits).append("\n")
          .append(colorize("\nValues\n", Attribute.UNDERLINE()))
          .append("Large commit threshold: ").append(largeThreshold).append("\n")
          .append(colorize("================================================\n\n", Attribute.BOLD()));

        System.out.print(sb);
    }

}
