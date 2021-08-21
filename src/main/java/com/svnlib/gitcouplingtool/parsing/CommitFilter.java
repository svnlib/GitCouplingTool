package com.svnlib.gitcouplingtool.parsing;

import com.svnlib.gitcouplingtool.Config;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.sql.Timestamp;
import java.time.LocalDate;

public class CommitFilter extends RevFilter {

    public CommitFilter() {

    }

    @Override
    public boolean include(final RevWalk walker, final RevCommit cmit) throws StopWalkException {

        if (!Config.includeMergeCommits) {
            // exclude merges
            if (cmit.getParentCount() > 1) {
                return false;
            }
        }

        if (Config.toDate != null || Config.fromDate != null) {
            final LocalDate commitTime = new Timestamp(cmit.getCommitTime()).toLocalDateTime().toLocalDate();
            if (Config.toDate != null && commitTime.isAfter(Config.toDate)) {
                return false;
            }
            if (Config.fromDate != null && commitTime.isBefore(Config.fromDate)) {
                return false;
            }
        }

        if (Config.author != null) {
            final PersonIdent authorIdent = cmit.getAuthorIdent();
            final String      email       = authorIdent.getEmailAddress();
            final String      name        = authorIdent.getName();

            if (!Config.author.equalsIgnoreCase(email) && !Config.author.equalsIgnoreCase(name)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public RevFilter clone() {
        return this;
    }

}
