package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzchu implements Runnable {
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ AtomicReference zzbtv;
    private /* synthetic */ boolean zzbtw;

    zzchu(zzchk com_google_android_gms_internal_zzchk, AtomicReference atomicReference, boolean z) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbtv = atomicReference;
        this.zzbtw = z;
    }

    public final void run() {
        this.zzbtt.zzww().zza(this.zzbtv, this.zzbtw);
    }
}
