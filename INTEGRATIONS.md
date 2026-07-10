# Integrations

AstraControl works completely standalone. Every integration below is optional (soft-dependency) and is checked safely - a missing plugin never spams the console or crashes AstraControl, and AstraControl never unloads, reloads, or otherwise manages another plugin's lifecycle.

| Integration | What AstraControl actually does with it |
|---|---|
| **PlaceholderAPI** | Full functional integration: the Placeholder Tester (GUI + `/astractrl papi`) resolves real placeholders through PlaceholderAPI's API. Never performs HTTP/SQL, never caches expensive results unless `placeholderapi.cache-results` is enabled. |
| **LuckPerms** | Full functional integration: the Permission Debugger uses LuckPerms' API to check permissions with full inheritance, report the (best-effort) source of a grant, and list a player's groups. |
| **Vault** | Detection only - shown in the Hooks menu/`/astractrl hooks` for informational purposes. AstraControl does not use Vault's economy or permission APIs. |
| **ProtocolLib** | Detection only. AstraControl never sends or intercepts packets. |
| **WorldGuard** | Detection only. |
| **Citizens** | Detection only. |
| **ItemsAdder** | Detection only. |
| **Oraxen** | Detection only. |
| **Nexo** | Detection only. |
| **HeadDatabase** | Detection only. |
| **PlayerPoints** | Detection only. |
| **ViaVersion** | Detection only. |
| **TAB** | Detection only. |
| **EssentialsX** | Detection only. AstraControl is not an EssentialsX replacement and does not touch its data. |

"Detection only" means AstraControl checks whether the plugin is installed and enabled (and its version), and shows that in the Hooks menu/command - nothing more. This is intentionally honest: AstraControl does not claim deep integration it hasn't implemented.

### Watching any other plugin

The table above lists the integrations with dedicated detection code, but the Hooks system isn't limited to them. Add any plugin to `hooks.yml`'s `hooks:` section under whatever id you like (with `plugin-name` set to that plugin's exact name) and AstraControl will detect/display it the same "detection only" way, with zero code changes or updates required. See `hooks.yml`'s comments for an example. Reloading (`/astractrl reload`) picks up newly added entries immediately.

## Hook status fields

Each hook reports:

- **Installed** - is the plugin present on the server.
- **Enabled** - is the plugin currently enabled.
- **Version** - the installed plugin's version string.
- **Status** - Active / Installed (disabled) / Not installed.
- **Notes** - a short description, configurable per-hook in `hooks.yml`.

## Configuring hooks

See [CONFIGURATION.md](CONFIGURATION.md#hooksyml) - each hook can be disabled from checks entirely, renamed for display, or given an optional reload command that the Plugin Detail GUI menu can run on request (never automatic).
