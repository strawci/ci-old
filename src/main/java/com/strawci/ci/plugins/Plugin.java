package com.strawci.ci.plugins;

import java.io.File;

import com.strawci.ci.CI;
import com.strawci.ci.commands.Command;
import com.strawci.ci.event.Listener;

public abstract class Plugin {
    private boolean enabled = false;
    private PluginLoader pluginLoader;
    private PluginDescriptor descriptor;
    private File dataFolder;

    public abstract void onEnable ();
    public void onDisable () {}
    
    public void init (final PluginLoader pluginLoader, final PluginDescriptor descriptor, final File dataFolder) {
        this.pluginLoader = pluginLoader;
        this.descriptor = descriptor;
        this.dataFolder = dataFolder;
    }

    public final File getDataFolder () {
        return this.dataFolder;
    }

    public final boolean isEnabled () {
        return this.enabled;
    }

    public final PluginDescriptor getDescriptor () {
        return this.descriptor;
    }

    public final String getName () {
        return this.getDescriptor().getName();
    }

    public final int hashCode () {
        return this.getName().hashCode();
    }

    public final boolean equals (final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Plugin)) {
            return false;
        }

        return this.getName().equals(((Plugin) obj).getName());
    }

    protected final void setEnabled (final boolean result) {
        if (this.enabled != result) {
            this.enabled = result;

            if (result) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    public void registerListener (final Listener listener) {
        CI.getPluginManager().registerEvents(listener, this);
    }

    public void registerCommand (final Command command) {
        try {
            CI.getCommandManager().registerCommand(command, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}