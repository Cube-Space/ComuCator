package net.cubespace.ComuCator.MultiCast;

import net.cubespace.ComuCator.Config.Main;
import net.cubespace.ComuCator.P2P.P2PServer;
import net.cubespace.ComuCator.P2P.P2PServers;
import net.cubespace.ComuCator.Util.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class MultiCastServerRunnable implements Runnable {
    private MulticastSocket multicastSocket;
    private InetAddress group;
    private int runs = 21;

    public MultiCastServerRunnable(Main config) throws IOException {
        group = InetAddress.getByName(config.getMultiCastIP());
        multicastSocket = new MulticastSocket(config.getMultiCastPort());

        if (!config.getNetworkInterface().equals("")) {
            multicastSocket.setNetworkInterface(NetworkInterface.getByName(config.getNetworkInterface()));
        }

        multicastSocket.joinGroup(group);
    }

    @Override
    public void run() {
        if (runs > 20) {
            try {
                for(P2PServer server : P2PServers.getServers()) {
                    String host = server.getServerSocket().getInetAddress().getHostAddress() + ":" + server.getServerSocket().getLocalPort();
                    DatagramPacket hi = new DatagramPacket(host.getBytes(), host.length(), group, multicastSocket.getPort());
                    multicastSocket.send(hi);
                }
            } catch (IOException e) {
                Logger.error("Could not send MultiCast", e);
            }

            runs = 0;
        } else {
            runs++;
        }

        try {
            if (multicastSocket.getReceiveBufferSize() > 0) {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                multicastSocket.receive(recv);

                String recHost = new String(recv.getData());
                for(P2PServer server : P2PServers.getServers()) {
                    if (!server.getDiscoveryTable().isKnownHost(recHost)) {
                        server.getDiscoveryTable().add(recHost.replaceAll("\\u0000", ""));
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("Could not get MultiCast data", e);
        }
    }
}
