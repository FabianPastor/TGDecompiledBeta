package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzchq implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ String zzbth;
    private /* synthetic */ String zzbti;
    private /* synthetic */ zzchl zzbtt;
    private /* synthetic */ AtomicReference zzbtv;
    private /* synthetic */ boolean zzbtw;

    zzchq(zzchl com_google_android_gms_internal_zzchl, AtomicReference atomicReference, String str, String str2, String str3, boolean z) {
        this.zzbtt = com_google_android_gms_internal_zzchl;
        this.zzbtv = atomicReference;
        this.zzbjh = str;
        this.zzbth = str2;
        this.zzbti = str3;
        this.zzbtw = z;
    }

    public final void run() {
        this.zzbtt.zzboe.zzww().zza(this.zzbtv, this.zzbjh, this.zzbth, this.zzbti, this.zzbtw);
    }
}
