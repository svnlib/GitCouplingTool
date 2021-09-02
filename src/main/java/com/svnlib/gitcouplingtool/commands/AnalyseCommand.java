package com.svnlib.gitcouplingtool.commands;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.algorithm.AbstractAlgorithm;
import com.svnlib.gitcouplingtool.model.Algorithm;
import com.svnlib.gitcouplingtool.model.Commit;
import com.svnlib.gitcouplingtool.pipeline.AnalysePipeline;
import com.svnlib.gitcouplingtool.pipeline.CommitCollectionPipeline;
import org.eclipse.jgit.api.Git;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "analyse", description = "Performing the coupling algorithm on a given git repository.")
public class AnalyseCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The path to the git repository")
    private File path;

    @Option(names = { "-a", "--algorithm" }, description = "URC or DRC")
    private Algorithm algorithm = Algorithm.URC;

    @Option(names = { "-t", "--threads" }, description = "The number of threads to use")
    private int threads = Runtime.getRuntime().availableProcessors();

    @Option(names = { "-o", "--output" }, description = "The path to a file where to save the results")
    public File output = new File(System.getProperty("user.dir") + "/result.json");

    @Option(names = { "-b", "--branch" }, description = "Begin traversal at a specific branch instead of HEAD")
    public String branch = "HEAD";

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

        final AbstractAlgorithm algorithm = Config.algorithm.getAlgorithm();

        final List<Commit> commits = collectCommits();

        final AnalysePipeline analysePipeline = new AnalysePipeline(commits, algorithm);
        analysePipeline.execute();

        return 0;
    }

    private List<Commit> collectCommits() throws IOException {
        final CommitCollectionPipeline commitCollectionPipeline = new CommitCollectionPipeline();
        commitCollectionPipeline.execute();
        return commitCollectionPipeline.getCommits();
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
