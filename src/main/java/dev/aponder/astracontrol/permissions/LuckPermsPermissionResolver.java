package dev.aponder.astracontrol.permissions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Resolves permissions and groups via the LuckPerms API. Every lookup is defensive -
 * if LuckPerms hasn't fully loaded the user yet (should not normally happen for an
 * online player), this falls back to reporting only what is safely available.
 */
public final class LuckPermsPermissionResolver {

    public PermissionDebugResult resolvePermission(Player player, String permission) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) {
            return new PermissionDebugResult(player.getName(), permission, player.hasPermission(permission),
                    null, List.of(), player.isOp(), "LuckPerms (user not loaded, used Bukkit fallback)");
        }

        boolean has = user.getCachedData().getPermissionData().checkPermission(permission) == Tristate.TRUE;
        List<String> groups = groupsOf(user);
        String source = resolveSource(user, permission, groups);

        return new PermissionDebugResult(player.getName(), permission, has, source, groups, player.isOp(), "LuckPerms");
    }

    public PermissionDebugResult resolvePlayer(Player player) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());
        List<String> groups = user == null ? List.of() : groupsOf(user);
        return new PermissionDebugResult(player.getName(), null, false, null, groups, player.isOp(), "LuckPerms");
    }

    private List<String> groupsOf(User user) {
        return user.getNodes(NodeType.INHERITANCE).stream()
                .map(InheritanceNode::getGroupName)
                .distinct()
                .toList();
    }

    private String resolveSource(User user, String permission, List<String> groups) {
        Optional<PermissionNode> direct = user.getNodes(NodeType.PERMISSION).stream()
                .filter(node -> node.getPermission().equalsIgnoreCase(permission))
                .findFirst();
        if (direct.isPresent()) {
            return "User (directly assigned)";
        }

        LuckPerms api = LuckPermsProvider.get();
        for (String groupName : groups) {
            Group group = api.getGroupManager().getGroup(groupName);
            if (group == null) {
                continue;
            }
            boolean matches = group.getNodes(NodeType.PERMISSION).stream()
                    .anyMatch(node -> node.getPermission().equalsIgnoreCase(permission));
            if (matches) {
                return "Group: " + groupName;
            }
        }

        return null;
    }
}
