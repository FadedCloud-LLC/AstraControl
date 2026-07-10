# Changelog

## 1.0.0-beta.1 - Public Beta

First public release, open for community beta testing ahead of a stable 1.0.0. See
[LIVE_TESTING.md](LIVE_TESTING.md) if you'd like to help test, and
[QA_CHECKLIST.md](QA_CHECKLIST.md) for the full pre-release audit trail.

**Features:**

- Server Health Monitor: TPS, MSPT, memory, uptime, worlds, chunks, entities, platform/Folia detection.
- Plugin Inspector: list, detail view, dependency analysis, configurable reload command.
- Hook Status detection for 14 built-in integrations, plus generic detection for any
  other installed plugin you add to `hooks.yml` - no code changes or updates needed.
- Error Watcher with deduplication, in-game notifications, and file export.
- Maintenance Mode with persistence, bypass permission, and broadcast-on-toggle.
- Broadcast Center: chat, title, actionbar, and bossbar broadcasts.
- Placeholder Tester with raw/plain/MiniMessage/legacy views.
- Permission Debugger with LuckPerms integration and Bukkit fallback.
- Online Player Tools: teleport, message, inventory view, staff command, permission/placeholder shortcuts.
- Fully config-driven GUI system (`gui.yml`) and localization system (`language/en.yml`).
- Folia-safe scheduler abstraction.
- Optional bStats metrics support.

**Fixed during pre-release QA** (multiple audit passes - see QA_CHECKLIST.md):

- Bare `/astractrl` now correctly opens the GUI for players (and requires `astracontrol.gui`, matching `/astractrl gui`).
- Tab completion now filters suggestions by the sender's actual permissions.
- Chat-input GUI prompts (player/permission/placeholder entry, etc.) are now cleared on abandonment instead of getting stuck.
- A malformed/trimmed `gui.yml` section degrades to a default layout instead of crashing menu construction.
- An uncaught exception in a command no longer leaks a raw stack trace to the sender.
- Pure-Spigot TPS now correctly displays "Unavailable" instead of a bogus negative number.
- `/astractrl debug command` (run a command as another player) now has its own dedicated permission instead of only requiring the base debug permission.
- Broadcasts (chat/title/actionbar/bossbar/sound) are now dispatched Folia-region-safely instead of risking a wrong-thread crash.
- The plugin-dependency status in the Plugin Detail GUI menu is now actually visible.
- Migrated off a deprecated chat-capture API that could, in rare cases, cause a GUI prompt to hang indefinitely.
- `/astractrl plugin <typo>` now shows a proper "no plugin found" message instead of a confusing "no player found" one.
- Removed a dead, unused `maintenance.kick-message` config key.
- bStats now reports under AstraControl's actual registered project ID.
