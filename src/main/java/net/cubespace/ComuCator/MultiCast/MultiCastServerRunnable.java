package net.cubespace.ComuCator.MultiCast;

import net.cubespace.ComuCator.P2P.P2PServer;
import net.cubespace.ComuCator.Util.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class MultiCastServerRunnable implements Runnable {
    private String host;
    private P2PServer server;
    private MulticastSocket multicastSocket;
    private InetAddress group;
    private int runs = 21;

    public MultiCastServerRunnable(P2PServer server) throws IOException {
        host = server.getServerSocket().getInetAddress().getHostAddress() + ":" + server.getServerSocket().getLocalPort();

        this.server = server;
        group = InetAddress.getByName("224.0.0.1");
        multicastSocket = new MulticastSocket(6789);
        multicastSocket.joinGroup(group);
    }

    @Override
    public void run() {
        if (runs > 20) {
            try {
                DatagramPacket hi = new DatagramPacket(host.getBytes(), host.length(), group, 6789);
                multicastSocket.send(hi);
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
                if (!server.getDiscoveryTable().isKnownHost(recHost)) {
                    server.getDiscoveryTable().add(recHost.replaceAll("\\u0000", ""));
                }
            }
        } catch (IOException e) {
            Logger.error("Could not get MultiCast data", e);
        }
    }
}
