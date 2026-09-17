package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.ElytraToggleAttachments;
import com.example.elytratoggle.ElytraToggleUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ElytraToggle.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ElytraToggleNetwork {

    private ElytraToggleNetwork() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();

        // Client -> server: player pressed the toggle key.
        registrar.playToServer(
                ToggleElytraFlightPayload.TYPE,
                ToggleElytraFlightPayload.STREAM_CODEC,
                ElytraToggleNetwork::handleToggleElytraFlight
        );

        // Server -> client: registered on the server side so it can send it;
        // the actual handling is in ElytraToggleMessageHandler on the client.
        registrar.playToClient(
                ElytraToggleStatePayload.TYPE,
                ElytraToggleStatePayload.STREAM_CODEC,
                (payload, context) -> { /* handled client-side in ElytraToggleMessageHandler */ }
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

            if (!newEnabled) {
                if (player.isFallFlying() && ElytraToggleUtil.shouldEnforceElytraLock(player)) {
                    player.stopFallFlying();
                }
            }

            // Send the new state back to the client so it can show a non-flickering
            // action bar message without needing to resend it every tick.
            PacketDistributor.sendToPlayer(player, new ElytraToggleStatePayload(newEnabled));
        });
    }
}
