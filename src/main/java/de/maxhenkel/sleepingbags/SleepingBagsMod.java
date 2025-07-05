package de.maxhenkel.sleepingbags;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.sleepingbags.items.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SleepingBagsMod.MODID)
@EventBusSubscriber(modid = SleepingBagsMod.MODID)
public class SleepingBagsMod {

    public static final String MODID = "sleeping_bags";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ServerConfig SERVER_CONFIG;

    public SleepingBagsMod(IEventBus eventBus) {
        eventBus.addListener(Events::onCreativeModeTabBuildContents);

        SERVER_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.SERVER, ServerConfig.class);

        ModItems.init(eventBus);
    }

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new Events());
    }

}
