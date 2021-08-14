import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RepoMiner {

    private final Git        git;
    private final Repository repository;

    public RepoMiner(final String pathToRepo) throws IOException {
        this.git = Git.open(new File(pathToRepo));
        this.repository = this.git.getRepository();
    }

    public Iterable<RevCommit> getCommits() throws GitAPIException {
        return this.git.log().call();
    }

    public Iterable<RevCommit> getCommits(final RevFilter filter) throws GitAPIException {
        return this.git.log().setRevFilter(filter).call();
    }

    public List<DiffEntry> getDiffsFromCommit(final RevCommit commit) throws GitAPIException, IOException {

        final ObjectReader reader = this.repository.newObjectReader();

        final CanonicalTreeParser treeParser       = new CanonicalTreeParser();
        final CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();

        treeParser.reset(reader, commit.getTree());
        parentTreeParser.reset(reader, commit.getParent(0).getTree());

        return this.git.diff()
                       .setNewTree(treeParser)
                       .setOldTree(parentTreeParser)
                       .call();
    }

}
