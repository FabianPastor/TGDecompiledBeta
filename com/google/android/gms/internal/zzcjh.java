package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzcjh implements Callable<List<zzclp>> {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcir zzjgo;

    zzcjh(zzcir com_google_android_gms_internal_zzcir, zzcgi com_google_android_gms_internal_zzcgi) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzjgo.zziwf.zzbal();
        return this.zzjgo.zziwf.zzaws().zzja(this.zzjgn.packageName);
    }
}
