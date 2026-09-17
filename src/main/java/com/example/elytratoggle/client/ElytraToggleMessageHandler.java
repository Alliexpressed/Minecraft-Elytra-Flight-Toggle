package com.example.elytratoggle.client;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.network.ElytraToggleStatePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Receives the toggle-state packet from the server and shows the action bar message for a
 * fixed duration. The payload is registered here (mod bus); ticking happens in
 * ElytraToggleMessageTicker (game bus) to keep the two event buses in separate classes.
 */
@EventBusSubscriber(modid = ElytraToggle.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ElytraToggleMessageHandler {

    /** How many ticks to keep the message visible (~3 seconds). */
    static final int MESSAGE_DURATION_TICKS = 60;

    static int messageTicksRemaining = 0;
    static Component pendingMessage = null;

    private ElytraToggleMessageHandler() {
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToClient(
                ElytraToggleStatePayload.TYPE,
                ElytraToggleStatePayload.STREAM_CODEC,
                ElytraToggleMessageHandler::handleToggleState
        );
    }

    private static void handleToggleState(ElytraToggleStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            pendingMessage = payload.enabled()
                    ? Component.translatable("message.elytratoggle.on")
                    : Component.translatable("message.elytratoggle.off");
            messageTicksRemaining = MESSAGE_DURATION_TICKS;
        });
    }
}
