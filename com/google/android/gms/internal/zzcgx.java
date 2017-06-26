package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzcgx implements Callable<List<zzcej>> {
    private /* synthetic */ zzceg zzbte;
    private /* synthetic */ zzcgp zzbtf;
    private /* synthetic */ String zzbth;
    private /* synthetic */ String zzbti;

    zzcgx(zzcgp com_google_android_gms_internal_zzcgp, zzceg com_google_android_gms_internal_zzceg, String str, String str2) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbte = com_google_android_gms_internal_zzceg;
        this.zzbth = str;
        this.zzbti = str2;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzbtf.zzboe.zzze();
        return this.zzbtf.zzboe.zzwz().zzi(this.zzbte.packageName, this.zzbth, this.zzbti);
    }
}
