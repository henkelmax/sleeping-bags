package de.maxhenkel.sleepingbags.items;

import com.mojang.datafixers.util.Either;
import de.maxhenkel.sleepingbags.Main;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BedBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class ItemSleepingBag extends Item {

    public ItemSleepingBag(DyeColor dyeColor) {
        super(new Properties().tab(ItemGroup.TAB_MISC).stacksTo(1));
        setRegistryName(new ResourceLocation(Main.MODID, dyeColor.getName() + "_sleeping_bag"));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isClientSide) {
            return ActionResult.success(playerIn.getItemInHand(handIn));
        }

        if (!BedBlock.canSetSpawn(worldIn)) {
            playerIn.displayClientMessage(new TranslationTextComponent("message.sleeping_bags.cant_sleep_here"), true);
            return ActionResult.success(playerIn.getItemInHand(handIn));
        }

        if (!playerIn.isOnGround()) {
            playerIn.displayClientMessage(new TranslationTextComponent("message.sleeping_bags.cant_sleep_in_air"), true);
            return ActionResult.success(playerIn.getItemInHand(handIn));
        }

        trySleep((ServerPlayerEntity) playerIn).ifLeft((sleepResult) -> {
            if (sleepResult != null && sleepResult.getMessage() != null) {
                playerIn.displayClientMessage(sleepResult.getMessage(), true);
            }
        });

        return ActionResult.success(playerIn.getItemInHand(handIn));
    }

    public Either<PlayerEntity.SleepResult, Unit> trySleep(ServerPlayerEntity player) {
        PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, Optional.empty());
        if (ret != null) {
            return Either.left(ret);
        }

        if (player.isSleeping() || !player.isAlive()) {
            return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
        }

        if (!player.level.dimensionType().natural()) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
        }
        if (player.level.isDay()) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
        }

        if (!ForgeEventFactory.fireSleepingTimeCheck(player, Optional.empty())) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
        }

        if (!player.isCreative()) {
            Vector3d vector3d = player.position();
            List<MonsterEntity> list = player.level.getEntitiesOfClass(MonsterEntity.class, new AxisAlignedBB(vector3d.x() - 8D, vector3d.y() - 5D, vector3d.z() - 8D, vector3d.x() + 8D, vector3d.y() + 5D, vector3d.z() + 8D), (entity) -> entity.isPreventingPlayerRest(player));
            if (!list.isEmpty()) {
                return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
            }
        }

        //player.startSleeping(at);
        player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        if (player.isPassenger()) {
            player.stopRiding();
        }

        try {
            Method setPose = ObfuscationReflectionHelper.findMethod(Entity.class, "func_213301_b", Pose.class);
            setPose.invoke(player, Pose.SLEEPING);
        } catch (Exception e) {
        }
        player.setSleepingPos(player.blockPosition());
        player.setDeltaMovement(Vector3d.ZERO);
        player.hasImpulse = true;

        //player.sleepTimer = 0;
        try {
            ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, player, 0, "field_71076_b");
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
        }

        if (player.level instanceof ServerWorld) {
            ((ServerWorld) player.level).updateSleepingPlayerList();
        }


        player.awardStat(Stats.SLEEP_IN_BED);
        CriteriaTriggers.SLEPT_IN_BED.trigger(player);

        ((ServerWorld) player.level).updateSleepingPlayerList();
        return Either.right(Unit.INSTANCE);
    }

}