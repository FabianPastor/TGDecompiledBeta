package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzcho implements Runnable {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ String zzbth;
    private /* synthetic */ String zzbti;
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ AtomicReference zzbtv;

    zzcho(zzchk com_google_android_gms_internal_zzchk, AtomicReference atomicReference, String str, String str2, String str3) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbtv = atomicReference;
        this.zzbjh = str;
        this.zzbth = str2;
        this.zzbti = str3;
    }

    public final void run() {
        this.zzbtt.zzboe.zzww().zza(this.zzbtv, this.zzbjh, this.zzbth, this.zzbti);
    }
}