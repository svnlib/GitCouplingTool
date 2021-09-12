package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import teetime.stage.basic.AbstractTransformation;

import java.util.LinkedList;
import java.util.List;

public class CombineConsecutiveStage extends AbstractTransformation<RevCommit, List<RevCommit>> {

    List<RevCommit> consecutiveCommits = new LinkedList<>();
    PersonIdent     lastAuthorIdent    = null;

    @Override
    protected void execute(final RevCommit revCommit) throws Exception {

        final PersonIdent authorIdent = revCommit.getAuthorIdent();
        if (this.lastAuthorIdent != null) {
            final long timeBetween =
                    Math.abs(this.lastAuthorIdent.getWhen().getTime() - authorIdent.getWhen().getTime()) / 1000;

            if (timeBetween > Config.combineConsecutive ||
                !authorEqual(authorIdent, this.lastAuthorIdent) ||
                !isParent(this.consecutiveCommits.get(this.consecutiveCommits.size() - 1), revCommit)) {

                this.outputPort.send(this.consecutiveCommits);
                this.consecutiveCommits = new LinkedList<>();
            }
        }
        this.lastAuthorIdent = authorIdent;
        this.consecutiveCommits.add(revCommit);
    }

    @Override
    protected void onTerminating() {
        if (!this.consecutiveCommits.isEmpty()) {
            this.outputPort.send(this.consecutiveCommits);
        }
        super.onTerminating();
    }

    /**
     * Checks if two given {@link PersonIdent} share the same author name and email.
     *
     * @param authorA the first author
     * @param authorB the second author
     *
     * @return {@code true} if the name and email is the same and {@code false} otherwise
     */
    private boolean authorEqual(final PersonIdent authorA, final PersonIdent authorB) {
        if (!authorA.getName().equalsIgnoreCase(authorB.getName())) {
            return false;
        }
        if (!authorA.getEmailAddress().equals(authorB.getEmailAddress())) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether the child's parents include the given parent
     *
     * @param child  the commit whose parents should include the given parent
     * @param parent the commit which should be included
     *
     * @return {@code true} if the child indeed includes the parent in its parents and {@code false} otherwise.
     */
    private boolean isParent(final RevCommit child, final RevCommit parent) {
        if (child.getParentCount() == 0) {
            return false;
        }

        for (int i = 0; i < child.getParentCount(); i++) {
            final RevCommit p = child.getParent(i);
            if (p.getId().equals(parent.getId())) {
                return true;
            }
        }
        return false;
    }

}
