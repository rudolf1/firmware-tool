package org.conservify.firmwaretool.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RunCommand {
    private static final Logger logger = LoggerFactory.getLogger(RunCommand.class);

    public static String run(String cmd, File workingDir, Consumer<String> progress) {
        String[] args = CommandLineParser.translateCommandLine(cmd);
        ArrayList<String> fixed = new ArrayList<String>();
        for (String arg : args) {
            fixed.add(DoubleQuotedArgumentsOnWindowsCommandLine.fixArgument(arg));
        }
        return run(fixed.toArray(new String[0]), workingDir, progress);
    }

    static String run(String[] cmd, File workingDir, Consumer<String> progress) {
        try {
            List<String> args = Arrays.asList(cmd);
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;

            while ((line = reader.readLine()) != null) {
                progress.accept(line);
            }

            process.waitFor();

            logger.info("Exited {}", process.exitValue());

            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
