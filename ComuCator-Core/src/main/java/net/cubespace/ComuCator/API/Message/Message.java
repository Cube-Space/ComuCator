package net.cubespace.ComuCator.API.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public interface Message {
    public Message write(DataOutputStream outputStream);
    public Message read(DataInputStream inputStream);
    public Message send();
    public String getChannel();
}
