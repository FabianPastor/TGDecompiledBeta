package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzcjx implements Runnable {
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ AtomicReference zzjhe;
    private /* synthetic */ boolean zzjhf;

    zzcjx(zzcjn com_google_android_gms_internal_zzcjn, AtomicReference atomicReference, boolean z) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjhe = atomicReference;
        this.zzjhf = z;
    }

    public final void run() {
        this.zzjhc.zzawp().zza(this.zzjhe, this.zzjhf);
    }
}
