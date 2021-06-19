package com.strawci.ci.users;

public class User {

    private final String id;
    private final String username;
    private final String passwordHashed;

    public User (final String id, final String username, final String passwordHashed) {
        this.id = id;
        this.username = username;
        this.passwordHashed = passwordHashed;
    }

    public String getID () {
        return this.id;
    }

    public String getUsername () {
        return this.username;
    }

    public boolean comparePassword (final String password) {
        return this.passwordHashed.equals(password);
    }
}
