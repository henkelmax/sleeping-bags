package de.maxhenkel.sleepingbags;

import de.maxhenkel.sleepingbags.items.ItemSleepingBag;
import de.maxhenkel.sleepingbags.items.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class Events {

    @SubscribeEvent
    public void sleepCheck(CanContinueSleepingEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (InteractionHand hand : InteractionHand.values()) {
                if (player.getItemInHand(hand).getItem() instanceof ItemSleepingBag) {
                    event.setContinueSleeping(true);
                    return;
                }
            }
        }
    }

    public static void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            for (DeferredHolder<Item, ItemSleepingBag> sleepingBag : ModItems.SLEEPING_BAGS) {
                event.accept(new ItemStack(sleepingBag.get()));
            }
        }
    }

}
