package com.google.android.gms.internal;

public final class zzaoz {
    public static void zzbs(boolean z) {
        if (!z) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> T zzy(T t) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException();
    }
}
