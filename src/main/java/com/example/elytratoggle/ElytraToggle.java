package com.example.elytratoggle;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Elytra Toggle - adds a rebindable key that starts and stops elytra flight.
 *
 * Minecraft 1.21.1 / NeoForge 21.1.x
 */
@Mod(ElytraToggle.MOD_ID)
public class ElytraToggle {
    public static final String MOD_ID = "elytratoggle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ElytraToggle(IEventBus modBus, ModContainer container) {
        // Everything is registered through @EventBusSubscriber classes.
    }
}
