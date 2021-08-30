package com.svnlib.gitcouplingtool.pipeline.stages;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import teetime.stage.basic.AbstractFilter;

import java.sql.Timestamp;
import java.time.LocalDate;

public class CommitFilter extends AbstractFilter<RevCommit> {

    @Override
    protected void execute(final RevCommit element) {
        if (checkFilter(element)) {
            this.outputPort.send(element);
        }
    }

    private boolean checkFilter(final RevCommit commit) {
        if (!Config.includeMergeCommits && commit.getParentCount() > 1) {
            return false;
        }

        if (Config.toDate != null || Config.fromDate != null) {
            final LocalDate commitTime = new Timestamp(commit.getCommitTime()).toLocalDateTime().toLocalDate();
            if (Config.toDate != null && commitTime.isAfter(Config.toDate)) {
                return false;
            }
            if (Config.fromDate != null && commitTime.isBefore(Config.fromDate)) {
                return false;
            }
        }

        if (Config.author != null) {
            final PersonIdent authorIdent = commit.getAuthorIdent();
            final String      email       = authorIdent.getEmailAddress();
            final String      name        = authorIdent.getName();

            if (!Config.author.equalsIgnoreCase(email) && !Config.author.equalsIgnoreCase(name)) {
                return false;
            }
        }

        return true;
    }

}
