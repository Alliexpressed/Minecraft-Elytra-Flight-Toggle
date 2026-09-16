package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.ElytraToggleAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ElytraToggle.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ElytraToggleNetwork {

    private ElytraToggleNetwork() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        // .optional() means a client with this mod can still join a server without it -
        // it just falls back to client-only behavior (see ClientElytraToggleHandler).
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToServer(
                ToggleElytraFlightPayload.TYPE,
                ToggleElytraFlightPayload.STREAM_CODEC,
                ElytraToggleNetwork::handleToggleElytraFlight
        );
    }

    private static void handleToggleElytraFlight(ToggleElytraFlightPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player playerGeneric = context.player();
            if (!(playerGeneric instanceof ServerPlayer player)) {
                return;
            }

            boolean newEnabled = !player.getData(ElytraToggleAttachments.ELYTRA_FLIGHT_ENABLED);
            player.setData(ElytraToggleAttachments.ELYTRA_FLIGHT_ENABLED, newEnabled);

            if (newEnabled) {
                if (!player.isFallFlying()) {
                    // Same check vanilla runs on double-jump (elytra equipped and usable,
                    // airborne, not already flying, no levitation, etc). If conditions aren't
                    // met right now, this just re-arms things for the next time they fall.
                    player.tryToStartFallFlying();
                }
                player.displayClientMessage(Component.translatable("message.elytratoggle.on"), true);
            } else {
                if (player.isFallFlying()) {
                    player.stopFallFlying();
                }
                player.displayClientMessage(Component.translatable("message.elytratoggle.off"), true);
            }
        });
    }
}
