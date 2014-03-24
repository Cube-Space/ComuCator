package net.cubespace.ComuCator.API;

import lombok.Data;
import net.cubespace.ComuCator.API.Listener.PacketListener;

import java.lang.reflect.Method;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
@Data
public class ListenerMethod {
    private Method method;
    private PacketListener packetListener;
}
