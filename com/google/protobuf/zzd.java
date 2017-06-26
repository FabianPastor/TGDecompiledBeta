package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class zzd {
    private static volatile boolean zzcrA = false;
    private static final Class<?> zzcrB = zzLq();
    static final zzd zzcrC = new zzd(true);
    private final Map<Object, Object> zzcrD;

    zzd() {
        this.zzcrD = new HashMap();
    }

    zzd(boolean z) {
        this.zzcrD = Collections.emptyMap();
    }

    private static Class<?> zzLq() {
        try {
            return Class.forName("com.google.protobuf.Extension");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
