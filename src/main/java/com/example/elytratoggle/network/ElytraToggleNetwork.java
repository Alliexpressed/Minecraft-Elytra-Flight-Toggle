package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.ElytraToggleAttachments;
import com.example.elytratoggle.ElytraToggleUtil;
import com.example.elytratoggle.client.ElytraToggleMessageHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
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

        // Server -> client: registered once here (both sides) so NeoForge knows the
        // payload is valid. The handler delegates to the client message handler, guarded
        // by a dist check so the server never touches client-only classes.
        registrar.playToClient(
                ElytraToggleStatePayload.TYPE,
                ElytraToggleStatePayload.STREAM_CODEC,
                ElytraToggleNetwork::handleToggleState
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

            // Send the new state to the client so it can display the action bar message.
            PacketDistributor.sendToPlayer(player, new ElytraToggleStatePayload(newEnabled));
        });
    }

    private static void handleToggleState(ElytraToggleStatePayload payload, IPayloadContext context) {
        // This packet only ever travels server->client, so this handler only runs on the
        // client. Guard with a dist check so the server never loads ElytraToggleMessageHandler
        // (a client-only class).
        if (FMLEnvironment.dist == Dist.CLIENT) {
            context.enqueueWork(() -> ElytraToggleMessageHandler.receiveToggleState(payload.enabled()));
        }
    }
}
