package com.example.elytratoggle.server;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.ElytraToggleAttachments;
import com.example.elytratoggle.ElytraToggleUtil;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Safety net that makes "disabled" actually stick.
 *
 * Our toggle key can stop the player's own flight, but it can't stop a vanilla double-jump
 * from starting it again - that goes through the normal player-command packet and never
 * touches our code. So instead we watch every player, every tick: if flight is somehow
 * active while this player has it disabled, we cancel it immediately. In practice this means
 * the elytra flag can only ever be true for a single tick (~50ms) while disabled, which is
 * imperceptible in play but reliably prevents sustained flight.
 *
 * Only fires when the player is actually wearing a usable elytra - other mods (e.g. a Curios
 * trinket that grants its own "swim through air" movement) can reuse the same fall-flying
 * flag, and those are left alone regardless of this toggle's state.
 */
@EventBusSubscriber(modid = ElytraToggle.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ServerElytraLockHandler {

    private ServerElytraLockHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        if (player.isFallFlying()
                && !player.getData(ElytraToggleAttachments.ELYTRA_FLIGHT_ENABLED)
                && ElytraToggleUtil.isWearingUsableElytra(player)) {
            player.stopFallFlying();
        }
    }
}
