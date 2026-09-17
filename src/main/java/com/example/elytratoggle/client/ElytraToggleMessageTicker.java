package com.example.elytratoggle.client;

import com.example.elytratoggle.ElytraToggle;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Counts down the action bar message timer set by ElytraToggleMessageHandler and sends the
 * displayClientMessage call once per toggle (not every tick) so it doesn't flicker.
 */
@EventBusSubscriber(modid = ElytraToggle.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ElytraToggleMessageTicker {

    private ElytraToggleMessageTicker() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (ElytraToggleMessageHandler.messageTicksRemaining <= 0
                || ElytraToggleMessageHandler.pendingMessage == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        // Show the message only on the first tick so the game's own 40-tick action bar
        // timer holds it visible, rather than resetting it every tick.
        if (ElytraToggleMessageHandler.messageTicksRemaining
                == ElytraToggleMessageHandler.MESSAGE_DURATION_TICKS) {
            mc.player.displayClientMessage(ElytraToggleMessageHandler.pendingMessage, true);
        }
        ElytraToggleMessageHandler.messageTicksRemaining--;
    }
}
