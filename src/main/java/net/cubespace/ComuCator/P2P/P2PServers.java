package net.cubespace.ComuCator.P2P;

import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Util.Logger;

import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class P2PServers {
    private final static LinkedHashSet<P2PServer> servers = new LinkedHashSet<>();

    public static void broadCastToAll(DefinedPacket packet) {
        synchronized (servers) {
            for(P2PServer server : servers) {
                try {
                    server.broadcast(packet);
                } catch (IOException e) {
                    Logger.warn("Server could not send packet", e);
                }
            }
        }
    }

    public static void addServer(P2PServer server) {
        synchronized (servers) {
            servers.add(server);
        }
    }

    public static void removeServer(P2PServer server) {
        synchronized (servers) {
            servers.remove(server);
        }
    }
}
