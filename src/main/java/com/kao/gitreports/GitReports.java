package com.kao.gitreports;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class GitReports {

    private Git git;

    public GitReports(File repositoryPath) throws GitAPIException {
        git = Git.init().setDirectory(repositoryPath).call();
    }

    private Stream<RevCommit> getCommits() throws IOException, GitAPIException {
        LogCommand command = git.log().all();
        return StreamSupport.stream(command.call().spliterator(), false);
    }

    public Stream<RevCommit> getCommits(Date after, Date before) throws IOException, GitAPIException {
        return getCommits()
                .filter(revCommit -> revCommit.getAuthorIdent().getWhen().after(after))
                .filter(revCommit -> revCommit.getAuthorIdent().getWhen().before(before));
    }

    public Stream<String> getContributors(Date after, Date before) throws IOException, GitAPIException {
        return getCommits(after, before).map( revCommit -> revCommit.getAuthorIdent().getName()).distinct();
    }

}
