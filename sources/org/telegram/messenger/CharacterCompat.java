package org.telegram.messenger;

public class CharacterCompat {
    public static final char MIN_HIGH_SURROGATE = '?';
    public static final char MIN_LOW_SURROGATE = '?';
    public static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;

    public static char highSurrogate(int i) {
        return (char) ((i >>> 10) + 55232);
    }

    public static char lowSurrogate(int i) {
        return (char) ((i & 1023) + 56320);
    }
}