package de.maxhenkel.sleepingbags;

import de.maxhenkel.sleepingbags.items.ItemSleepingBag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {

    @SubscribeEvent
    public void sleepCheck(SleepingLocationCheckEvent event) {
        if (event.getEntityLiving() instanceof Player) {
            Player playerEntity = (Player) event.getEntityLiving();
            for (InteractionHand hand : InteractionHand.values()) {
                if (playerEntity.getItemInHand(hand).getItem() instanceof ItemSleepingBag) {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
        }
    }

}
