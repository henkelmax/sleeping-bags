package de.maxhenkel.sleepingbags.mixin;

import de.maxhenkel.sleepingbags.items.ItemSleepingBag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
    private void checkSleepingBag(CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof Player player)) {
            return;
        }

        for (InteractionHand hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).getItem() instanceof ItemSleepingBag) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

}
