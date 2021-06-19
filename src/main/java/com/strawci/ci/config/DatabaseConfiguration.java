package com.strawci.ci.config;

public class DatabaseConfiguration extends ConfigurationFile {

    private String type;
    private String username;
    private String password;
    private int port = -1;
    private String address;
    private String database;

    public DatabaseConfiguration() {
        super("database.yml");
    }

    @Override
    protected void OnLoad(YamlConfiguration config) {
        this.type = config.getString("type");
        this.username = config.getString("username");
        this.password = config.getString("password");
        this.port = config.getInt("port");
        this.address = config.getString("address");
        this.database = config.getString("database");
    }

    public String toMongoURI () {
        String uri = "mongodb://";

        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            uri += username + ":" + password + "@";
        }

        uri += address + ":" + this.port + "/" + this.database;
        return uri;
    }

    public String toURI () {
        switch (this.type.toLowerCase()) {
            case "mongodb":
                return this.toMongoURI();
            default:
                return null;
        }
    }

    public String getDatabase () {
        return this.database;
    }

    public String getDatabaseType () {
        return this.type;
    }
}