package net.cubespace.ComuCator.Packet;

import net.cubespace.ComuCator.API.PacketManager;
import net.cubespace.ComuCator.P2P.P2PClient;
import net.cubespace.ComuCator.Packet.Protocol.DiscoveryTable;
import net.cubespace.ComuCator.Packet.Protocol.Message;
import net.cubespace.ComuCator.Packet.Protocol.PacketRegister;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PacketHandler extends PacketController {
    private P2PClient client;

    public PacketHandler(P2PClient client) {
        this.client = client;
    }

    public void handle(PacketRegister packetRegister) {
        if (packetRegister.getMode() == 0) {
            client.getPacketsCache().addToCache(packetRegister.getChannel(), packetRegister.getPacket());
        } else {
            client.getPacketsCache().removeFromCache(packetRegister.getChannel(), packetRegister.getPacket());
        }
    }

    public void handle(DiscoveryTable discoveryTable) {
        for (String host : discoveryTable.getHosts()) {
            if (!client.getServer().getDiscoveryTable().isKnownHost(host)) {
                client.getServer().getDiscoveryTable().add(host);
            }
        }
    }

    public void handle(Message message) {
        PacketManager.addMessage(message);
    }
}
