import com.kao.gitreports.DateUtil;
import com.kao.gitreports.GitApi;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;

@Slf4j
public class GitApiTest {

    private static final File TEST_REPOSITORY_PATH = new File("../APP").getAbsoluteFile();

    private static final Date AFTER = DateUtil.parseDate("2018-01-01");
    private static final Date BEFORE = DateUtil.parseDate("2019-01-01");

    private static final String AUTHOR = "Kao";

    private GitApi gitApi;

    @Before
    public void setUp() throws GitAPIException {
        log.info("Using test path: {}", TEST_REPOSITORY_PATH.getAbsoluteFile().toString());
        gitApi = new GitApi(TEST_REPOSITORY_PATH);
    }

    @Test
    public void testGetContributors() throws IOException, GitAPIException {
        Stream<String> contributorsStream = gitApi.getContributors(AFTER, BEFORE);
        List<String> contributors = contributorsStream.collect(Collectors.toList());

        assertTrue(contributors.size() > 0);

        log.info("Contributors: {}", contributors);
        assertTrue(contributors.contains("KarolAntczak"));
    }

    @Test
    public void testGetCommits() throws IOException, GitAPIException {
        Stream<RevCommit> commitStream = gitApi.getCommits(AFTER, BEFORE, AUTHOR);

        assertTrue(commitStream.count() > 0);
    }
}
