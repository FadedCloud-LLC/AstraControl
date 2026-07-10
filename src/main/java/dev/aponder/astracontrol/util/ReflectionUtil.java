package dev.aponder.astracontrol.util;

import java.lang.reflect.Method;

/**
 * Shared helper for the "look up a possibly-missing public no-arg method once, degrade
 * to unavailable instead of throwing" pattern used by every Paper-only reflective API
 * lookup in this codebase (TPS/MSPT, player ping/client-brand, etc).
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static Method findMethod(Class<?> type, String name) {
        try {
            return type.getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
