# Live Testing Checklist

This is the **human tester's** checklist — for someone with a real Paper/Spigot/Folia
server and a Minecraft client, not a code reviewer. No code knowledge needed: every
item says exactly what to do and what you should see. Check items off as you go.

For the code-level audit trail (what was verified by reading source instead of
clicking through it), see [QA_CHECKLIST.md](QA_CHECKLIST.md).

## Before you start

- [ ] A Paper server (primary target) on 1.18.2+, ideally 1.20.4 to match what
      AstraControl is built against.
- [ ] At least one OP account and one non-OP account (or a permissions plugin like
      LuckPerms so you can grant/revoke individual `astracontrol.*` nodes).
- [ ] Optional, for full coverage: PlaceholderAPI, LuckPerms, and any one or two of
      Vault/WorldGuard/ProtocolLib/etc. installed so the "installed" side of Hooks gets
      exercised too, not just the "not installed" side.
- [ ] If you have access to a Spigot (non-Paper) server and/or a Folia server, keep
      those handy for the platform spot-checks near the end.

## 1. Fresh install

- [ ] Stop the server, delete `plugins/AstraControl/` entirely if it exists, drop in
      the jar, start the server.
- [ ] Confirm `plugins/AstraControl/` now contains `config.yml`, `gui.yml`,
      `hooks.yml`, and `language/en.yml`.
- [ ] Open each of those 4 files and confirm they're not empty / not just comments —
      each should have real default values.

## 2. Startup console output

- [ ] With `debug: false` (the default) in `config.yml`, watch the console during
      startup. You should see a short, clean summary block — not a wall of text —
      ending with something like "Enabled successfully."
- [ ] Set `debug: true`, restart. You should now see extra diagnostic lines (per-hook
      detection detail, etc.) and still **no errors or stack traces**.
- [ ] Set `debug: false` again for the rest of testing unless a step below says
      otherwise.
- [ ] `/stop` the server (or `/reload confirm` if you use Bukkit's own reload).
      Console should show no exceptions during shutdown.

## 3. `/astractrl reload`

- [ ] As OP, run `/astractrl reload` twice in a row. Both times it should say it
      reloaded successfully, with no errors, and no duplicate/garbled output.
- [ ] Edit a health threshold in `config.yml` (e.g. lower the TPS warning threshold),
      reload, and confirm the new threshold is reflected (see the Health section
      below for how to check).
- [ ] Edit a message in `language/en.yml` (e.g. change the `no-permission` text to
      something obviously different), reload, then trigger that message (e.g. try a
      command you don't have permission for) and confirm the new text shows up.
- [ ] Edit a slot or material in `gui.yml` for a button you'll recognize, reload, then
      open that menu and confirm the change appears.
- [ ] Try `/astractrl reload` twice within a couple seconds — the second attempt
      should be blocked with a short cooldown message, not silently ignored or
      double-executed.

## 4. Hooks

- [ ] Run `/astractrl hooks`. You should see every optional integration you have
      installed marked as installed/active, and everything else marked "Not
      installed" — no errors either way.
- [ ] Open the Hooks menu from the GUI (Main Menu → Hooks) and confirm it matches the
      command output.
- [ ] **New: custom hooks.** Stop the server, open `hooks.yml`, and add a new entry
      under `hooks:` for some plugin you have installed that ISN'T already in the
      list (or make one up with a `plugin-name` that doesn't exist, to test the
      "not installed" path) — see the example comment near the top of `hooks.yml`.
      Restart (or just `/astractrl reload` if you added it while the server was
      running) and confirm your new entry shows up correctly in `/astractrl hooks`
      and the Hooks GUI.
- [ ] Set `enabled: false` on that custom entry and reload — it should disappear
      from both the command output and the GUI.

## 5. Commands — Main

- [ ] `/astractrl` (no arguments) as a player → opens the GUI directly.
- [ ] `/astractrl` (no arguments) from console → shows the help list instead (console
      can't open a GUI).
- [ ] `/astractrl help` → lists every command you have permission for; commands you
      don't have permission for should NOT appear in the list.
- [ ] `/astractrl status` → shows a plugin status summary, no errors.
- [ ] `/astractrl gui` → opens the same GUI as bare `/astractrl`.
- [ ] Tab-complete `/astractrl ` (with trailing space) and confirm you only see
      subcommands you have permission for.

## 6. Commands — Server Health

- [ ] `/astractrl health` → full report: TPS, MSPT, memory, uptime, worlds, chunks,
      entities, platform. All fields should show real numbers (or "Unavailable" if
      you're on Spigot, not `-1` or blank).
- [ ] `/astractrl tps`, `/astractrl memory`, `/astractrl worlds`, `/astractrl chunks`,
      `/astractrl entities` → each shows just that one piece of info, matching what
      the full report showed.
- [ ] Open the Health menu in the GUI and confirm the numbers roughly match the
      command output (small differences are fine since time passes between checks).

## 7. Commands — Plugin Tools

- [ ] `/astractrl plugins` → lists every plugin on your server, enabled ones shown
      differently from disabled ones.
- [ ] `/astractrl plugin <name>` for a real installed plugin → shows detailed info
      (version, authors, main class, etc.).
- [ ] `/astractrl plugin doesnotexist123` → a clear "no plugin found" message, not a
      crash or a confusing "no player found" message.
- [ ] `/astractrl dependencies <name>` for a plugin that has dependencies → shows
      required/soft dependencies with a clear OK/disabled/missing status for each.
- [ ] Open the Plugins menu in the GUI, click into a plugin's detail view, and check
      the dependencies section is actually visible (readable lore/text), not blank.
- [ ] If that plugin has a reload command configured in `hooks.yml`, click the
      "Run Reload Command" button and confirm it runs (check console).

## 8. Commands — Error Tools

- [ ] Trigger a warning from another plugin (or just wait for one to naturally occur),
      then run `/astractrl errors` — it should show up.
- [ ] Trigger the exact same warning again — the count next to that entry should go
      up instead of a second entry appearing.
- [ ] Open the Errors menu in the GUI and confirm it matches.
- [ ] `/astractrl errors export` → check `plugins/AstraControl/logs/` for a new file;
      open it and confirm it's plain, readable text (not gibberish/binary).
- [ ] `/astractrl errors clear` → the list should now be empty, both via command and
      in the GUI.

## 9. Commands — Maintenance

- [ ] `/astractrl maintenance status` → shows current state (should start disabled).
- [ ] `/astractrl maintenance on` → confirms enabled.
- [ ] Have a second, non-bypassed account try to join → should be kicked with the
      configured maintenance message.
- [ ] Grant `astracontrol.bypass.maintenance` to that account and have it try again →
      should be able to join normally.
- [ ] While maintenance is on, confirm players who were already online **before** you
      turned it on are NOT kicked or affected.
- [ ] With `maintenance.persist: true` (the default) and maintenance still on,
      restart the server → maintenance should still be on after restart.
- [ ] `/astractrl maintenance off` → confirms disabled, and a normal player can join.

## 10. Commands — Broadcasts

- [ ] `/astractrl broadcast Hello everyone` → every online player sees it in chat.
- [ ] `/astractrl title Big Title|small subtitle` → every online player sees the
      title/subtitle on screen.
- [ ] `/astractrl actionbar Quick message` → every online player sees it above their
      hotbar.
- [ ] If you have PlaceholderAPI installed, broadcast something containing a
      placeholder (e.g. `%player_name%`) and confirm **each recipient** sees their
      own name, not the sender's.
- [ ] Open the Broadcast menu in the GUI and send one from there too — same checks.
- [ ] Try `/astractrl broadcast` twice back-to-back — the second should be blocked by
      the short cooldown.
- [ ] If bossbars are enabled in `config.yml`, trigger one from the GUI and confirm
      it appears for everyone and disappears on its own after the configured
      duration — nobody should be stuck with a permanent bossbar.

## 11. Commands — PlaceholderAPI Tools

*(Needs PlaceholderAPI installed for the "resolves correctly" checks below.)*

- [ ] `/astractrl papi <yourname> %player_name%` → shows the resolved value in all
      4 display formats (raw / plain / MiniMessage / legacy), each one visibly
      different in formatting.
- [ ] `/astractrl papi raw <yourname> %player_name%` → shows just the raw value.
- [ ] `/astractrl papi compare <yourname> %player_name%` → shows input next to
      output side by side.
- [ ] `/astractrl papi render <yourname> Hello %player_name%, you have %vault_eco_balance% coins`
      (or any placeholders you have) → renders the whole block correctly.
- [ ] Without PlaceholderAPI installed (test on a server that doesn't have it, or
      temporarily disable it), run any `papi` command → should show a clean
      "PlaceholderAPI is not installed" message, not an error.
- [ ] Open the Placeholder Tester in the GUI, select a player via the chat prompt,
      type a placeholder, and confirm the same 4 views render there too.

## 12. Commands — Permission Debug

*(Some checks need LuckPerms installed; do the "without LuckPerms" ones on a server
that doesn't have it, or temporarily disable it.)*

- [ ] `/astractrl debug player <name>` → shows OP status and group list.
- [ ] `/astractrl debug permission <name> some.permission.node` → shows whether that
      player has it, and (with LuckPerms) a plausible source.
- [ ] `/astractrl debug command <name> say hi` → runs `say hi` as that player; confirm
      it actually executes as them, not as you.
- [ ] Without LuckPerms installed, repeat the permission-debug checks — should fall
      back cleanly to a plain OP/`hasPermission` check instead of erroring.
- [ ] Open the Permission Debugger in the GUI: select a player via chat prompt, then
      test both "enter a permission" and "view groups" — confirm both produce
      sensible output.

## 13. Commands — Player Tools

- [ ] `/astractrl player <name>` → brief info (world, gamemode).
- [ ] `/astractrl playerinfo <name>` → full snapshot: UUID, location, ping, health,
      food, OP status, client brand (client brand may say "Unavailable" depending on
      platform — that's fine, it just shouldn't be blank/garbled).
- [ ] `/astractrl stafftools <name>` → opens a GUI with actions for that player.
- [ ] Open the Players menu in the GUI, page through the list if you have multiple
      players online, and click into a player's detail view.

## 14. GUI — general behavior

- [ ] From the Main Menu, open every single sub-menu at least once: Health, Plugins,
      Hooks, Players, Errors, Maintenance, Placeholder Tester, Permission Debugger,
      Broadcast. None should error or show a blank/broken layout.
- [ ] In any menu, try shift-clicking and drag-clicking items — nothing should move,
      duplicate, or leave the menu. Your own inventory should be completely
      unaffected.
- [ ] In a paginated menu (Plugins, Players, Hooks, or Errors, whichever has enough
      entries), page forward to the last page and confirm the "next" button
      disappears/stops working there, then page back to the first and confirm
      "previous" does the same.
- [ ] Start a chat-input prompt (e.g. "select a player" in the Permission Debugger),
      then close your inventory instead of typing anything. Reopen the menu and
      confirm you're not stuck in a broken "waiting for chat input" state.
- [ ] Start a chat-input prompt, then instead of answering it, type something in
      normal chat — confirm your test message did NOT get sent to public chat (only
      you should see it, if anything).

## 15. Permissions matrix

Using a non-OP test account and a permissions plugin (or by editing permissions
directly), verify the following denials:

- [ ] Account with **no** `astracontrol.*` permissions: every `/astractrl` command
      should say "no permission," including bare `/astractrl`.
- [ ] Account with only `astracontrol.use`: bare `/astractrl` should still deny
      opening the GUI (that specifically needs `astracontrol.gui`) — `/astractrl
      help` should still work.
- [ ] Account with `astracontrol.debug` but NOT `astracontrol.debug.command`: `debug
      player` and `debug permission` should work if separately granted, but `debug
      command <player> <cmd>` should be denied.
- [ ] Account with `astracontrol.errors` but not `astracontrol.errors.clear`: `errors`
      should work, `errors clear` should be denied.
- [ ] Tab-completing `/astractrl ` for a limited-permission account should only show
      the subcommands they can actually use.

## 16. Platform spot-checks

*(Skip any you don't have access to — just note it in your test report.)*

- [ ] **Spigot (non-Paper):** `/astractrl health`/`tps` should show "Unavailable" for
      TPS/MSPT instead of a fake number or an error. Player ping/client-brand should
      likewise show "Unavailable" instead of throwing.
- [ ] **Folia:** plugin should enable without "called from wrong thread" errors in the
      console during normal use — join, run commands, open GUIs, send broadcasts,
      toggle maintenance.
- [ ] **Purpur/other Paper forks:** general smoke test — enable, run a few commands,
      open the GUI. No special behavior expected, just confirm nothing's broken.

## 17. Wrap-up

- [ ] `/stop` the server one more time after all the above and confirm a clean
      shutdown with no exceptions in the console.
- [ ] Note anything that didn't match what this checklist described, with exact
      steps to reproduce, and report it back.
