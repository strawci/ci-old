package com.strawci.ci.commands;

import com.strawci.ci.plugins.Plugin;

public abstract class Command {

    private final String name;
    private final String description;
    private final String[] aliases;

    private Plugin plugin;

    public Command (final String name, final String description, final String[] aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public Command (final String name, final String description) {
        this.name = name;
        this.description = description;
        this.aliases = null;
    }

    public Plugin getPlugin () {
        return this.plugin;
    }

    public void setPlugin (final Plugin plugin) throws Exception {
        if (this.plugin != null) {
            throw new Exception("Plugin already declared in Command class for (" + this.name + "@" + this.plugin.getName() + ")");
        } else {
            this.plugin = plugin;
        }
    }

    public String[] getAliases () {
        return this.aliases;
    }

    public String getName () {
        return this.name;
    }

    public String getDescription () {
        return this.description;
    }

    public boolean match (final String query) {
        if (this.name.equalsIgnoreCase(query)) {
            return true;
        } else {
            if (this.aliases != null) {
                for (final String alias : this.aliases) {
                    if (alias.equalsIgnoreCase(query)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public abstract boolean handle (final String args[], final ExecutionContext context);
}
