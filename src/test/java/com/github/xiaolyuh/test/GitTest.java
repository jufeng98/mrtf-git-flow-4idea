package com.github.xiaolyuh.test;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Logger;

@Ignore
public class GitTest {
    private final Logger logger = Logger.getLogger(GitTest.class.getName());

    @Test
    public void push() throws Exception {
        var file = new File("");
        logger.warning(file.getAbsolutePath());

        execCommand("cmd /c git push");
    }

    @Test
    public void pull() throws Exception {
        var file = new File("");
        logger.warning(file.getAbsolutePath());

        execCommand("cmd /c git pull");
    }

    private void execCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();

        var res = getRes(process);
        logger.warning("result:$res");

        if (res.contains("fatal")) {
            execCommand(command);
        }
    }

    private String getRes(Process process) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        var line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }

        return stringBuilder.toString();
    }
}
