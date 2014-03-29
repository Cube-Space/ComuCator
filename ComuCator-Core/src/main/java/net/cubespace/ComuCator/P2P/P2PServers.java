package net.cubespace.ComuCator.P2P;

import net.cubespace.ComuCator.Cache.RegisteredPacketsCache;
import net.cubespace.ComuCator.Config.Main;
import net.cubespace.ComuCator.MultiCast.MultiCastServerRunnable;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Packet.Protocol.Message;
import net.cubespace.ComuCator.Util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class P2PServers {
    private final static LinkedHashSet<P2PServer> servers = new LinkedHashSet<>();
    private final static RegisteredPacketsCache packetCache = new RegisteredPacketsCache();
    private static MultiCastServerRunnable multiCastServerRunnable;

    public static void init(Main config) {
        try {
            multiCastServerRunnable = new MultiCastServerRunnable(config);
            multiCastServerRunnable.start();
        } catch (IOException e) {
            Logger.error("Could not setup MultiCast", e);
        }
    }

    public static void requestDiscovery() {
        try {
            multiCastServerRunnable.requestDiscovery();
        } catch (IOException e) {
            Logger.error("Could not request Discovery", e);
        }
    }

    public static void registerPacket(Long channel, Long packet) {
        packetCache.addToCache(channel, packet);
    }

    public static void removePacket(Long channel, Long packet) {
        packetCache.removeFromCache(channel, packet);
    }

    public static LinkedHashMap<Long, ArrayList<Long>> getPacketCacheDump() {
        return packetCache.getDump();
    }

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

    public static void broadCastToAll(Message packet) {
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

    public static LinkedHashSet<P2PServer> getServers() {
        synchronized (servers) {
            return new LinkedHashSet<>(servers);
        }
    }
}
