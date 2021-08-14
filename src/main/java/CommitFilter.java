import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import java.io.IOException;

public class CommitFilter extends RevFilter {

    private boolean excludeMerges;

    private CommitFilter() {

    }

    @Override
    public boolean include(final RevWalk walker, final RevCommit cmit) throws StopWalkException, MissingObjectException,
                                                                              IncorrectObjectTypeException,
                                                                              IOException {
        boolean result = true;
        if (this.excludeMerges) {
            result = result && cmit.getParentCount() < 2;
        }
        return result;
    }

    @Override
    public RevFilter clone() {
        return this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CommitFilter filter;

        public Builder() {
            this.filter = new CommitFilter();
        }

        public CommitFilter build() {
            return this.filter;
        }

        public Builder noMerges() {
            this.filter.excludeMerges = true;
            return this;
        }

    }

}
