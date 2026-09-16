package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
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
        // .optional() means a client with this mod can still join a server without it.
        PayloadRegistrar registrar = event.registrar("1").optional();

        registrar.playToServer(
                StopElytraFlightPayload.TYPE,
                StopElytraFlightPayload.STREAM_CODEC,
                ElytraToggleNetwork::handleStopElytraFlight
        );
    }

    private static void handleStopElytraFlight(StopElytraFlightPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player.isFallFlying()) {
                // Clears the shared entity flag; it syncs back to every client automatically.
                player.stopFallFlying();
            }
        });
    }
}
