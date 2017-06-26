package com.google.android.gms.internal;

import java.util.concurrent.Callable;

final class zzchb implements Callable<byte[]> {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ zzcey zzbtj;

    zzchb(zzcgp com_google_android_gms_internal_zzcgp, zzcey com_google_android_gms_internal_zzcey, String str) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbtj = com_google_android_gms_internal_zzcey;
        this.zzbjh = str;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzbtf.zzboe.zzze();
        return this.zzbtf.zzboe.zza(this.zzbtj, this.zzbjh);
    }
}
