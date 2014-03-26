package net.cubespace.ComuCator.P2P;

import lombok.Getter;
import net.cubespace.ComuCator.Cache.RegisteredPacketsCache;
import net.cubespace.ComuCator.Packet.ByteHolder;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Packet.PacketHandler;
import net.cubespace.ComuCator.Util.Scheduler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class P2PClient {
    @Getter
    private Socket socket;
    @Getter
    private P2PServer server;
    @Getter
    private final ArrayDeque<DefinedPacket> queue = new ArrayDeque<>();
    @Getter
    private final ArrayDeque<ByteHolder> byteQueue = new ArrayDeque<>();
    @Getter
    private final PacketHandler packetHandler;
    @Getter
    private final DataOutputStream dataOutputStream;
    @Getter
    private final RegisteredPacketsCache packetsCache = new RegisteredPacketsCache();

    public P2PClient(Socket socket, P2PServer server) throws IOException {
        packetHandler = new PacketHandler(this);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        this.socket = socket;
        this.server = server;

        ClientRunnable clientRunnable = new ClientRunnable(this);
        Scheduler.schedule(clientRunnable, 5);
    }

    public synchronized void byteWrite(byte[] bytes) throws IOException {
        synchronized (byteQueue) {
            byteQueue.add(new ByteHolder(bytes));
        }
    }
}
