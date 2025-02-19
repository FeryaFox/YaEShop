package ru.feryafox.utils;

import java.util.UUID;

public final class UUIDUtils {

    private UUIDUtils() {}

    public static boolean isUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
