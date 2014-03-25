package net.cubespace.ComuCator.P2P;

import lombok.Getter;
import net.cubespace.ComuCator.Discover.DiscoveryTable;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Packet.ProtocolHandler;
import net.cubespace.ComuCator.Util.Logger;
import net.cubespace.ComuCator.Util.Scheduler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
                Socket accept = serverSocket.accept();

                synchronized (serverToClient) {
                    serverToClient.add(new P2PClient(accept, this));
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
            Logger.error("Could not create P2P Server Socket. Please check the Port in the Config");
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
        ByteArrayOutputStream bao1 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(bao1);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream1 = new DataOutputStream(bao);
        byte packetID = ProtocolHandler.getPacketID(packet);
        dataOutputStream1.write(packetID);
        packet.handle(dataOutputStream1);
        dataOutputStream1.close();

        DefinedPacket.writeArray(bao.toByteArray(), dataOutputStream);
        byte[] bytes = bao1.toByteArray();

        synchronized (clientToServer) {
            for(P2PClient client : new LinkedHashMap<>(clientToServer).values()) {
                if (!client.getSocket().isClosed()) {
                    client.byteWrite(bytes);
                } else {
                    removeClient(client);
                }
            }
        }
    }
}
