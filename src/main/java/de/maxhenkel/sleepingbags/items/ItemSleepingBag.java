package de.maxhenkel.sleepingbags.items;

import de.maxhenkel.sleepingbags.Main;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeDimension;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.Optional;

public class ItemSleepingBag extends Item {

    public ItemSleepingBag(DyeColor dyeColor) {
        super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
        setRegistryName(new ResourceLocation(Main.MODID, dyeColor.getName() + "_sleeping_bag"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isRemote) {
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }
        IForgeDimension.SleepResult sleepResult = worldIn.dimension.canSleepAt(playerIn, playerIn.getPosition());

        if (sleepResult.equals(IForgeDimension.SleepResult.DENY) || sleepResult.equals(IForgeDimension.SleepResult.BED_EXPLODES)) {
            playerIn.sendStatusMessage(new TranslationTextComponent("message.cant_sleep_here"), true);
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }

        if (!playerIn.onGround) {
            playerIn.sendStatusMessage(new TranslationTextComponent("message.cant_sleep_in_air"), true);
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        }

        PlayerEntity.SleepResult sleepResult1 = trySleep(playerIn);
        if (sleepResult1 != null) {
            playerIn.sendStatusMessage(sleepResult1.getMessage(), true);
        }

        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    public PlayerEntity.SleepResult trySleep(PlayerEntity player) {
        PlayerEntity.SleepResult ret = ForgeEventFactory.onPlayerSleepInBed(player, Optional.empty());
        if (ret != null) {
            return ret;
        }
        BlockPos pos = player.getPosition();
        if (player.isSleeping() || !player.isAlive()) {
            return PlayerEntity.SleepResult.OTHER_PROBLEM;
        }

        if (!player.world.dimension.isSurfaceWorld()) {
            return PlayerEntity.SleepResult.NOT_POSSIBLE_HERE;
        }

        if (!ForgeEventFactory.fireSleepingTimeCheck(player, Optional.empty())) {
            return PlayerEntity.SleepResult.NOT_POSSIBLE_NOW;
        }

        if (!player.isCreative()) {
            double width = 8D;
            double height = 5D;
            if (!player.world.getEntitiesWithinAABB(MonsterEntity.class, new AxisAlignedBB(pos.getX() - width, pos.getY() - height, pos.getZ() - width, pos.getX() + width, pos.getY() + height, pos.getZ() + width), monsterEntity -> monsterEntity.isPreventingPlayerRest(player)).isEmpty()) {
                return PlayerEntity.SleepResult.NOT_SAFE;
            }
        }

        //player.startSleeping(player.getPosition());
        player.takeStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        if (player.isPassenger()) {
            player.stopRiding();
        }
        //player.setPose(Pose.SLEEPING);
        try {
            Method setPose = ObfuscationReflectionHelper.findMethod(Entity.class, "func_213301_b", Pose.class);
            setPose.invoke(player, Pose.SLEEPING);
        } catch (Exception x) {
            try {
                Method setPose1 = ObfuscationReflectionHelper.findMethod(Entity.class, "setPose", Pose.class);
                setPose1.invoke(player, Pose.SLEEPING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        player.setBedPosition(pos);
        player.setMotion(Vec3d.ZERO);
        player.isAirBorne = true;


        try {
            ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, player, 0, "field_71076_b");
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException x) {
            try {
                ObfuscationReflectionHelper.setPrivateValue(PlayerEntity.class, player, 0, "sleepTimer");
            } catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
                e.printStackTrace();
            }
        }

        if (player.world instanceof ServerWorld) {
            ((ServerWorld) player.world).updateAllPlayersSleepingFlag();
        }

        return null;
    }
}
