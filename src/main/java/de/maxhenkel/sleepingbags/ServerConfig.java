package de.maxhenkel.sleepingbags;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig extends ConfigBase {

    public final ForgeConfigSpec.BooleanValue onePlayerSleep;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        onePlayerSleep = builder
                .comment("If a single player sleeping should skip night")
                .define("one_player_sleep", false);
    }

}
