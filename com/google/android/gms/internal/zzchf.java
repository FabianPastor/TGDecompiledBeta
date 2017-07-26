package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzchf implements Callable<List<zzcjk>> {
    private /* synthetic */ zzceh zzbte;
    private /* synthetic */ zzcgq zzbtf;

    zzchf(zzcgq com_google_android_gms_internal_zzcgq, zzceh com_google_android_gms_internal_zzceh) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbte = com_google_android_gms_internal_zzceh;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzbtf.zzboe.zzze();
        return this.zzbtf.zzboe.zzwz().zzdP(this.zzbte.packageName);
    }
}
