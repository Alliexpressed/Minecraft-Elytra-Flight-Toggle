package com.example.elytratoggle.client;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.network.StopElytraFlightPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ElytraToggle.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ClientElytraToggleHandler {

    private ClientElytraToggleHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.screen != null) {
            return;
        }

        // Drain the click queue so a burst of presses counts as one toggle.
        boolean pressed = false;
        while (ElytraToggleKeyMappings.TOGGLE_ELYTRA_FLIGHT.consumeClick()) {
            pressed = true;
        }
        if (!pressed) {
            return;
        }

        if (player.isFallFlying()) {
            stopFlying(player);
        } else {
            startFlying(mc, player);
        }
    }

    private static void startFlying(Minecraft mc, LocalPlayer player) {
        if (!canStartFlying(player)) {
            player.displayClientMessage(
                    Component.translatable("message.elytratoggle.cannot_start"), true);
            return;
        }

        // This is the exact packet vanilla sends when you double-tap jump mid-air,
        // so starting works even on servers that don't have this mod installed.
        if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundPlayerCommandPacket(
                    player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
    }

    private static void stopFlying(LocalPlayer player) {
        try {
            PacketDistributor.sendToServer(new StopElytraFlightPayload());
        } catch (Exception e) {
            // Server doesn't have the mod, so there's no way to ask it to stop.
            player.displayClientMessage(
                    Component.translatable("message.elytratoggle.no_server_mod"), true);
        }
    }

    /**
     * Mirrors the conditions vanilla checks in LivingEntity#tryToStartFallFlying and
     * LocalPlayer#aiStep, so we don't spam the server with requests it will reject.
     * The server re-checks all of this anyway.
     */
    private static boolean canStartFlying(LocalPlayer player) {
        if (player.onGround()
                || player.isFallFlying()
                || player.isInWater()
                || player.getAbilities().flying
                || player.isPassenger()
                || player.hasEffect(MobEffects.LEVITATION)) {
            return false;
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(chest);
    }
}
