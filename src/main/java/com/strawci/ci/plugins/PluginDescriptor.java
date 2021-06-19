package com.strawci.ci.plugins;

import java.io.InputStream;

import com.strawci.ci.config.YamlConfiguration;

public class PluginDescriptor extends YamlConfiguration {
    public PluginDescriptor (final InputStream stream) {
        super(stream);
    }

    public String getName () {
        return this.getString("name");
    }

    public String getAuthor () {
        return this.getString("author");
    }

    public String getVersion () {
        return this.getString("version");
    }

    public String getMain () {
        return this.getString("main");
    }

    public String getDescription () {
        return this.getString("description");
    }
}