package net.cubespace.ComuCator;

import net.cubespace.ComuCator.Config.Main;
import net.cubespace.ComuCator.Util.Logger;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class ComuCatorPlugin extends Plugin {
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
}
