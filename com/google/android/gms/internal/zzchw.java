package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzchw implements Runnable {
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ AtomicReference zzbtv;

    zzchw(zzchk com_google_android_gms_internal_zzchk, AtomicReference atomicReference) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbtv = atomicReference;
    }

    public final void run() {
        this.zzbtt.zzww().zza(this.zzbtv);
    }
}
