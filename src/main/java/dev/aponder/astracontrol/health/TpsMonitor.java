package dev.aponder.astracontrol.health;

import dev.aponder.astracontrol.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Method;

/**
 * Reads TPS and MSPT via Paper's public {@code Server} API. Both methods are looked
 * up reflectively (once, at class-init) so this class degrades gracefully to "data
 * unavailable" on plain Spigot instead of throwing {@code NoSuchMethodError}.
 */
public final class TpsMonitor {

    private static final Method GET_TPS = ReflectionUtil.findMethod(Server.class, "getTPS");
    private static final Method GET_AVERAGE_TICK_TIME = ReflectionUtil.findMethod(Server.class, "getAverageTickTime");

    public double[] tps() {
        if (GET_TPS == null) {
            return new double[0];
        }
        try {
            return (double[]) GET_TPS.invoke(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            return new double[0];
        }
    }

    public double mspt() {
        if (GET_AVERAGE_TICK_TIME == null) {
            return -1;
        }
        try {
            return (double) GET_AVERAGE_TICK_TIME.invoke(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    public boolean isMsptAvailable() {
        return GET_AVERAGE_TICK_TIME != null;
    }
}
