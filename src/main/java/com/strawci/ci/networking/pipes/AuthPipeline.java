package com.strawci.ci.networking.pipes;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;

import com.strawci.ci.database.DatabaseProvider;
import com.strawci.ci.networking.AuthResult;
import com.strawci.ci.networking.ServerHandler;
import com.strawci.ci.networking.packets.client.C0AuthPacket;
import com.strawci.ci.networking.packets.server.S0AuthResultPacket;
import com.strawci.ci.users.User;

public class AuthPipeline implements DataListener<C0AuthPacket> {

    private final ServerHandler handler;

    public AuthPipeline(ServerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onData(SocketIOClient client, C0AuthPacket packet, AckRequest ackSender) throws Exception {
        if (client.has("logged") && (boolean) client.get("logged") == true) {
            client.sendEvent("auth", new S0AuthResultPacket(AuthResult.ALREADY_LOGGED));
            return;
        }

        if (!this.handler.getServerConfig().isAllowGuest() && packet.isGuess()) {
            client.sendEvent("auth", new S0AuthResultPacket(AuthResult.GUEST_NOT_ALLOWED));
            client.disconnect();
            return;
        } 
        
        if (packet.isGuess()) {
            client.sendEvent("auth", new S0AuthResultPacket(AuthResult.SUCCESS));
            client.set("logged", true);
            client.joinRoom("guess");
        } else {
            DatabaseProvider database = this.handler.getStrawCI().getDatabase();
            User user = database.getUserByName(packet.getUsername());
            if (user.comparePassword(packet.getPassword())) {
                client.sendEvent("auth", new S0AuthResultPacket(AuthResult.SUCCESS), user.getID());
                client.set("user", user.getID());
                client.set("logged", true);
                client.joinRoom("logged");
            } else {
                client.sendEvent("auth", new S0AuthResultPacket(AuthResult.FAILED));
                client.disconnect();
            }
        }
    }
}
