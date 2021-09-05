package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.algorithm.AbstractAlgorithm;
import com.svnlib.gitcouplingtool.model.Artifact;
import teetime.stage.basic.AbstractFilter;

import java.util.Collection;
import java.util.LinkedList;

public class AlgorithmStage extends AbstractFilter<Collection<Artifact>> {

    private final AbstractAlgorithm algorithm;

    public AlgorithmStage(final AbstractAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void execute(final Collection<Artifact> artifacts) throws Exception {
        this.algorithm.addChangedArtifacts(new LinkedList<>(artifacts));
        this.outputPort.send(artifacts);
    }

}
