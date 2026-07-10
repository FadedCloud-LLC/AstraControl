# Configuration

AstraControl ships four config files under `plugins/AstraControl/`:

- `config.yml` - general settings.
- `gui.yml` - every menu's layout (fully configurable, no hardcoded items).
- `hooks.yml` - which integrations to check, display names, and optional reload commands.
- `language/en.yml` - every user-facing message (MiniMessage format).

All four are merged with their bundled defaults on load, so upgrading AstraControl adds new keys without wiping your edits. Run `/astractrl reload` after any change.

## config.yml

```yaml
debug: false            # Verbose diagnostic logging.
language: en             # Which language/<code>.yml to use.
aliases: [acontrol, ac]  # Command aliases (also declared in plugin.yml).

maintenance:
  persist: true          # Survive restarts via maintenance-state.yml.
  enabled: false
  reason: "..."           # Shown to denied players; MiniMessage, supports newlines.
  bypass-permission: astracontrol.bypass.maintenance
  broadcast-on-toggle: true

health:
  update-interval-ticks: 100
  warnings:
    enabled: true
    notify-permission: astracontrol.notify.health
    tps-threshold: 18.0
    mspt-threshold: 50.0
    memory-usage-threshold-percent: 90.0

error-watcher:
  enabled: true
  buffer-size: 200
  capture-plugin-logs: true
  notify-in-game: true
  notify-permission: astracontrol.notify.errors

broadcast:
  chat-format: "..."
  title: { fade-in: 10, stay: 60, fade-out: 10 }
  actionbar: { duration-ticks: 60 }
  sound: { enabled: true, sound: "entity.experience_orb.pickup", volume: 1.0, pitch: 1.0 }
  bossbar: { enabled: true, duration-seconds: 10, color: YELLOW, overlay: PROGRESS }

placeholderapi:
  enabled: true
  cache-results: false

permission-debugger:
  prefer-luckperms: true
  show-inherited-permissions: true

player-tools:
  allow-inventory-view: true
  allow-teleport: true
  staff-command: ""       # {player} placeholder, blank disables.

metrics:
  enabled: true

update-checker:
  enabled: false
  notify-admins-on-join: false
```

## gui.yml

Each menu section supports:

- `title`, `size` - inventory title (MiniMessage) and slot count.
- `filler.material` / `filler.name` - background filler item.
- `back-slot`, `previous-page-slot`, `next-page-slot`, `content-slots-start`/`content-slots-end` - navigation slots (paginated menus).
- `items.<key>.material` / `name` / `lore` / `slot` / `custom-model-data` / `glow` / `amount` - fully configurable per button.

Lore/name support `{token}` placeholders that AstraControl fills in with live data (e.g. `{value}`, `{used}`, `{max}` on the Health menu) - see the comments in the shipped `gui.yml` for exactly which tokens each item supports.

## hooks.yml

```yaml
settings:
  log-missing-hooks: false

hooks:
  <hook-id>:
    enabled: true              # Set false to stop checking/showing this hook entirely.
    display-name: "..."
    plugin-name: "..."         # Exact plugin name AstraControl looks for.
    notes: "..."
    reload-command: "..."      # Optional: console command run by the "reload" button/action.
```

`<hook-id>` can be anything - the entries AstraControl ships with (`placeholderapi`, `luckperms`, `vault`, ...) have dedicated detection code, but you can add any other id here to watch any installed plugin generically (presence/enabled/version only). See [INTEGRATIONS.md](INTEGRATIONS.md#watching-any-other-plugin).

## language/en.yml

Every message key AstraControl sends is listed here with MiniMessage formatting. See [LOCALIZATION.md](LOCALIZATION.md) for the full key reference and how to add another language.
