package de.maxhenkel.sleepingbags;

import de.maxhenkel.sleepingbags.items.ItemSleepingBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Events {

    @SubscribeEvent
    public void sleepTick(TickEvent.PlayerTickEvent event) {
        if(!Config.SERVER.ONE_PLAYER_SLEEP.get()){
            return;
        }
        if (event.player.world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) event.player.world;
            if (event.player.isSleeping()) {
                if (event.player.getSleepTimer() >= 100) {
                    if (serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                        long l = serverWorld.getDayTime() + 24000L;
                        serverWorld.setDayTime(l - l % 24000L);
                    }

                    serverWorld.getPlayers().stream().filter(LivingEntity::isSleeping).forEach((playerEntity) -> {
                        //playerEntity.wakeUpPlayer(false, false, true);
                        playerEntity.wakeUp();
                    });
                    if (serverWorld.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                        serverWorld.getDimension().resetRainAndThunder();
                    }

                    serverWorld.getServer().getPlayerList().sendMessage(new TranslationTextComponent("message.sleep", event.player.getDisplayName()).applyTextStyle(TextFormatting.YELLOW));
                }
            }
        }
    }

    @SubscribeEvent
    public void sleepCheck(SleepingLocationCheckEvent event) {
        if(!Config.SERVER.ONE_PLAYER_SLEEP.get()){
            return;
        }
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
            for (Hand hand : Hand.values()) {
                if (playerEntity.getHeldItem(hand).getItem() instanceof ItemSleepingBag) {
                    event.setResult(Event.Result.ALLOW);
                    return;
                }
            }
        }
    }


}
