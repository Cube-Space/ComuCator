package net.cubespace.ComuCator.API;

import net.cubespace.ComuCator.API.Annotation.Channel;
import net.cubespace.ComuCator.API.Annotation.PacketHandler;
import net.cubespace.ComuCator.API.Listener.PacketListener;
import net.cubespace.ComuCator.API.Message.Message;
import net.cubespace.ComuCator.Util.Logger;
import net.cubespace.ComuCator.Util.StringCode;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class PacketManager {
    private final static LinkedHashMap<Long, LinkedHashMap<Long, ArrayList<ListenerMethod>>> listeners = new LinkedHashMap<>();
    private final static LinkedHashMap<Long, LinkedHashMap<Long, Class>> packets = new LinkedHashMap<>();

    public static void callListeners(net.cubespace.ComuCator.Packet.Protocol.Message message) {
        if (!listeners.containsKey(message.getChannel()) || !listeners.get(message.getChannel()).containsKey(message.getPacket())) {
            return;
        }


        if (!packets.containsKey(message.getChannel())) {
            return;
        }

        // Build up that message
        if (!packets.get(message.getChannel()).containsKey(message.getPacket())) {
            return;
        }

        Class packetClass = packets.get(message.getChannel()).get(message.getPacket());

        try {
            Message message1 = (Message) packetClass.newInstance();
            message1.read(new DataInputStream(new ByteArrayInputStream(message.getMessage())));

            for (ListenerMethod listenerMethod : listeners.get(message.getChannel()).get(message.getPacket())) {
                listenerMethod.getMethod().invoke(listenerMethod.getPacketListener(), message1);
            }
        } catch (Exception e) {
            Logger.warn("The packet could not be build. Make sure it has a default Constructor - " + packetClass.getName());
        }
    }

    public static void registerListener(PacketListener listener) {
        // Find the Channel
        String channel;
        if (listener.getClass().isAnnotationPresent(Channel.class)) {
            channel = ((Channel) listener.getClass().getAnnotation(Channel.class)).value();
        } else {
            channel = listener.getChannel();
        }

        // If there is no channel for this Packet => Outta here
        if (channel == null) {
            Logger.warn("The listener has no Channel annotation and did gave null back on getChannel() - " + listener.getClass().getName());
            return;
        }

        Long key = StringCode.getStringCode(channel);

        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(PacketHandler.class)) {
                Class argument = method.getParameterTypes()[0];

                if (!Message.class.isAssignableFrom(argument)) {
                    continue;
                }

                ListenerMethod listenerMethod = new ListenerMethod();
                listenerMethod.setMethod(method);
                listenerMethod.setPacketListener(listener);

                Long argumentKey = StringCode.getStringCode(argument.getName());

                synchronized (packets) {
                    if (!packets.containsKey(key) || !packets.get(key).containsKey(argumentKey)) {
                        continue;
                    }
                }

                synchronized (listeners) {
                    // Check if Channel exists
                    if (listeners.containsKey(key)) {
                        if (listeners.get(key).containsKey(argumentKey)) {
                            listeners.get(key).get(argumentKey).add(listenerMethod);
                        } else {
                            ArrayList<ListenerMethod> listeners1 = new ArrayList<>();
                            listeners1.add(listenerMethod);

                            listeners.get(key).put(argumentKey, listeners1);
                        }
                    } else {
                        ArrayList<ListenerMethod> listeners1 = new ArrayList<>();
                        listeners1.add(listenerMethod);

                        LinkedHashMap<Long, ArrayList<ListenerMethod>> newHashMap = new LinkedHashMap<>();
                        newHashMap.put(argumentKey, listeners1);

                        listeners.put(key, newHashMap);
                    }
                }
            }
        }
    }

    public static void removeListener(PacketListener listener) {
        // Find the Channel
        String channel;
        if (listener.getClass().isAnnotationPresent(Channel.class)) {
            channel = ((Channel) listener.getClass().getAnnotation(Channel.class)).value();
        } else {
            channel = listener.getChannel();
        }

        // If there is no channel for this Packet => Outta here
        if (channel == null) {
            Logger.warn("The listener has no Channel annotation and did gave null back on getChannel() - " + listener.getClass().getName());
            return;
        }

        Long key = StringCode.getStringCode(channel);

        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(PacketHandler.class)) {
                Class argument = method.getParameterTypes()[0];

                if (!Message.class.isAssignableFrom(argument)) {
                    continue;
                }

                Long argumentKey = StringCode.getStringCode(argument.getName());

                synchronized (listeners) {
                    // Check if Channel exists
                    if (listeners.containsKey(key) && listeners.get(key).containsKey(argumentKey)) {
                        Iterator<ListenerMethod> iterator = listeners.get(key).get(argumentKey).iterator();

                        while (iterator.hasNext()) {
                            ListenerMethod method1 = iterator.next();

                            if (method1.getMethod().equals(method)) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        }

        synchronized (listeners) {
            // Check if Channel exists
            if (listeners.containsKey(key)) {
                listeners.get(key).remove(listener);
            }
        }
    }

    public static void registerPacket(Class packet) {
        if (!Message.class.isAssignableFrom(packet)) {
            Logger.warn("This packet does not implement the Message Interface - " + packet.getName());
            return;
        }

        // Create the Packet once
        try {
            Message message = (Message) packet.newInstance();

            // Find the Channel to which this Packet belongs
            String channel;
            if (packet.isAnnotationPresent(Channel.class)) {
                channel = ((Channel) packet.getAnnotation(Channel.class)).value();
            } else {
                channel = message.getChannel();
            }

            // If there is no channel for this Packet => Outta here
            if (channel == null) {
                Logger.warn("The packet has no Channel annotation and did gave null back on getChannel() - " + packet.getName());
                return;
            }

            Long key = StringCode.getStringCode(channel);
            Long classKey = StringCode.getStringCode(packet.getName());

            synchronized (packets) {
                // Check if Channel exists
                if (packets.containsKey(key)) {
                    packets.get(key).put(classKey, packet);
                } else {
                    LinkedHashMap<Long, Class> classes = new LinkedHashMap<>();
                    classes.put(classKey, packet);

                    packets.put(key, classes);
                }
            }
        } catch (Exception e) {
            Logger.warn("The packet could not be build. Make sure it has a default Constructor - " + packet.getName());
        }
    }

    public static void removePacket(Class packet) {
        if (!Message.class.isAssignableFrom(packet)) {
            Logger.warn("This packet does not implement the Message Interface - " + packet.getName());
            return;
        }

        // Create the Packet once
        try {
            Message message = (Message) packet.newInstance();

            // Find the Channel to which this Packet belongs
            String channel;
            if (packet.isAnnotationPresent(Channel.class)) {
                channel = ((Channel) packet.getAnnotation(Channel.class)).value();
            } else {
                channel = message.getChannel();
            }

            // If there is no channel for this Packet => Outta here
            if (channel == null) {
                Logger.warn("The packet has no Channel annotation and did gave null back on getChannel() - " + packet.getName());
                return;
            }

            Long key = StringCode.getStringCode(channel);
            Long classKey = StringCode.getStringCode(packet.getName());

            synchronized (packets) {
                // Check if Channel exists
                if (packets.containsKey(key)) {
                    packets.get(key).remove(classKey);
                }
            }

            synchronized (listeners) {
                if (listeners.containsKey(key)) {
                    listeners.get(key).remove(classKey);
                }
            }
        } catch (Exception e) {
            Logger.warn("The packet could not be build. Make sure it has a default Constructor - " + packet.getName());
        }
    }
}
