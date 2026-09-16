package com.example.elytratoggle;

import com.example.elytratoggle.compat.CuriosElytraCompat;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * Shared checks so the "elytra flight disabled" lock only ever touches actual elytra flight -
 * not other mods' movement abilities (a Curios trinket that grants its own flight/swimming,
 * for example) that happen to reuse the same underlying "fall flying" flag.
 */
public final class ElytraToggleUtil {

    /**
     * Checked once at startup. Curios' own API classes are only ever referenced from
     * CuriosElytraCompat, and that class is only ever loaded when this is true, so a setup
     * without Curios installed never touches Curios code at all.
     */
    private static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

    private ElytraToggleUtil() {
    }

    /**
     * True if the player has a working elytra equipped - in the vanilla chest slot, or (if
     * Curios is installed) in a Curios accessory slot, covering compat mods that let an
     * elytra be worn that way.
     *
     * Checking "instanceof ElytraItem" rather than the specific vanilla item also picks up
     * modded elytra variants (e.g. Soul Elytra from Deeper and Darker) for free: vanilla's own
     * double-jump gliding only ever recognizes items that extend that class, so any item that
     * actually behaves like a real elytra has to extend it too.
     */
    public static boolean isWearingUsableElytra(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (isUsableElytra(chest)) {
            return true;
        }
        return CURIOS_LOADED && CuriosElytraCompat.hasUsableElytraEquipped(player);
    }

    private static boolean isUsableElytra(ItemStack stack) {
        return stack.getItem() instanceof ElytraItem && ElytraItem.isFlyEnabled(stack);
    }
}
