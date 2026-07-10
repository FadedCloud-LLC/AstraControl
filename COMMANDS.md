# Commands

Root command: `/astractrl` (aliases: `/acontrol`, `/ac`). Base permission for any use: `astracontrol.use`.

## Main

| Command | Description | Permission |
|---|---|---|
| `/astractrl` | Opens the control center GUI. Same as `gui`. | `astracontrol.gui` |
| `/astractrl help` | Shows command help. | `astracontrol.use` |
| `/astractrl reload` | Reloads config, language, and hooks. | `astracontrol.reload` |
| `/astractrl status` | Shows overall plugin status. | `astracontrol.status` |
| `/astractrl gui` | Opens the control center GUI. | `astracontrol.gui` |

## Server Health

| Command | Description | Permission |
|---|---|---|
| `/astractrl health` | Full health report (TPS, MSPT, memory, uptime, worlds, chunks, entities, platform). | `astracontrol.health` |
| `/astractrl tps` | Current TPS. | `astracontrol.health` |
| `/astractrl memory` | Memory usage. | `astracontrol.health` |
| `/astractrl worlds` | Loaded world count. | `astracontrol.health` |
| `/astractrl chunks` | Loaded chunk count. | `astracontrol.health` |
| `/astractrl entities` | Entity/tile-entity counts. | `astracontrol.health` |

## Plugin Tools

| Command | Description | Permission |
|---|---|---|
| `/astractrl plugins` | Lists every installed plugin. | `astracontrol.plugins` |
| `/astractrl plugin <plugin>` | Detailed info about one plugin. | `astracontrol.plugins` |
| `/astractrl hooks` | Status of every optional integration. | `astracontrol.hooks` |
| `/astractrl dependencies <plugin>` | Dependency status for one plugin. | `astracontrol.plugins` |

## Error Tools

| Command | Description | Permission |
|---|---|---|
| `/astractrl errors` | Lists recent captured warnings/errors. | `astracontrol.errors` |
| `/astractrl errors clear` | Clears the error buffer. | `astracontrol.errors.clear` |
| `/astractrl errors export` | Exports the buffer to `plugins/AstraControl/logs/`. | `astracontrol.errors.export` |

## Maintenance

| Command | Description | Permission |
|---|---|---|
| `/astractrl maintenance on` | Enables maintenance mode. | `astracontrol.maintenance` |
| `/astractrl maintenance off` | Disables maintenance mode. | `astracontrol.maintenance` |
| `/astractrl maintenance status` | Shows current maintenance state. | `astracontrol.maintenance` |

## Broadcasts

| Command | Description | Permission |
|---|---|---|
| `/astractrl broadcast <message>` | Chat broadcast to every online player. | `astracontrol.broadcast` |
| `/astractrl title <title\|subtitle>` | Title broadcast (`\|` separates an optional subtitle). | `astracontrol.broadcast` |
| `/astractrl actionbar <message>` | Actionbar broadcast. | `astracontrol.broadcast` |

## PlaceholderAPI Tools

| Command | Description | Permission |
|---|---|---|
| `/astractrl papi <player> <placeholder>` | Tests a placeholder, shows every representation. | `astracontrol.papi` |
| `/astractrl papi raw <player> <placeholder>` | Shows only the raw output. | `astracontrol.papi` |
| `/astractrl papi render <player> <text>` | Renders a full block of text (multiple placeholders). | `astracontrol.papi` |
| `/astractrl papi compare <player> <placeholder>` | Shows input vs. resolved value side by side. | `astracontrol.papi` |

## Permission Debug

| Command | Description | Permission |
|---|---|---|
| `/astractrl debug player <player>` | Shows OP status and groups. | `astracontrol.debug` + `astracontrol.debug.player` |
| `/astractrl debug permission <player> <permission>` | Checks a specific permission, with source when known. | `astracontrol.debug` + `astracontrol.debug.permission` |
| `/astractrl debug command <player> <command>` | Runs a command as another player (for testing permissions). | `astracontrol.debug` + `astracontrol.debug.command` |

## Player Tools

| Command | Description | Permission |
|---|---|---|
| `/astractrl player <player>` | Brief info (world, gamemode). | `astracontrol.player` |
| `/astractrl playerinfo <player>` | Full snapshot (UUID, location, ping, health, food, OP, client brand). | `astracontrol.player` |
| `/astractrl stafftools <player>` | Opens the staff tools GUI for a player. | `astracontrol.stafftools` |

## Cooldowns

`/astractrl reload` (3s) and the broadcast/title/actionbar commands (2s) enforce a short per-player cooldown to prevent spam. Console/command blocks are never subject to cooldowns.
