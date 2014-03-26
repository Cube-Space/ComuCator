package net.cubespace.ComuCator.Packet.Protocol;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.ComuCator.Packet.DefinedPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Message extends DefinedPacket {
    @Getter
    @Setter
    private long channel;
    @Getter
    @Setter
    private long packet;
    @Getter
    @Setter
    private byte[] message;

    @Override
    public void handle(DataInputStream input) throws IOException {
        channel = DefinedPacket.readLong(input);
        packet = DefinedPacket.readLong(input);
        message = DefinedPacket.readArray(input);
    }

    @Override
    public void handle(DataOutputStream output) throws IOException {
        DefinedPacket.writeLong(channel, output);
        DefinedPacket.writeLong(packet, output);
        DefinedPacket.writeArray(message, output);
    }
}
