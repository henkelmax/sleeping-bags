package de.maxhenkel.sleepingbags.items;

import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.List;

public class ItemSleepingBag extends Item {

    protected DyeColor dyeColor;

    public ItemSleepingBag(DyeColor dyeColor, Properties properties) {
        super(properties.stacksTo(1));
        this.dyeColor = dyeColor;
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!BedBlock.canSetSpawn(worldIn)) {
            playerIn.displayClientMessage(Component.translatable("message.sleeping_bags.cant_sleep_here"), true);
            return InteractionResult.SUCCESS;
        }

        if (!playerIn.onGround()) {
            playerIn.displayClientMessage(Component.translatable("message.sleeping_bags.cant_sleep_in_air"), true);
            return InteractionResult.SUCCESS;
        }

        trySleep((ServerPlayer) playerIn).ifLeft((sleepResult) -> {
            if (sleepResult != null && sleepResult.getMessage() != null) {
                playerIn.displayClientMessage(sleepResult.getMessage(), true);
            }
        });

        return InteractionResult.SUCCESS;
    }

    public Either<Player.BedSleepingProblem, Unit> trySleep(ServerPlayer player) {
        Either<Player.BedSleepingProblem, Unit> ret = EventHooks.canPlayerStartSleeping(player, player.blockPosition(), Either.right(Unit.INSTANCE));
        if (ret.left().isPresent()) {
            return ret;
        }

        if (player.isSleeping() || !player.isAlive()) {
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
        }

        if (!player.level().dimensionType().natural()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
        }
        if (player.level().isBrightOutside()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
        }

        if (!player.isCreative()) {
            Vec3 vector3d = player.position();
            List<Monster> list = player.level().getEntitiesOfClass(Monster.class, new AABB(vector3d.x() - 8D, vector3d.y() - 5D, vector3d.z() - 8D, vector3d.x() + 8D, vector3d.y() + 5D, vector3d.z() + 8D), (entity) -> entity.isPreventingPlayerRest(player.level(), player));
            if (!list.isEmpty()) {
                return Either.left(Player.BedSleepingProblem.NOT_SAFE);
            }
        }

        player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        if (player.isPassenger()) {
            player.stopRiding();
        }

        player.setPose(Pose.SLEEPING);
        player.setSleepingPos(player.blockPosition());
        player.setDeltaMovement(Vec3.ZERO);
        player.hasImpulse = true;
        player.sleepCounter = 0;

        player.awardStat(Stats.SLEEP_IN_BED);
        CriteriaTriggers.SLEPT_IN_BED.trigger(player);

        player.level().updateSleepingPlayerList();
        return Either.right(Unit.INSTANCE);
    }

}