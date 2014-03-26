package net.cubespace.ComuCator.Discover;

import java.util.LinkedHashSet;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class DiscoveryTable {
    private final LinkedHashSet<String> knownHosts = new LinkedHashSet<>();

    public boolean isKnownHost(String host) {
        synchronized (knownHosts) {
            return knownHosts.contains(host);
        }
    }

    public void clear() {
        synchronized (knownHosts) {
            knownHosts.clear();
        }
    }

    public void remove(String hostAddress) {
        synchronized (knownHosts) {
            knownHosts.remove(hostAddress);
        }
    }

    public String[] dump() {
        synchronized (knownHosts) {
            return knownHosts.toArray(new String[knownHosts.size()]);
        }
    }

    public void add(String host) {
        synchronized (knownHosts) {
            knownHosts.add(host);
        }
    }
}
