package net.cubespace.ComuCator.P2P;

import lombok.Getter;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Packet.PacketHandler;
import net.cubespace.ComuCator.Util.Scheduler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class P2PClient {
    public static AtomicInteger clients = new AtomicInteger(0);

    @Getter
    private Socket socket;
    @Getter
    private P2PServer server;
    @Getter
    private final ArrayDeque<DefinedPacket> queue = new ArrayDeque<>();
    @Getter
    private final PacketHandler packetHandler;
    @Getter
    private final DataOutputStream dataOutputStream;

    public P2PClient(Socket socket, P2PServer server) throws IOException {
        clients.incrementAndGet();

        packetHandler = new PacketHandler(this);
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

        this.socket = socket;
        this.server = server;

        ClientRunnable clientRunnable = new ClientRunnable(this);
        Scheduler.schedule(clientRunnable, 5);
    }

    public synchronized void write(DefinedPacket packet) {
        synchronized (queue) {
            queue.add(packet);
        }
    }

    public synchronized void byteWrite(byte[] bytes) throws IOException {
        synchronized (queue) {
            socket.getOutputStream().write(bytes);
        }
    }
}
