package org.telegram.messenger.exoplayer.util;

public final class VerboseLogUtil {
    private static volatile boolean enableAllTags;
    private static volatile String[] enabledTags;

    private VerboseLogUtil() {
    }

    public static void setEnabledTags(String... tags) {
        enabledTags = tags;
        enableAllTags = false;
    }

    public static void setEnableAllTags(boolean enable) {
        enableAllTags = enable;
    }

    public static boolean isTagEnabled(String tag) {
        if (enableAllTags) {
            return true;
        }
        String[] tags = enabledTags;
        if (tags == null || tags.length == 0) {
            return false;
        }
        for (String equals : tags) {
            if (equals.equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean areAllTagsEnabled() {
        return enableAllTags;
    }
}
