package Network;

import net.cubespace.ComuCator.API.Annotation.Channel;
import net.cubespace.ComuCator.API.Annotation.PacketHandler;
import net.cubespace.ComuCator.API.Listener.AbstractPacketListener;
import net.cubespace.ComuCator.API.PacketManager;
import net.cubespace.ComuCator.Discover.DiscoveryTable;
import net.cubespace.ComuCator.P2P.P2PClient;
import net.cubespace.ComuCator.P2P.P2PServer;
import net.cubespace.ComuCator.Util.Logger;
import net.cubespace.ComuCator.Util.Scheduler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Server {
    public Server() {
        for(int i = 0; i < 2; i++) {
            P2PServer server = new P2PServer("192.168.1.121", 2222 + i, new DiscoveryTable());
            server.start();
        }

        PacketManager.registerPacket(TestPacket.class);
        PacketManager.registerListener(new TestListener());

        Scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                TestPacket testPacket = new TestPacket();
                testPacket.setTest("test");
                testPacket.send();
            }
        }, 1, 1);


        Scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Logger.debug("Clients connected: " + P2PClient.clients.get());
            }
        }, 5000, 5000);
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    @Channel("Test")
    public class TestListener extends AbstractPacketListener {
        public AtomicInteger handled = new AtomicInteger(0);

        public TestListener() {
            Scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    Logger.debug("Handled " + handled.getAndSet(0) + " Messages");
                }
            }, 1000, 1000);
        }

        @PacketHandler
        public void onTestMessage(TestPacket testPacket) {
            handled.incrementAndGet();
        }
    }
}
