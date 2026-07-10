# AstraControl

AstraControl is a modern, all-in-one **server control center** for Minecraft servers. It gives owners, admins, and staff a clean in-game GUI (plus full command support) for monitoring server health, plugin status, integration hooks, online players, recent errors, maintenance mode, broadcasts, PlaceholderAPI testing, and permission debugging.

## Why AstraControl is not Essentials

AstraControl is **not** an EssentialsX replacement and does not try to be. It has no economy, no homes/warps/kits, no gameplay commands, and no player-facing convenience commands. It is a diagnostics and operations dashboard: something you open when you need to know *what is broken, what is lagging, what hooks are missing, and whether it's safe to reload or restart* - not a plugin your average player interacts with day to day.

Run AstraControl alongside EssentialsX, not instead of it.

## Installation

1. Drop `AstraControl-<version>.jar` into your server's `plugins/` folder.
2. Start (or restart) the server.
3. Edit `plugins/AstraControl/config.yml`, `gui.yml`, `hooks.yml`, and `language/en.yml` as needed.
4. Run `/astractrl reload` to apply changes without a restart.

AstraControl works completely standalone - every integration below is optional.

## Supported Platforms

- Paper 1.18.2+
- Spigot 1.18.2+
- Purpur 1.18.2+
- Folia 1.19+
- Latest Paper/Purpur/Folia builds where possible

## Supported Minecraft Versions

1.18.2 and newer. AstraControl targets the modern Paper API (compiled against 1.20.4) but degrades gracefully on older/Spigot-only servers - features that depend on newer API (per-player ping, client brand, MSPT) simply report as "unavailable" rather than crashing.

## Commands

See [COMMANDS.md](COMMANDS.md) for the full list. Highlights:

- `/astractrl` (aliases: `/acontrol`, `/ac`) - opens the control center GUI.
- `/astractrl health` / `tps` / `memory` / `worlds` / `chunks` / `entities` - server diagnostics.
- `/astractrl plugins` / `plugin <plugin>` / `hooks` / `dependencies` - plugin & integration status.
- `/astractrl errors` / `errors clear` / `errors export` - the error watcher.
- `/astractrl maintenance on|off|status` - maintenance mode.
- `/astractrl broadcast` / `title` / `actionbar <message>` - broadcast center.
- `/astractrl papi ...` - PlaceholderAPI testing.
- `/astractrl debug player|permission|command ...` - permission/player debugging.
- `/astractrl player` / `playerinfo` / `stafftools <player>` - player tools.

## Permissions

See [PERMISSIONS.md](PERMISSIONS.md) for the full list and wildcard nodes.

## Optional Integrations

AstraControl soft-depends on, and detects, the following - see [INTEGRATIONS.md](INTEGRATIONS.md) for details on exactly what each hook does:

PlaceholderAPI, LuckPerms, Vault, ProtocolLib, WorldGuard, Citizens, ItemsAdder, Oraxen, Nexo, HeadDatabase, PlayerPoints, ViaVersion, TAB, EssentialsX.

None of these are required. AstraControl never claims support it doesn't actually implement - most integrations beyond PlaceholderAPI/LuckPerms are detected for status/GUI display purposes only.

## PlaceholderAPI Testing

The Placeholder Tester (GUI menu and `/astractrl papi`) lets you pick a player, enter a placeholder or block of text, and see the raw, plain, MiniMessage, and legacy-color representations of the result - useful for debugging broken placeholders without digging through configs.

## LuckPerms Permission Debugging

The Permission Debugger (GUI menu and `/astractrl debug permission|player`) checks a player's effective permission, and - when LuckPerms is installed - shows the source (direct assignment vs. a specific group) and the player's groups. Falls back to plain Bukkit permission checks when LuckPerms isn't installed.

## Maintenance Mode

Toggle maintenance on/off, set a custom reason, and optionally broadcast the change. Joining players without `astracontrol.bypass.maintenance` are kicked with a configurable message; players already online are never affected by a toggle. State can persist across restarts (`maintenance.persist` in `config.yml`).

## GUI Overview

Every menu (Main Control Center, Server Health, Plugins, Hooks, Online Players, Errors, Maintenance, Placeholder Tester, Permission Debugger, Broadcast Center) is built from `gui.yml` - material, display name, lore, slot, custom-model-data, glow, and amount are all configurable, no layout is hardcoded in Java. All clicks in AstraControl menus are cancelled unconditionally, so there is no item-duplication surface.

## Reload Safety

`/astractrl reload` (and the in-GUI reload button) re-reads `config.yml`, `gui.yml`, `hooks.yml`, and the active language file, re-detects every hook, and resizes the error buffer - without re-registering listeners, tasks, commands, or menus. Reloading is safe to run repeatedly.

## Folia Notes

AstraControl detects Folia at startup and switches to a Folia-safe scheduler (region/global/async schedulers) instead of the classic Bukkit scheduler. Player teleports use Paper's `teleportAsync`. Server-wide aggregate reads (entity/chunk counts) are defensively wrapped so a Folia threading restriction degrades to "unavailable" instead of crashing.

## Documentation

- [COMMANDS.md](COMMANDS.md)
- [PERMISSIONS.md](PERMISSIONS.md)
- [CONFIGURATION.md](CONFIGURATION.md)
- [INTEGRATIONS.md](INTEGRATIONS.md)
- [LOCALIZATION.md](LOCALIZATION.md)
- [QA_CHECKLIST.md](QA_CHECKLIST.md)
- [CHANGELOG.md](CHANGELOG.md)

## License / Support

AstraControl is a free plugin by APonder. Issues and feature requests are welcome wherever this plugin is distributed.
