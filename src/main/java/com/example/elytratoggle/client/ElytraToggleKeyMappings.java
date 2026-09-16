package com.example.elytratoggle.client;

import com.example.elytratoggle.ElytraToggle;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ElytraToggle.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ElytraToggleKeyMappings {

    public static final String CATEGORY = "key.categories." + ElytraToggle.MOD_ID;

    /**
     * Defaults to V. The player can rebind it in Options -> Controls -> Key Binds
     * (or clear it entirely) like any vanilla binding.
     */
    public static final KeyMapping TOGGLE_ELYTRA_FLIGHT = new KeyMapping(
            "key." + ElytraToggle.MOD_ID + ".toggle_elytra_flight",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    private ElytraToggleKeyMappings() {
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_ELYTRA_FLIGHT);
    }
}
