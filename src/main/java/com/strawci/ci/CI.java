package com.strawci.ci;

import com.strawci.ci.cli.Console;
import com.strawci.ci.commands.CommandManager;
import com.strawci.ci.event.Event;
import com.strawci.ci.plugins.PluginManager;

public class CI {
    private static Server server;

    public static void setServer (final Server server) throws Exception {
        if (CI.server == null) {
            CI.server = server;
        } else {
            throw new Exception("Server already initialized.");
        }
    }

    public static Server getServer () {
        return CI.server;
    }

    public static Console getConsole () {
        return getServer().getConsole();
    }

    public static PluginManager getPluginManager () {
        return getServer().getPluginManager();
    }

    public static CommandManager getCommandManager () {
        return getServer().getCommandManager();
    }

    public static void callEvent (final Event event) {
        getPluginManager().callEvent(event);
    }
}