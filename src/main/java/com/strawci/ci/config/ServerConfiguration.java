package com.strawci.ci.config;

public class ServerConfiguration extends ConfigurationFile {

    private String host;
    private int port;
    private boolean allowGuest;

    public ServerConfiguration() {
        super("server.yml");
    }

    @Override
    protected void OnLoad(YamlConfiguration config) {
        this.host = config.getString("host");
        this.port = config.getInt("port");
        this.allowGuest = config.getBoolean("allow-guest");
    }

    public String getHost () {
        return this.host;
    }

    public int getPort () {
        return this.port;
    }

    public boolean isAllowGuest () {
        return this.allowGuest;
    }
}