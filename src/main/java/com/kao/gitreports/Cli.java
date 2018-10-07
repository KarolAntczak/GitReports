package com.kao.gitreports;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import joptsimple.util.DateConverter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import static com.kao.gitreports.DateUtil.SHORT_DATE_FORMAT;


@Slf4j
public class Cli {

    public static void main(String[] args) {

        OptionParser parser = new OptionParser();

        OptionSpecBuilder activity = parser.accepts("activity", "Generate activity reports");
        OptionSpecBuilder diffs = parser.accepts("diffs", "Generate diffs").requiredUnless(activity);
        activity.requiredUnless(diffs);

        DateConverter dateConverter = DateConverter.datePattern(SHORT_DATE_FORMAT.toPattern());
        ArgumentAcceptingOptionSpec<Date> from = parser.accepts("from", "From date").withRequiredArg().ofType(Date.class).withValuesConvertedBy(dateConverter).required();
        ArgumentAcceptingOptionSpec<Date> to = parser.accepts("to", "To date").withRequiredArg().ofType(Date.class).withValuesConvertedBy(dateConverter).required();

        OptionSpec<URL> urlOption = parser.nonOptions("repository URL").ofType(URL.class);
        ArgumentAcceptingOptionSpec<String> user = parser.accepts("user", "Git username").withRequiredArg().ofType(String.class).required();
        ArgumentAcceptingOptionSpec<String> pass = parser.accepts("password", "Git password").withRequiredArg().ofType(String.class).required();


        parser.accepts("help").forHelp();

        OptionSet options = null;

        if (args.length == 0) {
            try {
                parser.printHelpOn(System.out);
                System.exit(0);
            } catch (IOException e) {
                log.error(e.getMessage());
                System.exit(-1);
            }

        }

        try {
            options = parser.parse(args);

        } catch (OptionException e) {
            log.error(e.getMessage());
            System.exit(-1);
        }

        URL gitRepUrl = null;
        try {
            gitRepUrl = options.valueOf(urlOption);
        } catch (OptionException e) {
            log.error("Malformed URL");
            System.exit(-1);
        }

        if (gitRepUrl == null) {
            log.error("Missing URL");
            System.exit(-1);
        }

        GitReport gitReport = null;

        String userString = options.valueOf(user);
        String passString = options.valueOf(pass);

        try {

            Path repoPath = Paths.get("repo");

            if (Arrays.asList(repoPath.toFile().list()).contains(".git")) {
                gitReport = new GitReport(new GitApi(repoPath.toFile().getAbsoluteFile(), userString, passString));
            } else {
                gitReport = new GitReport(new GitApi(repoPath.toFile().getAbsoluteFile(), gitRepUrl, userString, passString));
            }
        } catch (GitAPIException | URISyntaxException e) {
           log.error(e.getMessage(), e);
           System.exit(-1);
        }

        Date fromDate = options.valueOf(from);
        Date toDate = options.valueOf(to);

        if (options.has(diffs)) {
            gitReport.createDiffReports(fromDate, toDate);
        }
        if (options.has(activity)) {
            gitReport.createActivityReport(fromDate, toDate);
        }
    }
}
