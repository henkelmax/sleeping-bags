package de.maxhenkel.sleepingbags.items;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class ModItems {

    public static ItemSleepingBag WHITE_SLEEPING_BAG = new ItemSleepingBag(DyeColor.WHITE);
    public static ItemSleepingBag ORANGE_SLEEPING_BAG = new ItemSleepingBag(DyeColor.ORANGE);
    public static ItemSleepingBag MAGENTA_SLEEPING_BAG = new ItemSleepingBag(DyeColor.MAGENTA);
    public static ItemSleepingBag LIGHT_BLUE_SLEEPING_BAG = new ItemSleepingBag(DyeColor.LIGHT_BLUE);
    public static ItemSleepingBag YELLOW_SLEEPING_BAG = new ItemSleepingBag(DyeColor.YELLOW);
    public static ItemSleepingBag LIME_SLEEPING_BAG = new ItemSleepingBag(DyeColor.LIME);
    public static ItemSleepingBag PINK_SLEEPING_BAG = new ItemSleepingBag(DyeColor.PINK);
    public static ItemSleepingBag GRAY_SLEEPING_BAG = new ItemSleepingBag(DyeColor.GRAY);
    public static ItemSleepingBag LIGHT_GRAY_SLEEPING_BAG = new ItemSleepingBag(DyeColor.LIGHT_GRAY);
    public static ItemSleepingBag CYAN_SLEEPING_BAG = new ItemSleepingBag(DyeColor.CYAN);
    public static ItemSleepingBag PURPLE_SLEEPING_BAG = new ItemSleepingBag(DyeColor.PURPLE);
    public static ItemSleepingBag BLUE_SLEEPING_BAG = new ItemSleepingBag(DyeColor.BLUE);
    public static ItemSleepingBag BROWN_SLEEPING_BAG = new ItemSleepingBag(DyeColor.BROWN);
    public static ItemSleepingBag GREEN_SLEEPING_BAG = new ItemSleepingBag(DyeColor.GREEN);
    public static ItemSleepingBag RED_SLEEPING_BAG = new ItemSleepingBag(DyeColor.RED);
    public static ItemSleepingBag BLACK_SLEEPING_BAG = new ItemSleepingBag(DyeColor.BLACK);

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                WHITE_SLEEPING_BAG,
                ORANGE_SLEEPING_BAG,
                MAGENTA_SLEEPING_BAG,
                LIGHT_BLUE_SLEEPING_BAG,
                YELLOW_SLEEPING_BAG,
                LIME_SLEEPING_BAG,
                PINK_SLEEPING_BAG,
                GRAY_SLEEPING_BAG,
                LIGHT_GRAY_SLEEPING_BAG,
                CYAN_SLEEPING_BAG,
                PURPLE_SLEEPING_BAG,
                BLUE_SLEEPING_BAG,
                BROWN_SLEEPING_BAG,
                GREEN_SLEEPING_BAG,
                RED_SLEEPING_BAG,
                BLACK_SLEEPING_BAG
        );
    }

}
