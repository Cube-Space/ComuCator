package net.cubespace.ComuCator;

import net.cubespace.ComuCator.Config.Main;
import net.cubespace.ComuCator.Discover.DiscoveryTable;
import net.cubespace.ComuCator.P2P.P2PServer;
import net.cubespace.ComuCator.P2P.P2PServers;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Starter {
    public static void start(Main config) {
        P2PServers.init(config);
        P2PServer server = new P2PServer(config.getIp(), config.getPort(), new DiscoveryTable());
        server.start();
    }
}
