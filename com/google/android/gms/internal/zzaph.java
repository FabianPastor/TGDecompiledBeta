package com.google.android.gms.internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class zzaph {
    private static final Map<Class<?>, Class<?>> bmg;
    private static final Map<Class<?>, Class<?>> bmh;

    static {
        Map hashMap = new HashMap(16);
        Map hashMap2 = new HashMap(16);
        zza(hashMap, hashMap2, Boolean.TYPE, Boolean.class);
        zza(hashMap, hashMap2, Byte.TYPE, Byte.class);
        zza(hashMap, hashMap2, Character.TYPE, Character.class);
        zza(hashMap, hashMap2, Double.TYPE, Double.class);
        zza(hashMap, hashMap2, Float.TYPE, Float.class);
        zza(hashMap, hashMap2, Integer.TYPE, Integer.class);
        zza(hashMap, hashMap2, Long.TYPE, Long.class);
        zza(hashMap, hashMap2, Short.TYPE, Short.class);
        zza(hashMap, hashMap2, Void.TYPE, Void.class);
        bmg = Collections.unmodifiableMap(hashMap);
        bmh = Collections.unmodifiableMap(hashMap2);
    }

    private static void zza(Map<Class<?>, Class<?>> map, Map<Class<?>, Class<?>> map2, Class<?> cls, Class<?> cls2) {
        map.put(cls, cls2);
        map2.put(cls2, cls);
    }

    public static boolean zzk(Type type) {
        return bmg.containsKey(type);
    }

    public static <T> Class<T> zzp(Class<T> cls) {
        Class<T> cls2 = (Class) bmg.get(zzaoz.zzy(cls));
        return cls2 == null ? cls : cls2;
    }
}
