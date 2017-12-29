package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzciy implements Callable<List<zzclp>> {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ String zzjgq;
    private /* synthetic */ String zzjgr;

    zzciy(zzcir com_google_android_gms_internal_zzcir, String str, String str2, String str3) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzimf = str;
        this.zzjgq = str2;
        this.zzjgr = str3;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzjgo.zziwf.zzbal();
        return this.zzjgo.zziwf.zzaws().zzg(this.zzimf, this.zzjgq, this.zzjgr);
    }
}
