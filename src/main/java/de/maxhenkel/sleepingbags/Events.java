package de.maxhenkel.sleepingbags;

import de.maxhenkel.sleepingbags.items.ItemSleepingBag;
import de.maxhenkel.sleepingbags.items.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

public class Events {

    @SubscribeEvent
    public void sleepCheck(SleepingLocationCheckEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (InteractionHand hand : InteractionHand.values()) {
                if (player.getItemInHand(hand).getItem() instanceof ItemSleepingBag) {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCreativeModeTabBuildContents(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.register((flags, builder, hasPermissions) -> {
                for (RegistryObject<ItemSleepingBag> sleepingBag : ModItems.SLEEPING_BAGS) {
                    builder.accept(new ItemStack(sleepingBag.get()));
                }
            });
        }
    }

}
