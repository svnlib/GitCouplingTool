package com.svnlib.gitcouplingtool.commands;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.algorithm.Artifact;
import com.svnlib.gitcouplingtool.algorithm.ArtifactStore;
import com.svnlib.gitcouplingtool.algorithm.CouplingAlgorithm;
import com.svnlib.gitcouplingtool.algorithm.UndirectedRawCounting;
import com.svnlib.gitcouplingtool.model.Algorithm;
import com.svnlib.gitcouplingtool.pipeline.AnalysePipeline;
import com.svnlib.gitcouplingtool.pipeline.CommitCollectionPipeline;
import com.svnlib.gitcouplingtool.util.GitUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Command(name = "analyse", description = "Performing the coupling algorithm on a given git repository.")
public class AnalyseCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "The path to the git repository")
    private File path;

    @Option(names = {
            "-e",
            "--edges"
    }, description = "The number of edges with the highest coupling between files to export.")
    private int edgeCount = 100;

    @Option(names = { "-a", "--algorithm" }, description = "URC or DRC")
    private Algorithm algorithm = Algorithm.URC;

    @Option(names = { "-o", "--output" }, description = "The path to a file where to save the results")
    public File output = new File(System.getProperty("user.dir") + "/result.json");

    @Option(names = { "-b", "--branch" }, description = "Begin traversal at a specific branch instead of HEAD")
    public String branch = "HEAD";

    @Option(names = {
            "--file-type"
    }, description = "Filter files for a given file suffix e.g. \".cpp\" or \".java\"")
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
    }, description = "Combine consecutive commits in a given interval in seconds")
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

        final List<List<DiffEntry>> commits = getCommits();
        System.gc();
        final Set<Artifact> artifacts = initializeStoreAndGetArtifacts();
        System.gc();

        final CouplingAlgorithm algorithm;
        switch (Config.algorithm) {
            case URC:
                algorithm = new UndirectedRawCounting(artifacts);
                break;
            case DRC:
                algorithm = new UndirectedRawCounting(artifacts);
                break;
            default:
                algorithm = new UndirectedRawCounting(artifacts);
        }

        performAlgorithm(commits, algorithm);

        final BufferedWriter writer =
                new BufferedWriter(new FileWriter(Config.output, StandardCharsets.UTF_8, false));
        algorithm.execute();
        algorithm.export(writer);
        writer.close();

        return 0;
    }

    private void performAlgorithm(final List<List<DiffEntry>> commits, final CouplingAlgorithm algorithm) {
        final AnalysePipeline analysePipeline = new AnalysePipeline(commits, algorithm);
        analysePipeline.execute();
    }

    private Set<Artifact> initializeStoreAndGetArtifacts() throws IOException {
        Collection<String> paths = GitUtils.getFilesAtCommit(GitUtils.getFirstCommitFromConfig());
        if (Config.fileTypes != null && Config.fileTypes.size() > 0) {
            paths = paths.stream().filter(path -> {
                for (final String fileType : Config.fileTypes) {
                    if (path.endsWith(fileType)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }
        ArtifactStore.INSTANCE.initialize(paths);
        return ArtifactStore.INSTANCE.getArtifacts();
    }

    private List<List<DiffEntry>> getCommits() throws IOException {
        final CommitCollectionPipeline commitCollectionPipeline = new CommitCollectionPipeline();
        commitCollectionPipeline.execute();
        return commitCollectionPipeline.getCommits();
    }

    private void buildConfig() throws IOException {
        Config.edgeCount = this.edgeCount;
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
