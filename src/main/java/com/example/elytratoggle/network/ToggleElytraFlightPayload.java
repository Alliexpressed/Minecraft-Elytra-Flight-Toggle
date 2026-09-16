package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Client -> server: flip whether elytra flight is currently allowed for this player.
 *
 * The server is the single source of truth. It flips its own stored flag, forces flight to
 * stop immediately if it's being disabled mid-glide, and replies with an action-bar message -
 * the client never guesses the state locally.
 */
public record ToggleElytraFlightPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleElytraFlightPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ElytraToggle.MOD_ID, "toggle_elytra_flight"));

    public static final StreamCodec<ByteBuf, ToggleElytraFlightPayload> STREAM_CODEC =
            StreamCodec.unit(new ToggleElytraFlightPayload());

    @Override
    public CustomPacketPayload.Type<ToggleElytraFlightPayload> type() {
        return TYPE;
    }
}
