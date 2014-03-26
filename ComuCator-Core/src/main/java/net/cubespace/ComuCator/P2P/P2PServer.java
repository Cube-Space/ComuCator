package net.cubespace.ComuCator.P2P;

import lombok.Getter;
import net.cubespace.ComuCator.Discover.DiscoveryTable;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Packet.Protocol.Message;
import net.cubespace.ComuCator.Packet.Protocol.PacketRegister;
import net.cubespace.ComuCator.Util.Logger;
import net.cubespace.ComuCator.Util.Scheduler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class P2PServer extends Thread {
    private int port;
    @Getter
    private DiscoveryTable discoveryTable;
    @Getter
    private ServerSocket serverSocket;
    private final ArrayList<P2PClient> serverToClient = new ArrayList<>();
    private final LinkedHashMap<String, P2PClient> clientToServer = new LinkedHashMap<>();
    private String ip;

    public P2PServer(String ip, int port, DiscoveryTable discoveryTable) {
        Logger.info("New P2PServer with port " + port);

        this.discoveryTable = discoveryTable;
        this.port = port;
        this.ip = ip;

        setName("P2PServer-" + ip + ":" + String.valueOf(port));
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port, 100, InetAddress.getByName(ip));

            BroadCastDiscoveryTableThread tableRunnable = new BroadCastDiscoveryTableThread(this);
            final ScheduledFuture tableRunnableFuture = Scheduler.scheduleAtFixedRate(tableRunnable, 1000, 30000);

            final P2PServer server = this;

            final ScheduledFuture scheduledFuture = Scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    for (String host : discoveryTable.dump()) {
                        synchronized (clientToServer) {
                            if (!clientToServer.containsKey(host)) {
                                if (host.equals(serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort())) {
                                    continue;
                                }

                                String ip = host.substring(0, host.lastIndexOf(":"));
                                int port = Integer.parseInt(host.substring(host.lastIndexOf(":") + 1));

                                try {
                                    Socket socket = new Socket();
                                    socket.connect(new InetSocketAddress(ip, port));
                                    clientToServer.put(host, new P2PClient(socket, server));
                                } catch (IOException e) {
                                    Logger.warn("Unknown Host to connect to", e);
                                }
                            }
                        }
                    }
                }
            }, 50, 50);

            P2PServers.addServer(this);

            while(!serverSocket.isClosed()) {
                try {
                    Socket accept = serverSocket.accept();

                    synchronized (serverToClient) {
                        P2PClient client = new P2PClient(accept, this);
                        serverToClient.add(client);

                        for(Map.Entry<Long, ArrayList<Long>> entry : P2PServers.getPacketCacheDump().entrySet()) {
                            PacketRegister packetRegister = new PacketRegister();
                            packetRegister.setMode((byte) 0);
                            packetRegister.setChannel(entry.getKey());

                            for(Long packet : entry.getValue()) {
                                packetRegister.setPacket(packet);

                                byte[] bytes = DefinedPacket.packPacket(packetRegister);
                                client.byteWrite(bytes);
                            }
                        }
                    }
                } catch (SocketException e) {

                }
            }

            P2PServers.removeServer(this);

            scheduledFuture.cancel(true);
            tableRunnableFuture.cancel(true);

            synchronized (serverToClient) {
                serverToClient.clear();
            }

            discoveryTable.clear();
        } catch (IOException e) {
            Logger.error("Could not create P2P Server Socket. Please check the Port in the Config", e);
        }
    }

    public void removeClient(P2PClient p2PClient)  {
        synchronized (clientToServer) {
            clientToServer.remove(p2PClient.getSocket().getInetAddress().getHostAddress() + ":" + p2PClient.getSocket().getPort());
        }

        synchronized (serverToClient) {
            serverToClient.remove(p2PClient);
        }

        discoveryTable.remove(p2PClient.getSocket().getInetAddress().getHostAddress() + ":" + p2PClient.getSocket().getPort());
    }

    public synchronized void broadcast(DefinedPacket packet) throws IOException {
        byte[] send = DefinedPacket.packPacket(packet);

        synchronized (serverToClient) {
            for(P2PClient client : new ArrayList<>(serverToClient)) {
                if (!client.getSocket().isClosed()) {
                    client.byteWrite(send);
                } else {
                    removeClient(client);
                }
            }
        }
    }

    public synchronized void broadcast(Message packet) throws IOException {
        byte[] send = DefinedPacket.packPacket(packet);

        synchronized (clientToServer) {
            for(P2PClient client : new LinkedHashMap<>(clientToServer).values()) {
                if (!client.getSocket().isClosed()) {
                    if (client.getPacketsCache().doesKnowPacket(packet.getChannel(), packet.getPacket())) {
                        client.byteWrite(send);
                    }
                } else {
                    removeClient(client);
                }
            }
        }
    }

    public void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Logger.error("Could not close Socket", e);
        }
    }
}
