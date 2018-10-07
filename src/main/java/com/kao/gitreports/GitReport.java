package com.kao.gitreports;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GitReport {

    private GitApi gitApi;

    public GitReport(GitApi gitApi) {
        this.gitApi = gitApi;
    }

    public List<File> createActivityReport(Date after, Date before) {
        return getUserList(after, before).stream().map(contributor -> {
            try {
                return createActivityReport(after, before, contributor);
            } catch (IOException | GitAPIException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }).collect(Collectors.toList());
    }

    public File createActivityReport(Date after, Date before, String user) throws IOException, GitAPIException {

        String afterString = DateUtil.toShortString(after);
        String beforeString = DateUtil.toShortString(before);

        log.info("Retrieving commits for user {} after {} before {} ...", user, afterString, beforeString);

        Stream<RevCommit> commitStream = gitApi.getCommits(after, before, user);

        File reportFile = new File(String.format("activity reports/%s %s - %s.csv", user, afterString, beforeString));

        Files.createDirectories(reportFile.toPath().getParent());

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

    public List<File> createDiffReports(Date after, Date before) {
        return getUserList(after, before).stream().map(contributor -> createDiffsReport(after, before, contributor)).collect(Collectors.toList());
    }

    public File createDiffsReport(Date after, Date before, String user)  {

        String afterString = DateUtil.toShortString(after);
        String beforeString = DateUtil.toShortString(before);

        log.info("Retrieving diffs for user {} after {} before {} ...", user, afterString, beforeString);

        File reportDir = new File(String.format("diffs/%s %s - %s", user, afterString, beforeString));
        try {
            Files.createDirectories(reportDir.toPath());
            gitApi.writeDiff(after, before, user, reportDir.toPath());
        } catch (IOException | GitAPIException e) {
            log.error(e.getMessage(), e);
            return null;
        }

        log.info("Success!");
        return reportDir;
    }

    private List<String> getUserList(Date after, Date before) {
        String afterString = DateUtil.toShortString(after);
        String beforeString = DateUtil.toShortString(before);

        log.info("Retrieving user list after {} before {} ...",  afterString, beforeString);

        Stream<String> contributorsStream;
        try {
            contributorsStream = gitApi.getContributors(after, before);
        } catch (IOException | GitAPIException e) {
            log.error(e.getMessage(), e);
            return  null;
        }
        List<String> contributors = contributorsStream.collect(Collectors.toList());

        log.info("Found {} contributors", contributors.size());

        return contributors;
    }

}
