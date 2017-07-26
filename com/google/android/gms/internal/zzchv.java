package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzchv implements Runnable {
    private /* synthetic */ zzchl zzbtt;
    private /* synthetic */ AtomicReference zzbtv;
    private /* synthetic */ boolean zzbtw;

    zzchv(zzchl com_google_android_gms_internal_zzchl, AtomicReference atomicReference, boolean z) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.zzbtv = atomicReference;
        this.zzbtw = z;
    }

    public final void run() {
        this.zzbtt.zzww().zza(this.zzbtv, this.zzbtw);
    }
}
