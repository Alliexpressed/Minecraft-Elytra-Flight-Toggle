package com.example.elytratoggle.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * All Curios API references live in this one class, and it is only ever touched from
 * ElytraToggleUtil after that class has confirmed Curios is actually loaded (see
 * ElytraToggleUtil.CURIOS_LOADED). The JVM only resolves a class's imports the first time
 * something in it actually runs, so on a setup without Curios installed, this class is
 * simply never loaded and never causes a crash.
 */
public final class CuriosElytraCompat {

    private CuriosElytraCompat() {
    }

    /**
     * True if any Curios-equipped item (in any slot, from any compat mod - e.g. one that
     * lets an elytra be worn as an accessory) is a usable elytra. Reads the Curios inventory
     * directly, bypassing any platform layer that might be patched by other mods.
     */
    public static boolean hasUsableElytraEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.isEquipped(CuriosElytraCompat::isUsableElytra))
                .orElse(false);
    }

    private static boolean isUsableElytra(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack);
    }
}
