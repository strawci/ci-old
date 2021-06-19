package com.strawci.ci.commands;

import java.util.ArrayList;
import java.util.List;

import com.strawci.ci.commands.server.StopCommand;
import com.strawci.ci.plugins.Plugin;

public class CommandManager {
    private final List<Command> registeredCommands;
    
    public CommandManager () {
        this.registeredCommands = new ArrayList<>();

        this.registeredCommands.add(new StopCommand());
    }

    public void registerCommand (final Command command, final Plugin plugin) throws Exception {
        command.setPlugin(plugin);
        this.registeredCommands.add(command);
    }

    public void unregisterByPlugin (final Plugin plugin) {
        for (final Command cmd : this.registeredCommands) {
            if (cmd.getPlugin().equals(plugin)) {
                this.registeredCommands.remove(cmd);
            }
        }
    }

    public Command fetchCommand (final String query) {
        for (final Command command : this.registeredCommands) {
            if (command.match(query)) {
                return command;
            }
        }

        return null;
    }
}