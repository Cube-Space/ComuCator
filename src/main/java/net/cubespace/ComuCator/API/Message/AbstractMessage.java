package net.cubespace.ComuCator.API.Message;

import net.cubespace.ComuCator.API.Annotation.Channel;
import net.cubespace.ComuCator.Cache.ChannelKeyCache;
import net.cubespace.ComuCator.Cache.ClassKeyCache;
import net.cubespace.ComuCator.P2P.P2PServers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public abstract class AbstractMessage implements Message {
    public AbstractMessage() {
        String channel;
        if (getClass().isAnnotationPresent(Channel.class)) {
            channel = getClass().getAnnotation(Channel.class).value();
        } else {
            channel = getChannel();
        }

        channelKey = ChannelKeyCache.getKey(channel);
        classKey = ClassKeyCache.getKey(getClass());
    }

    private Long channelKey;
    private Long classKey;

    public String getChannel() {
        return null;
    }

    public Message send() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        write(dataOutputStream);

        net.cubespace.ComuCator.Packet.Protocol.Message message = new net.cubespace.ComuCator.Packet.Protocol.Message();
        message.setChannel(channelKey);
        message.setPacket(classKey);
        message.setMessage(byteArrayOutputStream.toByteArray());

        P2PServers.broadCastToAll(message);

        return this;
    }
}
