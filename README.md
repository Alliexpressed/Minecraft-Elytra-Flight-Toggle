# Elytra Toggle

A NeoForge mod for **Minecraft 1.21.1 / NeoForge 21.1.247** that adds a rebindable key for
turning elytra flight on and off.

## What it does

- Registers a key binding under **Options -> Controls -> Key Binds -> Elytra Toggle**, defaulting to **V**.
- Press it while falling with a working elytra equipped -> flight starts (same as double-tapping jump).
- Press it again while gliding -> flight stops immediately and you start falling normally.
- If you can't start flight right now (on the ground, in water, no elytra, creative flight, levitation),
  you get a short action-bar message instead of nothing happening.

## Building

The project uses ModDevGradle. It does **not** include a Gradle wrapper, so either:

```bash
gradle wrapper --gradle-version 8.10   # once, if you have Gradle installed
./gradlew build
```

or copy `gradlew`, `gradlew.bat` and `gradle/wrapper/` from the official NeoForge 1.21.1 MDK
into the project root.

The built jar lands in `build/libs/elytratoggle-1.0.0.jar`.

Run in dev with `./gradlew runClient`.

## Client / server split

| Action | Needs the mod on the server? |
|---|---|
| Starting flight | No — it sends the vanilla `START_FALL_FLYING` player command |
| Stopping flight | **Yes** — vanilla has no "stop gliding" packet, so a custom payload is used |

The custom payload is registered as `optional()` and the mod declares
`displayTest = "IGNORE_ALL_VERSION"`, so you can keep it installed and still join vanilla
or unmodded servers. On those servers the toggle only works one way.

## File map

```
src/main/java/com/example/elytratoggle/
├── ElytraToggle.java                     mod entry point
├── client/
│   ├── ElytraToggleKeyMappings.java      the key binding itself
│   └── ClientElytraToggleHandler.java    reads the key each tick, decides start vs stop
└── network/
    ├── StopElytraFlightPayload.java      empty client->server packet
    └── ElytraToggleNetwork.java          payload registration + server-side handler
```

## Renaming it

Change `mod_id`, `mod_group_id`, `mod_name` and `mod_authors` in `gradle.properties`, rename the
Java package and the `assets/elytratoggle/` resource folder to match, and update `MOD_ID` in
`ElytraToggle.java`.

## Ideas for extending it

- Add a NeoForge config (`ModConfig.Type.SERVER`) to gate the stop feature per-server.
- Make the key hold-to-glide instead of toggle by checking `isDown()` rather than `consumeClick()`.
- Play a sound or send a `LevelEvent` when flight is cancelled.
