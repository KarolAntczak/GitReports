package com.kao.gitreports;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Cli {

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();

        OptionSpec<File> pathOption = parser.accepts( "path" ).withRequiredArg().ofType( File.class ).required();

        OptionSet options = null;

        try {
            options = parser.parse();
        } catch (OptionException e) {
            log.error(e.getMessage());
            System.exit(-1);
        }

        File gitRepoPath = options.valueOf(pathOption);

        log.info(gitRepoPath.toString());

    }
}
