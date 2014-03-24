package DefinedPacket;

import net.cubespace.ComuCator.Packet.DefinedPacket;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class String {
    @Test
    public void writeString() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        DefinedPacket.writeString("Test", new DataOutputStream(outputStream));

        Assert.assertEquals(outputStream.toByteArray(), new byte[]{4,84,101,115,116});
    }

    @Test
    public void readString() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{4,84,101,115,116});

        java.lang.String string = DefinedPacket.readString(new DataInputStream(inputStream));

        Assert.assertEquals(string, "Test");
    }
}
