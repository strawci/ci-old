package com.strawci.ci.networking;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.strawci.ci.Server;
import com.strawci.ci.config.ServerConfiguration;
import com.strawci.ci.networking.packets.client.C0AuthPacket;
import com.strawci.ci.networking.pipes.AuthPipeline;

public class ServerHandler {

    private final Server strawci;
    private final ServerConfiguration serverConfig;
    private final SocketIOServer server;

    public ServerHandler (final Server strawci, final ServerConfiguration serverConfig) {
        Configuration config = new Configuration();
        config.setHostname(serverConfig.getHost());
        config.setPort(serverConfig.getPort());

        this.server = new SocketIOServer(config);
        this.strawci = strawci;
        this.serverConfig = serverConfig;
    }

    public void start () {
        this.server.addEventListener("auth", C0AuthPacket.class, new AuthPipeline(this));
        this.server.start();
    }

    public void stop () {
        this.server.stop();
    }

    public ServerConfiguration getServerConfig () {
        return this.serverConfig;
    }

    public Server getStrawCI () {
        return this.strawci;
    }
}