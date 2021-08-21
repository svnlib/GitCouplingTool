package com.svnlib.gitcouplingtool.commands;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.model.Algorithm;
import com.svnlib.gitcouplingtool.parsing.CommitParser;
import com.svnlib.gitcouplingtool.parsing.CommitReader;
import org.eclipse.jgit.api.Git;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "analyse", description = "Performing the coupling algorithm on a given git repository.")
public class Analyser implements Callable<Integer> {

    @Parameters(index = "0", description = "The path to the git repository")
    private File path;

    @Option(names = { "-a", "--algorithm" }, description = "URC or DRC")
    private Algorithm algorithm = Algorithm.URC;

    @Option(names = { "-t", "--threads" }, description = "The number of threads to use")
    private int threads = Math.min(Runtime.getRuntime().availableProcessors(), 12);

    @Option(names = { "-o", "--output" }, description = "The path to a file where to save the results")
    public File output = new File(System.getProperty("user.dir") + "/result.json");

    @Option(names = { "-b", "--branch" }, description = "Begin traversal at a specific branch instead of HEAD")
    public String branch;

    @Option(names = {
            "--file-type"
    }, description = "Filter files for a given file extension e.g. \".cpp\" or \".java\"")
    public List<String> fileTypes;

    @Option(names = { "--author" }, description = "Filter commits for a given author name or email")
    public String author;

    @Option(names = { "--from-date" }, description = "Filter commits to be committed after a given date")
    public LocalDate fromDate;

    @Option(names = { "--to-date" }, description = "Filter commits to be committed before a given date")
    public LocalDate toDate;

    @Option(names = { "--from-commit" }, description = "Begin traversal at a specific commit instead of HEAD")
    public String fromCommit;

    @Option(names = { "--to-commit" }, description = "End traversal at a specific commit instead of initial commit")
    public String toCommit;

    @Option(names = { "--from-tag" }, description = "Begin traversal at a specific tag instead of HEAD")
    public String fromTag;

    @Option(names = { "--to-tag" }, description = "End traversal at a specific tag instead of initial commit")
    public String toTag;

    @Option(names = { "--large-threshold" }, description = "The number of file changes to consider a commit large")
    public int largeThreshold = 50;

    @Option(names = { "--no-large" }, description = "Exclude large commits")
    public boolean excludeLargeCommits = false;

    @Option(names = { "--merges" }, description = "Include merge commits")
    public boolean includeMergeCommits = false;

    @Option(names = {
            "-cc",
            "--combine-consecutive"
    }, description = "Combine consecutive commits in a given interval e.g. \"1d\" or \"5m\"")
    public int combineConsecutive;

    @Option(names = {
            "-r",
            "--follow-renames"
    }, description = "Increases data quality but adds performance overhead")
    public boolean followRenames;

    @Override
    public Integer call() throws Exception {
        buildConfig();
        Config.print();

        final CommitReader       reader = new CommitReader();
        final List<CommitParser> parser = new LinkedList<>();
        for (int i = 0; i < this.threads - 2; i++) {
            final CommitParser commitParser = new CommitParser(reader.getQueue());
            commitParser.start();
            parser.add(commitParser);
        }

        reader.start();
        reader.join();
        for (final CommitParser commitParser : parser) {
            commitParser.join();
        }
        return 0;
    }

    private void buildConfig() throws IOException {
        Config.threads = this.threads;
        Config.algorithm = this.algorithm;
        Config.output = this.output;
        Config.branch = this.branch;
        Config.fileTypes = this.fileTypes;
        Config.author = this.author;
        Config.fromDate = this.fromDate;
        Config.toDate = this.toDate;
        Config.fromCommit = this.fromCommit;
        Config.toCommit = this.toCommit;
        Config.fromTag = this.fromTag;
        Config.toTag = this.toTag;
        Config.largeThreshold = this.largeThreshold;
        Config.excludeLargeCommits = this.excludeLargeCommits;
        Config.includeMergeCommits = this.includeMergeCommits;
        Config.combineConsecutive = this.combineConsecutive;
        Config.followRenames = this.followRenames;

        Config.git = Git.open(this.path);
    }

}
