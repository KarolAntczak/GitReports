package com.kao.gitreports;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

@Slf4j
public class GitReport {

    private GitApi gitApi;

    public GitReport(File repositoryPath) throws GitAPIException {
        gitApi = new GitApi(repositoryPath);
    }

    public File createActivityReport(Date after, Date before, String user) throws IOException, GitAPIException {

        String afterString = DateUtil.toShortString(after);
        String beforeString = DateUtil.toShortString(before);

        log.info("Retrieving commits for user {} after {} before {} ...", user, afterString, beforeString);

        Stream<RevCommit> commitStream = gitApi.getCommits(after, before, user);

        File reportFile = new File(String.format("Activity report %s %s - %s.csv", user, afterString, beforeString));

        log.info("Creating file '{}'...", reportFile);

        CsvFileWriter writer = new CsvFileWriter(reportFile);
        log.info("Writing commits to file...");

        writer.writeLine("Commit ID", "Author", "Date", "Commit Message");
        commitStream.forEach(commit -> {
            try {
                writer.writeLine(
                        commit.getId().name(),
                        commit.getAuthorIdent().getName(),
                        DateUtil.toCsvString(commit.getAuthorIdent().getWhen()),
                        commit.getShortMessage());
            } catch (IOException e) {
               log.error(e.getMessage(), e);
            }
        });
        writer.close();
        log.info("Success!");
        return reportFile;
    }

}
