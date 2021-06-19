package com.strawci.ci.commands.server;

import com.strawci.ci.CI;
import com.strawci.ci.commands.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop", "Stop the application");
    }

    @Override
    public boolean handle(String[] args) {
        CI.getServer().stop();
        return true;
    }
    
}
