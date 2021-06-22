package com.strawci.ci.cli;

import com.strawci.ci.Server;
import com.strawci.ci.commands.Command;
import com.strawci.ci.commands.ExecutionContext;

public class Console {
    
    private boolean active;
    private final Server server;
    private final Thread thread;

    public Console (final Server server) {
        this.active = false;
        this.server = server;
        this.thread = new Thread(new ConsoleRunnable(this));
    }

    public void runCommand (final String input) {
        this.runCommand(input, ExecutionContext.createDefault());
    }

    public void runCommand (final String input, final ExecutionContext ctx) {
        final String[] parts = input.split(" ");
        final int length = parts.length - 1;
        final String name = parts[0];
        final String[] args = new String[length];

        System.arraycopy(parts, 1, args, 0, length);

        final Command command = this.server.getCommandManager().fetchCommand(name);
        if (command != null) {
            command.handle(args, ctx);
        } else {
            System.out.println("Unknown command \"" + name + "\"");
        }
    }

    public boolean isActive () {
        return this.active;
    }

    public void start () {
        this.active = true;
        this.thread.start();
    }

    public void stop () {
        this.active = false;
    }
}
