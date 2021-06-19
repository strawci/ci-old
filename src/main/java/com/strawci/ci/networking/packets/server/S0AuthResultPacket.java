package com.strawci.ci.networking.packets.server;

import com.strawci.ci.networking.AuthResult;

public class S0AuthResultPacket {
    final private AuthResult result;
    final private String userID;

    public S0AuthResultPacket (final AuthResult result) {
        this.result = result;
        this.userID = null;
    }

    public S0AuthResultPacket (final AuthResult result, final String userID) {
        this.result = result;
        this.userID = userID;
    }

    public String getUserID () {
        return this.userID;
    }
    
    public AuthResult getResult () {
        return this.result;
    }
}