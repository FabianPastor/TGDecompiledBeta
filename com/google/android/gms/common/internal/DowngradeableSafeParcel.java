package com.google.android.gms.common.internal;

import com.google.android.gms.common.internal.safeparcel.zza;

public abstract class DowngradeableSafeParcel extends zza implements ReflectedParcelable {
    private static final Object zzaHq = new Object();
    private static ClassLoader zzaHr = null;
    private static Integer zzaHs = null;
    private boolean zzaHt = false;

    protected static boolean zzcA(String str) {
        zzrw();
        return true;
    }

    private static ClassLoader zzrw() {
        synchronized (zzaHq) {
        }
        return null;
    }

    protected static Integer zzrx() {
        synchronized (zzaHq) {
        }
        return null;
    }
}
