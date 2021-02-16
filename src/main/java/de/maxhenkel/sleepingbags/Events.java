package de.maxhenkel.sleepingbags;

import de.maxhenkel.sleepingbags.items.ItemSleepingBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;

public class Events {

    public static final Method RESET_RAIN_AND_THUNDER = ObfuscationReflectionHelper.findMethod(ServerWorld.class, "func_73051_P");

    @SubscribeEvent
    public void sleepTick(TickEvent.PlayerTickEvent event) {
        if (!Main.SERVER_CONFIG.onePlayerSleep.get()) {
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

                    serverWorld.getPlayers().stream().filter(LivingEntity::isSleeping).forEach(PlayerEntity::wakeUp);
                    if (serverWorld.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
                        try {
                            RESET_RAIN_AND_THUNDER.invoke(serverWorld);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    serverWorld.getServer().getPlayerList().func_232641_a_(new TranslationTextComponent("message.sleeping_bags.sleep", event.player.getDisplayName()).mergeStyle(TextFormatting.YELLOW), ChatType.SYSTEM, Util.DUMMY_UUID);
                }
            }
        }
    }

    @SubscribeEvent
    public void sleepCheck(SleepingLocationCheckEvent event) {
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
