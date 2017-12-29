package com.google.android.gms.internal;

import java.util.concurrent.Callable;

final class zzcje implements Callable<byte[]> {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ zzcha zzjgs;

    zzcje(zzcir com_google_android_gms_internal_zzcir, zzcha com_google_android_gms_internal_zzcha, String str) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgs = com_google_android_gms_internal_zzcha;
        this.zzimf = str;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzjgo.zziwf.zzbal();
        return this.zzjgo.zziwf.zza(this.zzjgs, this.zzimf);
    }
}
