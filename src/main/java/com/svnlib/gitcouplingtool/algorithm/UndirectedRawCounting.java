package com.svnlib.gitcouplingtool.algorithm;

import com.svnlib.gitcouplingtool.Config;
import com.svnlib.gitcouplingtool.graph.AbstractEdge;
import com.svnlib.gitcouplingtool.graph.UndirectedEdge;
import com.svnlib.gitcouplingtool.graph.UndirectedGraph;
import com.svnlib.gitcouplingtool.graph.io.AbstractExporter;
import com.svnlib.gitcouplingtool.graph.io.GMLExporter;
import com.svnlib.gitcouplingtool.graph.io.JSONExporter;
import com.svnlib.gitcouplingtool.util.ProgressBarUtils;
import com.svnlib.gitcouplingtool.util.PushList;
import me.tongfei.progressbar.ProgressBar;

import java.io.Writer;
import java.util.Collection;
import java.util.Map;

public class UndirectedRawCounting extends CountingAlgorithm {

    private final UndirectedGraph        graph;
    final         PushList<AbstractEdge> edges;

    public UndirectedRawCounting(final Collection<Artifact> artifacts) {
        super(artifacts);
        this.graph = new UndirectedGraph();
        this.edges = new PushList<>(Config.edgeCount);
    }

    @Override
    public void execute() {
        super.execute();

        final ProgressBar pb = ProgressBarUtils.getDefaultBuilder("Building Graph", "Steps")
                                               .setInitialMax(Config.edgeCount).build();

        this.edges.toList().forEach(e -> {
            this.graph.putEdge((UndirectedEdge) e);
            this.graph.addNode(e.getSrc());
            this.graph.addNode(e.getDest());
        });
        pb.close();
    }

    @Override
    protected void addToGraph(final Artifact a, final Artifact b, final int aCount, final int bCount,
                              final int commonCount) {
        final UndirectedEdge edge =
                this.graph.createEdge(a.getOriginalPath(), b.getOriginalPath(), commonCount);
        this.edges.add(edge);
    }

    @Override
    public void export(final Writer writer) throws Exception {
        final AbstractExporter<UndirectedEdge> exporter;
        switch (Config.format) {
            case JSON:
                exporter = getJsonExporter(writer);
                break;
            case GML:
                exporter = getGmlExporter(writer);
                break;
            default:
                return;
        }
        exporter.export(this.graph);
    }

    private JSONExporter<UndirectedEdge> getJsonExporter(final Writer writer) {
        return new JSONExporter<>(writer) {
            @Override
            protected Map<String, Object> edgeAttributes(final UndirectedEdge edge) {
                return Map.of("id",
                              edge.getSrc() + "::" + edge.getDest(),
                              "start",
                              edge.getSrc(),
                              "end",
                              edge.getDest(),
                              "weight",
                              (int) edge.getWeight(),
                              "directed",
                              0);
            }
        };
    }

    private GMLExporter<UndirectedEdge> getGmlExporter(final Writer writer) {
        return new GMLExporter<>(writer) {
            @Override
            protected Map<String, Object> edgeAttributes(final UndirectedEdge edge) {
                return Map.of("source",
                              edge.getSrc(),
                              "target",
                              edge.getDest(),
                              "value",
                              (int) edge.getWeight(),
                              "directed",
                              0);
            }
        };
    }

}
