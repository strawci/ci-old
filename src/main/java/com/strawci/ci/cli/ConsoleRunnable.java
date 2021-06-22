package com.strawci.ci.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleRunnable implements Runnable {

    private final Console console;

    public ConsoleRunnable(final Console console) {
        this.console = console;
    }

    @Override
    public void run() {
        while (this.console.isActive()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;

            try {
                line = reader.readLine();
                if (this.console.isActive() && !line.isEmpty()) {
                    this.console.runCommand(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
