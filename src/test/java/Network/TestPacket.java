package Network;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.ComuCator.API.Annotation.Channel;
import net.cubespace.ComuCator.API.Message.AbstractMessage;
import net.cubespace.ComuCator.API.Message.Message;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Util.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
@Channel("Test")
public class TestPacket extends AbstractMessage {
    @Getter
    @Setter
    private String test;

    public TestPacket() {}

    @Override
    public Message write(DataOutputStream outputStream) {
        try {
            DefinedPacket.writeString(test, outputStream);
        } catch (IOException e) {
            Logger.error("Could not write Message :(");
        }

        return this;
    }

    @Override
    public Message read(DataInputStream inputStream) {
        try {
            test = DefinedPacket.readString(inputStream);
        } catch (IOException e) {
            Logger.error("Could not read Message :(");
        }

        return this;
    }
}