package net.cubespace.ComuCator.P2P;

import net.cubespace.ComuCator.Packet.ByteHolder;
import net.cubespace.ComuCator.Packet.DefinedPacket;
import net.cubespace.ComuCator.Packet.ProtocolHandler;
import net.cubespace.ComuCator.Util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ClientRunnable extends Thread {
    private P2PClient client;

    public ClientRunnable(P2PClient client) {
        this.client = client;

        setName("Client-" + client.getSocket().getRemoteSocketAddress());
    }

    @Override
    public void run() {
        while(!client.getSocket().isClosed()) {
            try {
                if (client.getSocket().getInputStream().available() > 0 || client.getQueue().size() > 0 || client.getByteQueue().size() > 0) {
                    if (client.getSocket().getInputStream().available() > 0) {
                        while (client.getSocket().getInputStream().available() > 0) {
                            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(DefinedPacket.readArray(client.getSocket().getInputStream())));
                            client.getPacketHandler().handle(ProtocolHandler.readPacket(dataInputStream));
                            dataInputStream.close();
                        }
                    }

                    if (client.getByteQueue().size() > 0) {
                        synchronized (client.getByteQueue()) {
                            while (client.getByteQueue().size() > 0) {
                                ByteHolder byteHolder = client.getByteQueue().poll();
                                client.getDataOutputStream().write(byteHolder.getBytes());
                            }
                        }
                    }

                    if (client.getQueue().size() > 0) {
                        synchronized (client.getQueue()) {
                            while (client.getQueue().size() > 0) {
                                DefinedPacket packet = client.getQueue().poll();

                                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                                DataOutputStream dataOutputStream1 = new DataOutputStream(bao);
                                byte packetID = ProtocolHandler.getPacketID(packet);
                                dataOutputStream1.write(packetID);
                                packet.handle(dataOutputStream1);
                                dataOutputStream1.close();

                                DefinedPacket.writeArray(bao.toByteArray(), client.getDataOutputStream());
                            }
                        }
                    }
                }

                Thread.sleep(5);
            } catch (Exception e) {
                Logger.error("Could not tick client", e);
                client.getServer().removeClient(client);
            }
        }

        client.getServer().removeClient(client);
    }
}
