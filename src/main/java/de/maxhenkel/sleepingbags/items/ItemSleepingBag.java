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
        super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
        setRegistryName(new ResourceLocation(Main.MODID, dyeColor.func_176610_l() + "_sleeping_bag"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isRemote) {
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }

        if (!BedBlock.func_235330_a_(worldIn)) {
            playerIn.sendStatusMessage(new TranslationTextComponent("message.sleeping_bags.cant_sleep_here"), true);
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }

        if (!playerIn.func_233570_aj_()) {
            playerIn.sendStatusMessage(new TranslationTextComponent("message.sleeping_bags.cant_sleep_in_air"), true);
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }

        trySleep((ServerPlayerEntity) playerIn).ifLeft((sleepResult) -> {
            if (sleepResult != null && sleepResult.getMessage() != null) {
                playerIn.sendStatusMessage(sleepResult.getMessage(), true);
            }
        });

        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    public Either<PlayerEntity.SleepResult, Unit> trySleep(ServerPlayerEntity player) {
        PlayerEntity.SleepResult ret = net.minecraftforge.event.ForgeEventFactory.onPlayerSleepInBed(player, Optional.empty());
        if (ret != null) {
            return Either.left(ret);
        }

        if (player.isSleeping() || !player.isAlive()) {
            return Either.left(PlayerEntity.SleepResult.OTHER_PROBLEM);
        }

        if (!player.world.func_230315_m_().func_236043_f_()) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_HERE);
        }
        if (player.world.isDaytime()) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
        }

        if (!ForgeEventFactory.fireSleepingTimeCheck(player, Optional.empty())) {
            return Either.left(PlayerEntity.SleepResult.NOT_POSSIBLE_NOW);
        }

        if (!player.isCreative()) {
            Vector3d vector3d = player.getPositionVec();
            List<MonsterEntity> list = player.world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB(vector3d.getX() - 8D, vector3d.getY() - 5D, vector3d.getZ() - 8D, vector3d.getX() + 8D, vector3d.getY() + 5D, vector3d.getZ() + 8D), (entity) -> entity.func_230292_f_(player));
            if (!list.isEmpty()) {
                return Either.left(PlayerEntity.SleepResult.NOT_SAFE);
            }
        }

        //player.startSleeping(at);
        player.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        if (player.isPassenger()) {
            player.stopRiding();
        }

        try {
            Method setPose = ObfuscationReflectionHelper.findMethod(Entity.class, "func_213301_b", Pose.class);
            setPose.invoke(player, Pose.SLEEPING);
        } catch (Exception e) {
        }
        player.setBedPosition(player.func_233580_cy_());
        player.setMotion(Vector3d.ZERO);
        player.isAirBorne = true;

        //player.sleepTimer = 0;
        try {
            ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, player, 0, "field_71076_b");
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
        }

        if (player.world instanceof ServerWorld) {
            ((ServerWorld) player.world).updateAllPlayersSleepingFlag();
        }


        player.addStat(Stats.SLEEP_IN_BED);
        CriteriaTriggers.SLEPT_IN_BED.trigger(player);

        ((ServerWorld) player.world).updateAllPlayersSleepingFlag();
        return Either.right(Unit.INSTANCE);
    }

}