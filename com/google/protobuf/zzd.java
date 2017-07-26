package com.google.protobuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class zzd {
    private static volatile boolean zzcrE = false;
    private static final Class<?> zzcrF = zzLt();
    static final zzd zzcrG = new zzd(true);
    private final Map<Object, Object> zzcrH;

    zzd() {
        this.zzcrH = new HashMap();
    }

    zzd(boolean z) {
        this.zzcrH = Collections.emptyMap();
    }

    private static Class<?> zzLt() {
        try {
            return Class.forName("com.google.protobuf.Extension");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
