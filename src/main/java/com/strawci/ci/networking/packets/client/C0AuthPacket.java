package com.strawci.ci.networking.packets.client;

public class C0AuthPacket {

    private String username;
    private String password;
    private boolean guess;

    public C0AuthPacket () {
        this.guess = true;
    }

    public C0AuthPacket (final String username, final String password) {
        this.guess = false;
        this.username = username;
        this.password = password;
    }

    public String getUsername () {
        return this.username;
    }

    public String getPassword () {
        return this.password;
    }

    public boolean isGuess () {
        return this.guess;
    }
}