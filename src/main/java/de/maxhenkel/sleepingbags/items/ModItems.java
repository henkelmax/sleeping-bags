package de.maxhenkel.sleepingbags.items;

import de.maxhenkel.sleepingbags.Main;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject<ItemSleepingBag>[] SLEEPING_BAGS;

    static {
        SLEEPING_BAGS = new RegistryObject[DyeColor.values().length];
        for (int i = 0; i < DyeColor.values().length; i++) {
            SLEEPING_BAGS[i] = registerSleepingBag(DyeColor.values()[i]);
        }
    }

    public static void init() {
        ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static RegistryObject<ItemSleepingBag> registerSleepingBag(DyeColor dyeColor) {
        return ITEM_REGISTER.register(dyeColor.getName() + "_sleeping_bag", () -> new ItemSleepingBag(dyeColor));
    }

}
