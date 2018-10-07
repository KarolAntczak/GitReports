package com.kao.gitreports;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class GitApi {

    private Git git;

    public GitApi(File repositoryPath) throws GitAPIException {
        git = Git.init().setDirectory(repositoryPath).call();
    }

    public GitApi(File repositoryPath, String username, String password) throws GitAPIException {
        log.info("Updating repo... (this may take some time)");
        git = Git.init().setDirectory(repositoryPath).call();
        git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .call();
    }

    public GitApi(File repositoryPath, URL repoUrl, String username, String password) throws GitAPIException, URISyntaxException {
        log.info("Cloning repo... (this may take some time)");
        git = Git.cloneRepository().setDirectory(repositoryPath).setURI(String.valueOf(repoUrl.toURI()))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
    }

    private Stream<RevCommit> getCommits() throws IOException, GitAPIException {
        LogCommand command = git.log().all();
        return StreamSupport.stream(command.call().spliterator(), false);
    }

    private Stream<RevCommit> getCommits(Date after, Date before) throws IOException, GitAPIException {
        return getCommits()
                .filter(revCommit -> revCommit.getAuthorIdent().getWhen().after(after))
                .filter(revCommit -> revCommit.getAuthorIdent().getWhen().before(before));
    }

    public Stream<RevCommit> getCommits(Date after, Date before, String author) throws IOException, GitAPIException {
        return  getCommits(after, before)
                .filter(revCommit -> revCommit.getAuthorIdent().getName().equalsIgnoreCase(author));
    }

    public Stream<String> getContributors(Date after, Date before) throws IOException, GitAPIException {
        return getCommits(after, before).map( revCommit -> revCommit.getAuthorIdent().getName()).distinct();
    }

    public void writeDiff(Date after, Date before, String author, Path outputDir) throws IOException, GitAPIException {
        getCommits(after, before, author)
                .filter(commit -> ! commit.getShortMessage().contains("Merge branch"))
                .forEach(commit -> {
            try (OutputStream outputStream = Files.newOutputStream(outputDir.resolve(commit.name() + ".diff"))) {
                writeDiff(commit, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private void writeDiff(RevCommit commit, OutputStream outputStream) {
        if (commit.getParentCount()==0) {
            log.warn("Found a commit without parent: {}", commit.getName());
            return;
        }
        RevCommit diffWith = commit.getParent(0);
        try (DiffFormatter diffFormatter = new DiffFormatter(outputStream)) {
            diffFormatter.setRepository(git.getRepository());
            for (DiffEntry entry : diffFormatter.scan(diffWith, commit)) {
                diffFormatter.format(diffFormatter.toFileHeader(entry));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
