package net.cubespace.ComuCator.Packet;

import net.cubespace.ComuCator.API.PacketManager;
import net.cubespace.ComuCator.P2P.P2PClient;
import net.cubespace.ComuCator.Packet.Protocol.ChannelRegister;
import net.cubespace.ComuCator.Packet.Protocol.ChannelUnRegister;
import net.cubespace.ComuCator.Packet.Protocol.DiscoveryTable;
import net.cubespace.ComuCator.Packet.Protocol.Message;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PacketHandler extends PacketController {
    private P2PClient client;

    public PacketHandler(P2PClient client) {
        this.client = client;
    }

    public void handle(ChannelRegister channelRegister) {

    }

    public void handle(ChannelUnRegister channelUnRegister) {

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
