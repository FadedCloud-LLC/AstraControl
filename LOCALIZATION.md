# Localization

AstraControl stores every user-facing message in `plugins/AstraControl/language/en.yml`, using [MiniMessage](https://docs.advntr.dev/minimessage/format.html) formatting. There is no `messages.yml` - everything text-related lives under `language/`.

## Adding another language

1. Copy `language/en.yml` to `language/<code>.yml` (e.g. `language/de.yml`), inside your server, and translate the values (keep the `{placeholder}` tokens unchanged).
2. Set `language: <code>` in `config.yml`.
3. Run `/astractrl reload`.

If a key is missing from your custom language file, AstraControl logs a warning (only visible with `debug: true`) and falls back to the built-in English default for that key - it never crashes or shows a blank message.

## Placeholders

Every message may contain `{token}` placeholders (curly braces, not PlaceholderAPI's `%percent%` syntax). These are always inserted as literal text, never re-parsed as MiniMessage - so a player name containing `<red>` can never break formatting or inject tags.

## Key reference

| Key | Placeholders | Used by |
|---|---|---|
| `prefix` | - | Prepended to most sent messages. |
| `no-permission` | - | Any permission check failure. |
| `player-only` | - | Player-only commands run from console. |
| `invalid-usage` | `{usage}` | Wrong argument count/format. |
| `unknown-command` | - | Unknown subcommand. |
| `player-not-found` | `{player}` | Target player not online. |
| `reload-started` / `reload-success` / `reload-failed` | `{time}` / `{error}` | `/astractrl reload`. |
| `placeholder-missing` | - | A tested placeholder produced no output. |
| `placeholderapi-missing` | - | PlaceholderAPI not installed. |
| `maintenance-enabled` / `-disabled` / `-already-enabled` / `-already-disabled` / `-join-denied` / `-status` | `{reason}` / `{state}` | Maintenance mode. |
| `broadcast-sent` | `{count}` | Broadcast Center. |
| `errors-cleared` / `-exported` / `-empty` / `error-notification` | `{count}` / `{file}` / `{source}` / `{message}` | Error Watcher. |
| `health-warning-tps` / `-mspt` / `-memory` | `{tps}` / `{mspt}` / `{percent}` | Health warnings. |
| `gui-loading` / `gui-no-access-item` | - | GUI state messages. |
| `command-help-header` / `command-help-entry` | `{command}` / `{description}` | `/astractrl help`. |
| `debug-permission-result` / `debug-player-header` | `{player}` / `{result}` / `{permission}` | Permission debugger. |

## Notes for translators

- Keep MiniMessage tags (`<red>`, `<gold>`, etc.) balanced - an unclosed tag colors everything after it.
- `\n` inside a value renders as a line break in chat.
- Component-heavy GUI item names/lore live in `gui.yml`, not `language/en.yml` - translate those separately if needed.
