package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.algorithm.Artifact;
import com.svnlib.gitcouplingtool.algorithm.CouplingAlgorithm;
import teetime.stage.basic.AbstractFilter;

import java.util.Collection;

public class AlgorithmStage extends AbstractFilter<Collection<Artifact>> {

    private final CouplingAlgorithm algorithm;

    public AlgorithmStage(final CouplingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void execute(final Collection<Artifact> artifacts) throws Exception {
        this.algorithm.changedArtifacts(artifacts);
        this.outputPort.send(artifacts);
    }

}
