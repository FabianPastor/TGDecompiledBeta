package com.google.android.gms.internal;

import java.util.concurrent.Callable;

final class zzchc implements Callable<byte[]> {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcgq zzbtf;
    private /* synthetic */ zzcez zzbtj;

    zzchc(zzcgq com_google_android_gms_internal_zzcgq, zzcez com_google_android_gms_internal_zzcez, String str) {
        this.zzbtf = com_google_android_gms_internal_zzcgq;
        this.zzbtj = com_google_android_gms_internal_zzcez;
        this.zzbjh = str;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzbtf.zzboe.zzze();
        return this.zzbtf.zzboe.zza(this.zzbtj, this.zzbjh);
    }
}
