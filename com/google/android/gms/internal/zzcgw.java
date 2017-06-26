package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzcgw implements Callable<List<zzcjj>> {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ String zzbth;
    private /* synthetic */ String zzbti;

    zzcgw(zzcgp com_google_android_gms_internal_zzcgp, String str, String str2, String str3) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbjh = str;
        this.zzbth = str2;
        this.zzbti = str3;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzbtf.zzboe.zzze();
        return this.zzbtf.zzboe.zzwz().zzh(this.zzbjh, this.zzbth, this.zzbti);
    }
}
