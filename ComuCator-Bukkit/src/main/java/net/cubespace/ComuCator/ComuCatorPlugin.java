package net.cubespace.ComuCator;

import net.cubespace.ComuCator.Config.Main;
import net.cubespace.ComuCator.P2P.P2PServer;
import net.cubespace.ComuCator.P2P.P2PServers;
import net.cubespace.ComuCator.Util.Logger;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ComuCatorPlugin extends JavaPlugin {
    public void onEnable() {
        // Start the Core
        Main config = new Main();

        try {
            config.init(new File(getDataFolder(), "config.yml"));
            Starter.start(config);
        } catch (InvalidConfigurationException e) {
            Logger.error("Could not init Config", e);
        }

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().info("Could not start metrics");
        }
    }

    public void onDisable() {
        for (P2PServer server : P2PServers.getServers()) {
            server.shutdown();
        }
    }
}
