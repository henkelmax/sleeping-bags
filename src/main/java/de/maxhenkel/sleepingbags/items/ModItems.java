package de.maxhenkel.sleepingbags.items;

import de.maxhenkel.sleepingbags.Main;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(BuiltInRegistries.ITEM, Main.MODID);

    public static final DeferredHolder<Item, ItemSleepingBag>[] SLEEPING_BAGS;

    static {
        SLEEPING_BAGS = new DeferredHolder[DyeColor.values().length];
        for (int i = 0; i < DyeColor.values().length; i++) {
            SLEEPING_BAGS[i] = registerSleepingBag(DyeColor.values()[i]);
        }
    }

    public static void init(IEventBus eventBus) {
        ITEM_REGISTER.register(eventBus);
    }

    private static DeferredHolder<Item, ItemSleepingBag> registerSleepingBag(DyeColor dyeColor) {
        return ITEM_REGISTER.register(dyeColor.getName() + "_sleeping_bag", () -> new ItemSleepingBag(dyeColor));
    }

}
