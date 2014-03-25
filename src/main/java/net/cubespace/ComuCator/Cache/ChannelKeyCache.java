package net.cubespace.ComuCator.Cache;

import net.cubespace.ComuCator.Util.StringCode;

import java.util.LinkedHashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ChannelKeyCache {
    private final static LinkedHashMap<String, Long> cache = new LinkedHashMap<>();

    public static void addToCache(String channel) {
        synchronized (cache) {
            cache.put(channel, StringCode.getStringCode(channel));
        }
    }

    public static Long getKey(String channel) {
        return cache.get(channel);
    }

    public static void removeFromCache(String channel) {
        synchronized (cache) {
            cache.remove(channel);
        }
    }
}
