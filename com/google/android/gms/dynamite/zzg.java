package com.google.android.gms.dynamite;

import dalvik.system.PathClassLoader;

final class zzg extends PathClassLoader {
    zzg(String str, ClassLoader classLoader) {
        super(str, classLoader);
    }

    protected final Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
        if (!(str.startsWith("java.") || str.startsWith("android."))) {
            try {
                return findClass(str);
            } catch (ClassNotFoundException e) {
            }
        }
        return super.loadClass(str, z);
    }
}
