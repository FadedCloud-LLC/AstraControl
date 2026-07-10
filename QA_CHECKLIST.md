# QA Checklist

Manual verification checklist before releasing a build. Test on at least Paper (primary target); spot-check Spigot and Folia where noted.

> Items below are checked off where verified either by running the build or by a full source-level audit (file:line evidence for every claim). Items still needing a live server + client are marked **(live)**. Known bugs found during the audit are called out inline — fix before shipping 1.0.0.
>
> This document is the code-level audit trail. If you're a human tester with an actual server and client rather than a code reviewer, use [LIVE_TESTING.md](LIVE_TESTING.md) instead — same coverage, written as plain click-through steps with no code references.

## Startup / Shutdown

- [x] Clean startup on Paper prints the 6-line summary banner and nothing else (with `debug: false`). — `StartupSummaryLogger` logs exactly 6 unconditional lines; all other startup logging is gated behind debug. **(live)** spot-check actual console output.
- [x] `debug: true` prints additional per-hook and diagnostic detail without errors. — `HookManager.detectAll()` wraps each hook in try/catch, falls back to `HookStatus.missing(...)` instead of throwing.
- [x] Fresh install generates `config.yml`, `gui.yml`, `hooks.yml`, `language/en.yml` with correct defaults. — all four use `saveResource` on first run; bundled resources verified non-trivial (75/417/93/46 lines).
- [x] Plugin disables cleanly (`/stop` or `/reload`) with no exceptions; all AstraControl tasks are cancelled. — `onEnable` catches bootstrap failures, `onDisable` null-checks; `cancelAllTasks()` iterates every tracked task. Minor caveat: if bootstrap throws mid-way after registering some listeners/tasks, those rely on Bukkit's own teardown rather than AstraControl code — acceptable, not exercised.
- [x] Running on pure Spigot: TPS/MSPT/ping report "Unavailable" instead of throwing. — `TpsMonitor`/`PlayerToolsManager` resolve reflectively once, catch `NoSuchMethodException`, expose availability flags consumers check before formatting. **Fixed 2026-07-08 (2nd pass):** `ServerHealthSnapshot.currentTps()` returned a `-1` sentinel when unavailable, but `HealthCommand`/`HealthMenu` formatted it unconditionally, showing `TPS: -1.00` instead of "Unavailable". Added `ServerHealthSnapshot.tpsAvailable()` and gated both display sites on it, matching the existing MSPT/entity-count pattern.
- [x] Running on Folia: plugin enables, scheduler uses region/global/async paths, no "called from wrong thread" errors during normal use. — `FoliaSchedulerAdapter` routes correctly per work type; zero direct `Bukkit.getScheduler()` call sites anywhere in the codebase.

## Reload Safety

- [x] `/astractrl reload` twice in a row does not duplicate hook detections, error-watcher attachment, or menu sessions. — `HookManager` clears registry before re-detecting; `ErrorWatcher` attach/detach guarded by a `volatile boolean`; listeners are registered once at bootstrap, never re-registered by `ReloadManager`.
- [x] Editing `config.yml` health thresholds and reloading applies the new thresholds immediately. — config accessors re-read the live (reload-replaced) `YamlConfiguration` each call, no cached fields.
- [x] Editing `language/en.yml` and reloading changes displayed messages. — `LanguageManager.reload()` invalidates the translation cache.
- [x] Editing `gui.yml` slot/material and reloading changes the next-opened menu's appearance. — `MenuRegistry` rebuilds every menu fresh from the live config on each open.

## Hooks

- [x] Every dedicated hook (PlaceholderAPI, LuckPerms, Vault, ProtocolLib, WorldGuard, Citizens, ItemsAdder, Oraxen, Nexo, HeadDatabase, PlayerPoints, ViaVersion, TAB, EssentialsX) still detects correctly after the `HookManager`/`GenericHook` refactor (2026-07-09). — `HookManager.knownHooks` builds the same 14 instances as before; `detectAndPublish` is the same enabled-check/try-catch/registry.put logic, just factored out of the loop body.
- [x] Adding an arbitrary new entry under `hooks.yml`'s `hooks:` section (an id with no dedicated `Hook` class) is detected generically via `GenericHook` — shows up in `/astractrl hooks` and the Hooks GUI with correct installed/enabled/version/notes. — `HooksConfig.additionalHookIds()` finds the extra id, `HookManager.detectAll()` publishes a `GenericHook` for it into the same `HookStatusRegistry`; both `HooksCommand` and `HookStatusMenu` iterate the registry generically (no hardcoded 14-item assumption).
- [x] A generic entry whose id collides with a dedicated hook's id does not get double-detected/double-registered — the dedicated class wins. — `additionalHookIds()` removes `knownHookIds` from the config's key set before any `GenericHook` is built, so a colliding id never reaches the generic path at all; `HookStatusRegistry.put()` is also keyed by id (overwrite-safe) as a second layer.
- [x] Disabling a generic entry (`enabled: false`) skips its check the same way a dedicated hook's disable does. — `detectAndPublish` checks `hooksConfig.isEnabled(hook.id())` before calling `.detect()` for both dedicated and generic hooks alike.
- [x] Adding a new generic entry and running `/astractrl reload` picks it up immediately, without a restart. — `configManager.reload()` (which reloads `hooksConfig` from disk) runs as an earlier `ReloadManager` step than `hookManager::detectAll`, and `additionalHookIds()` always reads the live `YamlConfiguration`, so no restart is needed.
- [x] A generic entry for a plugin that isn't installed reports "Not installed" instead of throwing. — `AbstractHook.detect()` returns `HookStatus.missing(...)` when `Bukkit.getPluginManager().getPlugin(pluginName())` is null; `detectAndPublish`'s try/catch is a second safety net even if that changed.

## Commands

- [x] Every command in [COMMANDS.md](COMMANDS.md) runs without error for a permitted sender. — all 25 documented commands map 1:1 to a `SubCommand` implementation; no orphans either direction.
- [x] Every command correctly denies a sender lacking its permission. — `CommandManager.dispatch` checks `sender.hasPermission(...)` centrally before every execute; nested subcommand permissions re-checked and match `PERMISSIONS.md`/`plugin.yml` exactly.
- [x] Tab completion returns sensible values for subcommands and player-name arguments. — **Fixed 2026-07-08:** `CommandManager.tabComplete` now filters both top-level and nested suggestions through `isPermitted(sender, command)`, matching `/help`'s filtering.
- [x] `/astractrl` with no args opens the GUI for a player and shows help to console. — **Fixed 2026-07-08:** `CommandManager.dispatch` now resolves `"gui"` for player senders and `"help"` for everyone else on zero args, then runs through the normal permission/cooldown/execute pipeline (including the full permission check — a player needs `astracontrol.gui` to open the GUI this way, matching `/astractrl gui` and `COMMANDS.md`). A same-day 2nd-pass "fix" that special-cased the zero-args path to skip that permission check was itself a security regression (any player with just `astracontrol.use` could open the admin GUI) and was reverted during the 2026-07-08 code-quality pass; `COMMANDS.md` row 1 was corrected to document `astracontrol.gui` instead.

Also fixed: subcommand `execute()` is now wrapped in try/catch in `CommandManager.dispatch` — an uncaught exception is logged via `PluginLogger.error(...)` (which flows into AstraControl's own error-watcher, since it's attached to the Log4j2 root logger) and the sender gets a friendly `command-error` message instead of a raw stack trace.

## GUI

- [x] All 10 top-level menus open without error: Main, Health, Plugins (list+detail), Hooks, Players (list+detail), Errors, Maintenance, Placeholder Tester, Permission Debugger, Broadcast. — all 12 menu sections present and non-null in shipped `gui.yml`. **Fixed 2026-07-08:** `GuiLayout.parse()` now falls back to an empty `MemoryConfiguration` when the section is `null`, so a trimmed/corrupted `gui.yml` degrades to default layout instead of NPE-ing.
- [x] Every click in every AstraControl menu is cancelled - dragging/shift-clicking cannot extract or duplicate items. — `MenuManager.onClick`/`onDrag` cancel unconditionally before any early-return branch.
- [x] Pagination (Plugins, Players, Hooks, Errors) correctly disables next/previous at the list boundaries. — shared `PaginatedMenu` boundary math verified correct, no off-by-one, hidden buttons are also inert no-ops.
- [x] Chat-input prompts (placeholder entry, permission entry, maintenance reason, broadcast messages) correctly capture the next chat line and never leak it to public chat. — `MenuManager.onChat` cancels at `EventPriority.LOWEST`, before other plugins' chat listeners can broadcast it.
- [x] Closing a menu mid-prompt and rejoining does not leave a stuck pending-input state. — **Fixed 2026-07-08:** `MenuManager.onClose` now clears and invokes `PendingChatInput.onCancel()` when a prompt is abandoned by closing the inventory; `GUIManager.open`/`openMenu` also clear any pending chat input when navigating to a new menu, so abandoning a prompt by opening a different menu no longer leaves it stuck either.

## Maintenance Mode

- [x] Enabling maintenance kicks a bypass-less joining player with the configured message. — `MaintenanceListener` runs at `EventPriority.LOWEST`, kicks via configured/language-driven message.
- [x] A player with `astracontrol.bypass.maintenance` can still join. — bypass check runs before the kick, node matches `plugin.yml` exactly.
- [x] Players already online are unaffected by toggling maintenance. — no code path kicks online players on toggle; confirmed via repo-wide grep for kick calls.
- [x] `maintenance.persist: true` survives a full server restart. — state written to `maintenance-state.yml` on every toggle, read back on startup when persist is enabled.
- **Fixed 2026-07-08:** removed the dead `maintenance.kick-message` config key (and its `ConfigManager` getter) from `config.yml`/`CONFIGURATION.md` — it was never referenced; actual kick text comes from `language/en.yml`.

## Error Watcher

- [x] A warning/error logged by another plugin appears in `/astractrl errors` and the Errors GUI. — `ErrorWatcher` attaches to the Log4j2 root logger, captures from any source.
- [x] Repeated identical warnings increment the count instead of creating duplicate entries. — dedup key is `source|level|message`; matching entries increment a counter instead of inserting.
- [x] `errors clear` empties the buffer; `errors export` writes a readable file under `plugins/AstraControl/logs/`. — both command and GUI share the same buffer instance; export writes plain-text, human-readable lines to the documented path.

## PlaceholderAPI / Permissions

- [x] With PlaceholderAPI installed, `/astractrl papi` resolves a real placeholder correctly in all 4 view modes. — each mode is a genuinely distinct transform of the resolved string. Caveat: if a PAPI expansion returns text already using `§` codes rather than `&`, the plain/MiniMessage/legacy views won't re-transform it (`ColorUtil.fromLegacy` only handles `&`). **(live)** spot-check against a `§`-emitting expansion if you have one installed.
- [x] Without PlaceholderAPI, the tester shows the "not installed" message instead of erroring. — presence guard checked before any PAPI class is touched, at every call site.
- [x] With LuckPerms installed, permission debug shows a plausible source and group list. — queries LuckPerms user/group data correctly, walks direct + inherited nodes.
- [x] Without LuckPerms, permission debug falls back to Bukkit's `hasPermission`/OP check. — runtime selection gated on config + plugin presence, with try/catch fallback.

## Broadcasts

- [x] Chat, title, actionbar, and bossbar broadcasts reach every online player. — all 4 types iterate `Bukkit.getOnlinePlayers()`, not just the sender.
- [x] Bossbars disappear automatically after the configured duration. — removal scheduled via the Folia-safe scheduler adapter, not raw Bukkit scheduler.
- [x] Broadcast messages correctly substitute PlaceholderAPI placeholders per-recipient when PAPI is installed. — placeholder resolution happens once per recipient inside the send loop, not globally.

## Build

- [x] `mvn clean package` succeeds with no errors. — verified, clean build, exit 0.
- [x] The shaded jar contains `dev/aponder/astracontrol/libs/bstats/...` and no unrelocated `org/bstats/...` classes. — verified via jar listing.
- [x] The shaded jar does NOT bundle Bukkit/Paper/PlaceholderAPI/LuckPerms/etc. classes. — verified via jar listing; only AstraControl's own hook/adapter classes reference those APIs, nothing bundled.

## Known Issues

All 6 issues found during the 2026-07-07 audit were fixed on 2026-07-08 and the build was re-verified (`mvn clean package`, exit 0):

1. ~~`/astractrl` with no args should open the GUI for players~~ — fixed, `CommandManager.dispatch`.
2. ~~Tab completion should filter suggestions by sender permission~~ — fixed, `CommandManager.tabComplete`.
3. ~~Pending chat-input prompts must be cleared on abandonment~~ — fixed, `MenuManager`/`GUIManager`.
4. ~~`GuiLayout.parse()` should null-check its section~~ — fixed.
5. ~~Subcommand execution should be wrapped in try/catch~~ — fixed, `CommandManager.dispatch`.
6. ~~Remove the dead `maintenance.kick-message` config key~~ — fixed.

A second full audit pass was run on 2026-07-08 (build + 4 parallel source-level re-verifications covering all sections above) to check for regressions and anything the first pass missed. All 6 fixes above held with no regressions. Two new issues surfaced and were fixed the same day:

7. ~~Pure-Spigot `/astractrl tps`/`health` (command and GUI) showed `TPS: -1.00` instead of "Unavailable"~~ — fixed, `ServerHealthSnapshot.tpsAvailable()` + `HealthCommand`/`HealthMenu`.
8. ~~Bare `/astractrl` required `astracontrol.gui` in addition to the documented `astracontrol.use`~~ — **this "fix" was itself a security regression** (see below) and was later reverted; the actual bug was in `COMMANDS.md`'s docs, not the code.

A third full audit pass (build + 4 parallel source-level re-verifications) was run again on 2026-07-08 to confirm stability. All 36 checklist items PASS, both fixes from issues #7/#8 confirmed genuinely intact (including a specific check that the root-permission fix introduced no permission bypass on explicit subcommands), and no new issues were found.

**Code-quality/cleanup pass (2026-07-08):** a separate whole-codebase audit (not diff-based — no git repo here; 4 parallel agents split by layer, then fixes applied and verified) found 7 more bugs and 21 cleanups:

9. ~~bStats plugin ID was a placeholder (`1`), reporting live telemetry to someone else's bstats.org project~~ — fixed, now the real registered ID.
10. ~~Issue #8's "fix" above was a permission-bypass regression: any player with just `astracontrol.use` could open the full admin GUI via bare `/astractrl`, bypassing `astracontrol.gui`~~ — reverted; `CommandManager.dispatch` always enforces `command.permission()` again, and `COMMANDS.md` row 1 now correctly documents `astracontrol.gui`.
11. ~~`/astractrl debug command <player> <command>` (run a command as another player) had no permission beyond the base `astracontrol.debug`~~ — fixed, new `astracontrol.debug.command` permission added and enforced.
12. ~~`BroadcastManager` mutated `Player` state (chat/title/actionbar/bossbar/sound) without Folia region dispatch on 5 of 6 call sites~~ — fixed, every per-player action now runs through `scheduler.runAtEntity(...)`.
13. ~~Plugin-dependency status in the GUI detail menu was invisible (no `lore:` key) and would've rendered squished onto one line if it had been~~ — fixed, lore is now built as one `Component` per dependency.
14. ~~`MenuManager` used the deprecated `AsyncPlayerChatEvent`, risking chat-input prompts hanging forever if another plugin intercepted the modern event first~~ — fixed, migrated to Paper's `AsyncChatEvent`.
15. ~~`/astractrl plugin <typo>` showed a "no online player found" message~~ — fixed, new `PLUGIN_NOT_FOUND` translation key.

A fourth full audit pass (build + 4 parallel source-level re-verifications, each specifically probing the cleanup-pass changes) confirmed all 36 checklist items still PASS with zero regressions from any of the above. See `astracontrol_release_status.md` (memory) for the full list of 21 cleanups (dead-code removal, shared helper extraction, etc.) alongside the 7 bugs.

No remaining code-level blockers for 1.0.0. Live-server spot-checks (items marked **(live)** above) are still recommended before shipping, but are not expected to surface new issues.
