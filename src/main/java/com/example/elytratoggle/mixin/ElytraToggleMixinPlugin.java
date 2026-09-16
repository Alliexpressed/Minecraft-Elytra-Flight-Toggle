package com.example.elytratoggle.mixin;

import java.util.List;
import java.util.Set;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Keeps the Elytra Slot compatibility mixin from ever being applied unless Elytra Slot is
 * actually installed, so this mod works fine without it too.
 *
 * This runs during Mixin's very early config-evaluation phase, before FML's normal ModList
 * is populated - so, mirroring the pattern Elytra Slot itself uses for its own optional
 * integrations (Deeper and Darker, WaveyCapes, etc.), this checks the lower-level
 * LoadingModList instead of ModList.
 */
public final class ElytraToggleMixinPlugin implements IMixinConfigPlugin {

    private static final String ELYTRA_SLOT_MOD_ID = "elytraslot";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return FMLLoader.getLoadingModList().getModFileById(ELYTRA_SLOT_MOD_ID) != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName,
            IMixinInfo mixinInfo) {
    }
}
