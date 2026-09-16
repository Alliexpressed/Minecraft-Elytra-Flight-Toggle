package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Client -> server request to cancel elytra flight.
 *
 * Vanilla has a packet for STARTING elytra flight (ServerboundPlayerCommandPacket with
 * START_FALL_FLYING) but none for stopping it, so we need our own.
 */
public record StopElytraFlightPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StopElytraFlightPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ElytraToggle.MOD_ID, "stop_elytra_flight"));

    public static final StreamCodec<ByteBuf, StopElytraFlightPayload> STREAM_CODEC =
            StreamCodec.unit(new StopElytraFlightPayload());

    @Override
    public CustomPacketPayload.Type<StopElytraFlightPayload> type() {
        return TYPE;
    }
}
