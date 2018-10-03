import com.kao.gitreports.GitReports;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;

@Slf4j
public class GitReportsTest {

    private static final File TEST_REPOSITORY_PATH = new File("").getAbsoluteFile();

    private GitReports gitReports;

    @Before
    public void setUp() throws GitAPIException {
        log.info("Using test path: {}", TEST_REPOSITORY_PATH.getAbsoluteFile().toString());
        gitReports = new GitReports(TEST_REPOSITORY_PATH);
    }


    @Test
    public void testGetContributors() throws IOException, GitAPIException {
        Stream<String> contributorsStream = gitReports.getContributors();
        List<String> contributors = contributorsStream.collect(Collectors.toList());

        assertTrue(contributors.size() > 0);

        assertTrue(contributors.contains("Kao"));

        log.info("Contributors: {}", contributors);
    }
}
