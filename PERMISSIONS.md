# Permissions

All permissions default to `op` unless noted otherwise.

| Permission | Description |
|---|---|
| `astracontrol.*` | Grants every AstraControl permission. |
| `astracontrol.admin.*` | Grants every admin permission (everything below `astracontrol.admin`). |
| `astracontrol.admin` | Marks a player as an AstraControl administrator. |
| `astracontrol.use` | Base permission required for any `/astractrl` command. |
| `astracontrol.reload` | Reload config/language/hooks. |
| `astracontrol.gui` | Open the control center GUI. |
| `astracontrol.status` | View plugin status. |
| `astracontrol.health` | View server health diagnostics. |
| `astracontrol.plugins` | View installed plugin information. |
| `astracontrol.hooks` | View integration hook status. |
| `astracontrol.errors` | View captured errors/warnings. |
| `astracontrol.errors.clear` | Clear the error buffer. |
| `astracontrol.errors.export` | Export the error buffer to file. |
| `astracontrol.maintenance` | Manage maintenance mode. |
| `astracontrol.broadcast` | Send broadcasts, titles, and actionbars. |
| `astracontrol.papi` | Use the PlaceholderAPI testing tools. |
| `astracontrol.debug` | Access debugging tools (base). |
| `astracontrol.debug.player` | Debug a player's overall state. |
| `astracontrol.debug.permission` | Debug a specific permission for a player. |
| `astracontrol.debug.command` | Execute arbitrary commands as another player. |
| `astracontrol.player` | View player information. |
| `astracontrol.stafftools` | Use staff tools on players. |
| `astracontrol.bypass.maintenance` | Join the server while maintenance mode is enabled. |
| `astracontrol.notify.errors` | Receive real-time error watcher notifications. |
| `astracontrol.notify.health` | Receive real-time health warning notifications. |

## Wildcards

- `astracontrol.*` implies `astracontrol.admin` and `astracontrol.admin.*`.
- `astracontrol.admin.*` implies every individual permission above `astracontrol.admin` itself.

## Notes

- Every command and every GUI button independently re-checks its permission at execution/click time - hiding a button is a UX nicety, not a security boundary, and AstraControl never relies on it alone.
- `astracontrol.bypass.maintenance`, `astracontrol.notify.errors`, and `astracontrol.notify.health` are not tied to any command - they only affect passive behavior (join gating, chat notifications).
