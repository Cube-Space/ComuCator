package net.cubespace.ComuCator.API;

import lombok.Getter;
import net.cubespace.ComuCator.API.Listener.PacketListener;
import net.cubespace.ComuCator.Util.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ListenerMethod {
    @Getter
    private MethodHandle method;
    @Getter
    private PacketListener packetListener;

    public ListenerMethod(Method method, PacketListener packetListener) {
        try {
            this.method = MethodHandles.lookup().unreflect(method);
            this.packetListener = packetListener;
        } catch (IllegalAccessException e) {
            Logger.error("Could not setup Listener Method", e);
        }
    }
}
