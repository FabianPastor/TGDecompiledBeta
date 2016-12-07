package com.google.android.gms.common.internal;

import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public abstract class DowngradeableSafeParcel extends AbstractSafeParcelable implements ReflectedParcelable {
    private static final Object Ce = new Object();
    private static ClassLoader Cf = null;
    private static Integer Cg = null;
    private boolean Ch = false;

    protected static ClassLoader zzaup() {
        synchronized (Ce) {
        }
        return null;
    }

    protected static Integer zzauq() {
        synchronized (Ce) {
        }
        return null;
    }

    private static boolean zzd(Class<?> cls) {
        boolean z = false;
        try {
            z = SafeParcelable.NULL.equals(cls.getField("NULL").get(null));
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e2) {
        }
        return z;
    }

    protected static boolean zzhs(String str) {
        ClassLoader zzaup = zzaup();
        if (zzaup == null) {
            return true;
        }
        try {
            return zzd(zzaup.loadClass(str));
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean zzaur() {
        return false;
    }
}
