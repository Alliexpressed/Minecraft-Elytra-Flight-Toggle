package com.example.elytratoggle.compat;

import net.minecraft.core.registries.BuiltInRegistries;
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

    /** Mod ID of the Artifacts mod (adds the Helium Innertube and similar trinkets). */
    private static final String ARTIFACTS_MOD_ID = "artifacts";

    private CuriosElytraCompat() {
    }

    /**
     * True if any Curios-equipped item (in any slot, from any compat mod - e.g. one that
     * lets an elytra be worn as an accessory) is a usable elytra.
     */
    public static boolean hasUsableElytraEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.isEquipped(CuriosElytraCompat::isUsableElytra))
                .orElse(false);
    }

    /**
     * True if the player has anything from the Artifacts mod equipped in a Curios slot
     * (e.g. the Helium Innertube). Minecraft's "is gliding" flag doesn't record which item
     * granted it, and the innertube starts with the exact same double-jump gesture as an
     * elytra, so there's no reliable way to tell "this glide came from the innertube" apart
     * from "this glide came from the elytra" once both could be active. Rather than risk
     * cutting off the innertube, the elytra lock simply steps aside entirely whenever
     * anything from Artifacts is equipped.
     */
    public static boolean hasArtifactsItemEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.isEquipped(CuriosElytraCompat::isFromArtifacts))
                .orElse(false);
    }

    private static boolean isUsableElytra(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack);
    }

    private static boolean isFromArtifacts(ItemStack stack) {
        return ARTIFACTS_MOD_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
    }
}
