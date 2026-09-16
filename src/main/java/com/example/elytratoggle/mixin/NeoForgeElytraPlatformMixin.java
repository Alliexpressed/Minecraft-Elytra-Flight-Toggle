package com.example.elytratoggle.mixin;

import com.example.elytratoggle.ElytraToggleAttachments;
import com.illusivesoulworks.elytraslot.platform.NeoForgeElytraPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Elytra Slot decides "does this player have an elytra" (checked before granting flight, in
 * chest slot or its own Curios "back" slot) entirely through
 * NeoForgeElytraPlatform#getEquipped. Reporting an empty stack here whenever this mod's
 * toggle is off makes Elytra Slot itself treat the elytra as not equipped - which is what
 * lets other Curios accessories (e.g. Artifacts' Helium Innertube) win the same double-jump
 * gesture instead, without physically touching the elytra's slot.
 *
 * Server-side only: the server is authoritative for whether flight is allowed, and letting
 * the client optimistically try (then get corrected, same as vanilla already does elsewhere)
 * avoids needing to sync this mod's toggle state to every client.
 */
@Mixin(NeoForgeElytraPlatform.class)
public abstract class NeoForgeElytraPlatformMixin {

    @Inject(method = "getEquipped", at = @At("HEAD"), cancellable = true, remap = false)
    private void elytratoggle$hideDisabledElytra(LivingEntity livingEntity,
            CallbackInfoReturnable<ItemStack> cir) {
        if (livingEntity.level().isClientSide()) {
            return;
        }
        if (!(livingEntity instanceof Player player)) {
            return;
        }
        if (!player.getData(ElytraToggleAttachments.ELYTRA_FLIGHT_ENABLED)) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
