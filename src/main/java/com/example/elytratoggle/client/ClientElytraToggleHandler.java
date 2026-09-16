package com.example.elytratoggle.client;

import com.example.elytratoggle.ElytraToggle;
import com.example.elytratoggle.network.ToggleElytraFlightPayload;
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

        // The server owns the real on/off state (and enforces it every tick), so we just ask
        // it to flip - the "Elytra flight: ON/OFF" message comes back from the server too.
        try {
            PacketDistributor.sendToServer(new ToggleElytraFlightPayload());
        } catch (Exception e) {
            handleWithoutServerSupport(mc, player);
        }
    }

    /**
     * Fallback for servers that don't have the mod installed. There's no vanilla packet to
     * stop gliding or to lock out double-jump, so this can only ever start flight the normal
     * way; it can't enforce "off" at all here.
     */
    private static void handleWithoutServerSupport(Minecraft mc, LocalPlayer player) {
        if (player.isFallFlying()) {
            player.displayClientMessage(
                    Component.translatable("message.elytratoggle.no_server_mod"), true);
            return;
        }

        if (!canStartFlying(player)) {
            player.displayClientMessage(
                    Component.translatable("message.elytratoggle.cannot_start"), true);
            return;
        }

        if (mc.getConnection() != null) {
            mc.getConnection().send(new ServerboundPlayerCommandPacket(
                    player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
    }

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
