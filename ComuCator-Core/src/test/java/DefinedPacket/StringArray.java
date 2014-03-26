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
public class StringArray {
    @Test
    public void writeStringArray() throws IOException {
        java.lang.String[] stringArray = new java.lang.String[]{"Test1", "Test2"};
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(bao);
        DefinedPacket.writeStringArray(stringArray, outputStream);

        Assert.assertEquals(bao.toByteArray(), new byte[]{2,5,84,101,115,116,49,5,84,101,115,116,50});
    }

    @Test
    public void readString() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{2,5,84,101,115,116,49,5,84,101,115,116,50});
        java.lang.String[] stringArray = DefinedPacket.readStringArray(new DataInputStream(inputStream));

        Assert.assertEquals(stringArray, new java.lang.String[]{"Test1", "Test2"});
    }
}
