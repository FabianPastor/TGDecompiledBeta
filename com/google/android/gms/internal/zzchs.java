package com.google.android.gms.internal;

import android.os.Bundle;

final class zzchs implements Runnable {
    private /* synthetic */ String val$name;
    private /* synthetic */ String zzbjh;
    private /* synthetic */ boolean zzbtA;
    private /* synthetic */ boolean zzbtB;
    private /* synthetic */ boolean zzbtC;
    private /* synthetic */ String zzbth;
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ long zzbty;
    private /* synthetic */ Bundle zzbtz;

    zzchs(zzchk com_google_android_gms_internal_zzchk, String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbth = str;
        this.val$name = str2;
        this.zzbty = j;
        this.zzbtz = bundle;
        this.zzbtA = z;
        this.zzbtB = z2;
        this.zzbtC = z3;
        this.zzbjh = str3;
    }

    public final void run() {
        this.zzbtt.zzb(this.zzbth, this.val$name, this.zzbty, this.zzbtz, this.zzbtA, this.zzbtB, this.zzbtC, this.zzbjh);
    }
}
