package com.strawci.ci.commands.server;

import com.strawci.ci.CI;
import com.strawci.ci.commands.Command;
import com.strawci.ci.commands.ExecutionContext;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "Stop the application");
    }

    @Override
    public boolean handle(final String[] args, final ExecutionContext ctx) {
        CI.getServer().stop();
        return true;
    }
    
}
