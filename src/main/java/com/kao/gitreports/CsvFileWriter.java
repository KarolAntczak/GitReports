package com.kao.gitreports;


import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class CsvFileWriter implements Closeable {

    private static String DEFAULT_SEPARATOR = ";";

    private BufferedWriter writer;

    private String separator;

    public CsvFileWriter(File file) throws IOException {
        this(file, DEFAULT_SEPARATOR);
    }

    public CsvFileWriter(File file, String separator) throws IOException {
        this.writer = Files.newBufferedWriter(file.toPath(),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
        this.separator = separator;
    }

    public void writeLine(String... values) throws IOException {
        writeLine(Arrays.asList(values));
    }

    public void writeLine(List<String> values) throws IOException {
        this.writer.write(String.join(separator, values));
        this.writer.newLine();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
