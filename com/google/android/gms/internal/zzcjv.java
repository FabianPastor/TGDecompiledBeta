package com.google.android.gms.internal;

import android.os.Bundle;

final class zzcjv implements Runnable {
    private /* synthetic */ String val$name;
    private /* synthetic */ String zzimf;
    private /* synthetic */ String zzjgq;
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ long zzjhh;
    private /* synthetic */ Bundle zzjhi;
    private /* synthetic */ boolean zzjhj;
    private /* synthetic */ boolean zzjhk;
    private /* synthetic */ boolean zzjhl;

    zzcjv(zzcjn com_google_android_gms_internal_zzcjn, String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjgq = str;
        this.val$name = str2;
        this.zzjhh = j;
        this.zzjhi = bundle;
        this.zzjhj = z;
        this.zzjhk = z2;
        this.zzjhl = z3;
        this.zzimf = str3;
    }

    public final void run() {
        this.zzjhc.zzb(this.zzjgq, this.val$name, this.zzjhh, this.zzjhi, this.zzjhj, this.zzjhk, this.zzjhl, this.zzimf);
    }
}
