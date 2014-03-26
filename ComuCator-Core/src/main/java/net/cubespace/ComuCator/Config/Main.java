package net.cubespace.ComuCator.Config;

import lombok.Getter;
import lombok.Setter;
import net.cubespace.Yamler.Config.Config;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Main extends Config {
    private @Getter @Setter String ip = "0.0.0.0";
    private @Getter @Setter Integer port = 2222;
    private @Getter @Setter String networkInterface = "";
    private @Getter @Setter String multiCastIP = "224.0.0.1";
    private @Getter @Setter Integer multiCastPort = 6789;
}
