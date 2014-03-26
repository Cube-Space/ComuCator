package net.cubespace.ComuCator.Packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public abstract class DefinedPacket {
    public static byte[] packPacket(DefinedPacket packet) throws IOException {
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bao1);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream1 = new DataOutputStream(bao);
        byte packetID = ProtocolHandler.getPacketID(packet);
        dataOutputStream1.write(packetID);
        packet.handle(dataOutputStream1);
        dataOutputStream1.close();

        DefinedPacket.writeArray(bao.toByteArray(), dataOutputStream);
        return bao1.toByteArray();
    }

    public static void writeString(String string, DataOutputStream output) throws IOException {
        writeArray(string.getBytes(StandardCharsets.UTF_8), output);
    }

    public static String readString(DataInputStream input) throws IOException {
        return new String(readArray(input), StandardCharsets.UTF_8);
    }

    public static void writeArray(byte[] byteArray, OutputStream output) throws IOException {
        writeVarInt(byteArray.length, output);
        output.write(byteArray);
    }

    public static byte[] readArray(InputStream input) throws IOException {
        int len = readVarInt(input);

        byte[] byteArray = new byte[len];
        int read = input.read(byteArray);

        if (read != len) {
            for(int i = read; i < len; i++) {
                byteArray[i] = (byte) input.read();
            }
        }

        return byteArray;
    }

    public static void writeStringArray(String[] stringArray, DataOutputStream output) throws IOException {
        writeVarInt(stringArray.length, output);

        for (String string : stringArray) {
            writeString(string, output);
        }
    }

    public static String[] readStringArray(DataInputStream input) throws IOException {
        int len = readVarInt(input);

        String[] ret = new String[ len ];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = readString(input);
        }

        return ret;
    }

    public static int readVarInt(InputStream input) throws IOException {
        int out = 0;
        int bytes = 0;
        byte in;

        while (true) {
            in = (byte) input.read();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((in & 0x80) != 0x80) {
                break;
            }
        }

        return out;
    }

    public static void writeVarInt(int value, OutputStream output) throws IOException {
        int part;

        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0) {
                part |= 0x80;
            }

            output.write(part);

            if (value == 0) {
                break;
            }
        }
    }

    public static void writeLong(long value, DataOutputStream output) throws IOException {
        output.writeLong(value);
    }

    public static long readLong(DataInputStream input) throws IOException {
        return input.readLong();
    }

    public abstract void handle(DataInputStream input) throws IOException;
    public abstract void handle(DataOutputStream output) throws IOException;
}
