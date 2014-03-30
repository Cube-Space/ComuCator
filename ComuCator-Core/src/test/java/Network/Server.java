package Network;

import net.cubespace.ComuCator.API.Annotation.Channel;
import net.cubespace.ComuCator.API.Annotation.PacketHandler;
import net.cubespace.ComuCator.API.Listener.AbstractPacketListener;
import net.cubespace.ComuCator.API.PacketManager;
import net.cubespace.ComuCator.Config.Main;
import net.cubespace.ComuCator.Discover.DiscoveryTable;
import net.cubespace.ComuCator.P2P.P2PServer;
import net.cubespace.ComuCator.P2P.P2PServers;
import net.cubespace.ComuCator.Util.Scheduler;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Server {
    private boolean running = true;
    private TestListener listener;
    private AtomicInteger handled = new AtomicInteger(0);

    public static void main(String[] args) {
        final Server server = new Server();

        try {
            Scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    System.out.println(server.handled.getAndSet(0));
                }
            }, 1000, 1000);

            server.startServers();
            server.register();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 1, timeOut = 2000, singleThreaded = true)
    public void startServers() throws Exception {
        Main config = new Main();
        config.setIp("127.0.0.1");
        config.setMultiCastIP("224.0.0.1");
        config.setMultiCastPort(6789);

        P2PServers.init(config);

        for (int i = 0; i < 2; i++) {
            P2PServer server = new P2PServer("127.0.0.1", 2222 + i, new DiscoveryTable());
            server.start();
        }

        new Thread() {
            public void run() {
                while (running) {
                    TestPacket testPacket = new TestPacket();
                    testPacket.setTest("test");
                    testPacket.send();
                }
            }
        }.start();

        Thread.sleep(1000);
    }

    @Test(priority = 2, timeOut = 6000, singleThreaded = true)
    public void register() throws Exception {
        listener = new TestListener();

        PacketManager.registerPacket(TestPacket.class);
        PacketManager.registerListener(listener);

        Thread.sleep(5000);
    }

    @Test(priority = 3, timeOut = 3000, singleThreaded = true)
    public void removeListener() throws Exception {
        Assert.assertTrue(handled.get() > 300000, "Only handled " + handled.get());

        PacketManager.removeListener(listener);

        handled.set(0);
        Thread.sleep(1000);
        Assert.assertTrue(handled.get() == 0);
    }

    @AfterSuite
    public void cleanup() {
        running = false;

        for (P2PServer server : P2PServers.getServers()) {
            server.shutdown();
        }
    }

@Channel("Test")
public class TestListener extends AbstractPacketListener {
    @PacketHandler
    public void onTestMessage(TestPacket testPacket) {
        handled.incrementAndGet();
    }
}
}
