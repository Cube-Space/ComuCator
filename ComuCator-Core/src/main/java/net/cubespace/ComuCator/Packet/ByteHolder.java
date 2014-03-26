package net.cubespace.ComuCator.Packet;

import lombok.Data;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
@Data
public class ByteHolder {
    private byte[] bytes;

    public ByteHolder(byte[] bytes) {
        this.bytes = bytes;
    }
}
