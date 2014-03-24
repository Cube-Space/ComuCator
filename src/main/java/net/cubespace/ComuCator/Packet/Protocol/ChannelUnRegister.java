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
public class ChannelUnRegister extends DefinedPacket {
    @Getter
    @Setter
    private String channel;

    @Override
    public void handle(DataInputStream input) throws IOException {
        channel = readString(input);
    }

    @Override
    public void handle(DataOutputStream output) throws IOException {
        writeString(channel, output);
    }
}
