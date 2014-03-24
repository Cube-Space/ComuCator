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
public class DiscoveryTable extends DefinedPacket {
    @Getter
    @Setter
    private String[] hosts;

    @Override
    public void handle(DataInputStream input) throws IOException {
        hosts = readStringArray(input);
    }

    @Override
    public void handle(DataOutputStream output) throws IOException {
        writeStringArray(hosts, output);
    }
}
