package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzcjz implements Runnable {
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ AtomicReference zzjhe;

    zzcjz(zzcjn com_google_android_gms_internal_zzcjn, AtomicReference atomicReference) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjhe = atomicReference;
    }

    public final void run() {
        this.zzjhc.zzawp().zza(this.zzjhe);
    }
}
