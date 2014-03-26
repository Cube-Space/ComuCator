package net.cubespace.ComuCator.Packet;

import net.cubespace.ComuCator.Packet.Protocol.DiscoveryTable;
import net.cubespace.ComuCator.Packet.Protocol.Message;
import net.cubespace.ComuCator.Packet.Protocol.PacketRegister;
import net.cubespace.ComuCator.Util.Logger;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ProtocolHandler {
    private enum Packets {
        DISCOVER_TABLE((byte) 1, DiscoveryTable.class),
        PACKET_REGISTER((byte) 2, PacketRegister.class),
        MESSAGE((byte) 3, Message.class);

        private byte packetID;
        private Class<? extends DefinedPacket> packet;

        Packets(byte packetID, Class<? extends DefinedPacket> packet) {
            this.packetID = packetID;
            this.packet = packet;
        }

        public static Class<? extends DefinedPacket> getPacket(byte packetID) {
            for(Packets packets : values()) {
                if (packets.packetID == packetID) {
                    return packets.packet;
                }
            }

            return null;
        }

        public static byte getPacketID(Class packet) {
            for(Packets packets : values()) {
                if (packets.packet.equals(packet)) {
                    return packets.packetID;
                }
            }

            return -1;
        }
    }

    public static DefinedPacket readPacket(DataInputStream input) throws IOException {
        byte packetID = input.readByte();
        Class<? extends DefinedPacket> packet = Packets.getPacket(packetID);

        if (packet == null) {
            Logger.error("Fatal error in Protocol. Unknown PacketID");
            throw new RuntimeException("Fatal Error");
        } else {
            try {
                DefinedPacket definedPacket = packet.newInstance();
                definedPacket.handle(input);

                return definedPacket;
            } catch (IllegalAccessException|InstantiationException e) {
                Logger.error("Could not build up Packet");
                throw new RuntimeException(e);
            }
        }
    }

    public static byte getPacketID(DefinedPacket packet) {
        return Packets.getPacketID(packet.getClass());
    }
}
