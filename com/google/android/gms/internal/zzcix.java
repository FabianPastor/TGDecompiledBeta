package com.google.android.gms.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzcix implements Callable<List<zzclp>> {
    private /* synthetic */ zzcgi zzjgn;
    private /* synthetic */ zzcir zzjgo;
    private /* synthetic */ String zzjgq;
    private /* synthetic */ String zzjgr;

    zzcix(zzcir com_google_android_gms_internal_zzcir, zzcgi com_google_android_gms_internal_zzcgi, String str, String str2) {
        this.zzjgo = com_google_android_gms_internal_zzcir;
        this.zzjgn = com_google_android_gms_internal_zzcgi;
        this.zzjgq = str;
        this.zzjgr = str2;
    }

    public final /* synthetic */ Object call() throws Exception {
        this.zzjgo.zziwf.zzbal();
        return this.zzjgo.zziwf.zzaws().zzg(this.zzjgn.packageName, this.zzjgq, this.zzjgr);
    }
}
