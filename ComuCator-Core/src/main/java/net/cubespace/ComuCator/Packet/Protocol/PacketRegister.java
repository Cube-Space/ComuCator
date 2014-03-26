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
public class PacketRegister extends DefinedPacket {
    @Getter
    @Setter
    private Long channel;
    @Getter
    @Setter
    private Long packet;
    @Getter
    @Setter
    private byte mode;

    @Override
    public void handle(DataInputStream input) throws IOException {
        channel = DefinedPacket.readLong(input);
        packet = DefinedPacket.readLong(input);
        mode = (byte) input.read();
    }

    @Override
    public void handle(DataOutputStream output) throws IOException {
        DefinedPacket.writeLong(channel, output);
        DefinedPacket.writeLong(packet, output);
        output.write(mode);
    }
}
