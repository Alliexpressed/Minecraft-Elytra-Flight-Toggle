package com.example.elytratoggle.network;

import com.example.elytratoggle.ElytraToggle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server -> client: tells the client the new toggle state so it can show the action bar
 * message locally for a fixed duration without flickering.
 */
public record ElytraToggleStatePayload(boolean enabled) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ElytraToggleStatePayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(ElytraToggle.MOD_ID, "toggle_state"));

    public static final StreamCodec<ByteBuf, ElytraToggleStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ElytraToggleStatePayload::enabled,
                    ElytraToggleStatePayload::new);

    @Override
    public CustomPacketPayload.Type<ElytraToggleStatePayload> type() {
        return TYPE;
    }
}
