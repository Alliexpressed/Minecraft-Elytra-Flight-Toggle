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
 * fixed number of ticks. Refreshing a single local display avoids the flicker caused by
 * re-sending displayClientMessage every tick.
 */
@EventBusSubscriber(modid = ElytraToggle.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ElytraToggleMessageHandler {

    /** How many ticks to keep the message visible. 60 = 3 seconds. */
    private static final int MESSAGE_DURATION_TICKS = 60;

    /** Counts down each tick; message is shown while > 0. */
    private static int messageTicksRemaining = 0;
    private static Component pendingMessage = null;

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

    // On the GAME bus to tick the countdown
    @EventBusSubscriber(modid = ElytraToggle.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class Ticker {
        private Ticker() {
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (messageTicksRemaining <= 0 || pendingMessage == null) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            // Only display on the first tick of the countdown to set it, then let the
            // game's own action bar timer hold it visible for its natural 40-tick duration.
            // We reset on each toggle by setting messageTicksRemaining again.
            if (messageTicksRemaining == MESSAGE_DURATION_TICKS) {
                mc.player.displayClientMessage(pendingMessage, true);
            }
            messageTicksRemaining--;
        }
    }
}
