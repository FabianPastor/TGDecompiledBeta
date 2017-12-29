package com.google.android.gms.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzcjr implements Runnable {
    private /* synthetic */ String zzimf;
    private /* synthetic */ String zzjgq;
    private /* synthetic */ String zzjgr;
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ AtomicReference zzjhe;

    zzcjr(zzcjn com_google_android_gms_internal_zzcjn, AtomicReference atomicReference, String str, String str2, String str3) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjhe = atomicReference;
        this.zzimf = str;
        this.zzjgq = str2;
        this.zzjgr = str3;
    }

    public final void run() {
        this.zzjhc.zziwf.zzawp().zza(this.zzjhe, this.zzimf, this.zzjgq, this.zzjgr);
    }
}
