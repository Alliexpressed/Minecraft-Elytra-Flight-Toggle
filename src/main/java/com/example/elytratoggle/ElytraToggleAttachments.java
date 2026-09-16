package com.example.elytratoggle;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Per-player "is elytra flight currently allowed" flag.
 *
 * This is intentionally NOT persisted to disk (no .serialize(...) on the builder) - it
 * resets to enabled (true) on login/respawn, matching vanilla's default behavior until the
 * player chooses to disable it for that session.
 */
public final class ElytraToggleAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ElytraToggle.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> ELYTRA_FLIGHT_ENABLED =
            ATTACHMENT_TYPES.register("elytra_flight_enabled",
                    () -> AttachmentType.builder(() -> Boolean.TRUE).build());

    private ElytraToggleAttachments() {
    }
}
