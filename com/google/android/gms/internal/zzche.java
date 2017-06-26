package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzche implements Callable<List<zzcjj>> {
    private /* synthetic */ zzceg zzbte;
    private /* synthetic */ zzcgp zzbtf;

    zzche(zzcgp com_google_android_gms_internal_zzcgp, zzceg com_google_android_gms_internal_zzceg) {
        this.zzbtf = com_google_android_gms_internal_zzcgp;
        this.zzbte = com_google_android_gms_internal_zzceg;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzbtf.zzboe.zzze();
        return this.zzbtf.zzboe.zzwz().zzdP(this.zzbte.packageName);
    }
}
