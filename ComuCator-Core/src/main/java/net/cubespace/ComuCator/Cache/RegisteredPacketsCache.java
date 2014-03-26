package net.cubespace.ComuCator.Cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class RegisteredPacketsCache {
    private final LinkedHashMap<Long, ArrayList<Long>> cache = new LinkedHashMap<>();

    public void addToCache(Long channel, Long packet) {
        synchronized (cache) {
            if (cache.containsKey(channel)) {
                cache.get(channel).add(packet);
            } else {
                ArrayList<Long> arrayList = new ArrayList<>();
                arrayList.add(packet);

                cache.put(channel, arrayList);
            }
        }
    }

    public boolean doesKnowPacket(Long channel, Long packet) {
        return cache.containsKey(channel) && cache.get(channel).contains(packet);
    }

    public LinkedHashMap<Long, ArrayList<Long>> getDump() {
        return new LinkedHashMap<>(cache);
    }

    public void removeFromCache(Long channel, Long packet) {
        synchronized (cache) {
            if (cache.containsKey(channel)) {
                cache.get(channel).remove(packet);

                if (cache.get(channel).size() == 0) {
                    cache.remove(channel);
                }
            }
        }
    }
}
