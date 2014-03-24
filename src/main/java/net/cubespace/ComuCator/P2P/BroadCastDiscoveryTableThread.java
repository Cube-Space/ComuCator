package net.cubespace.ComuCator.P2P;

import net.cubespace.ComuCator.Packet.Protocol.DiscoveryTable;
import net.cubespace.ComuCator.Util.Logger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class BroadCastDiscoveryTableThread implements Runnable {
    private P2PServer server;

    public BroadCastDiscoveryTableThread(P2PServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            DiscoveryTable discoveryTable = new DiscoveryTable();
            discoveryTable.setHosts(server.getDiscoveryTable().dump());

            server.broadcast(discoveryTable);
        } catch (Exception e) {
            Logger.error("Could not broadcast Discovery Table", e);
        }
    }
}
