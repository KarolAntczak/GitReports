import com.kao.gitreports.DateUtil;
import com.kao.gitreports.GitReport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static junit.framework.TestCase.assertTrue;

@Slf4j
public class GitReportTest {
    private static final File TEST_REPOSITORY_PATH = new File("../APP").getAbsoluteFile();

    private static final Date AFTER = DateUtil.parseDate("2018-01-01");
    private static final Date BEFORE = DateUtil.parseDate("2019-01-01");

    private static final String AUTHOR = "Karol";

    private GitReport gitReport;

    @Before
    public void setUp() throws GitAPIException {
        log.info("Using test path: {}", TEST_REPOSITORY_PATH.getAbsoluteFile().toString());
        gitReport = new GitReport(TEST_REPOSITORY_PATH);
    }

    @Test
    public void testCreateActivityReport() throws IOException, GitAPIException {
        File reportFile = gitReport.createActivityReport(AFTER, BEFORE, AUTHOR);

        assertTrue(reportFile.exists());
        assertTrue(reportFile.isFile());
    }

}

