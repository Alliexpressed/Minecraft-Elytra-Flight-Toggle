package com.example.elytratoggle.client;

import net.minecraft.network.chat.Component;

/**
 * Holds the action bar message state for the toggle. Called from ElytraToggleNetwork when the
 * server sends a toggle-state packet. The actual display tick is in ElytraToggleMessageTicker.
 */
public final class ElytraToggleMessageHandler {

    /** How many ticks to keep the message visible (~3 seconds). */
    static final int MESSAGE_DURATION_TICKS = 60;

    static int messageTicksRemaining = 0;
    static Component pendingMessage = null;

    private ElytraToggleMessageHandler() {
    }

    /** Called from ElytraToggleNetwork on the client side when a toggle-state packet arrives. */
    public static void receiveToggleState(boolean enabled) {
        pendingMessage = enabled
                ? Component.translatable("message.elytratoggle.on")
                : Component.translatable("message.elytratoggle.off");
        messageTicksRemaining = MESSAGE_DURATION_TICKS;
    }
}
