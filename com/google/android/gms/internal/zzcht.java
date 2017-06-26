package com.google.android.gms.internal;

final class zzcht implements Runnable {
    private /* synthetic */ String val$name;
    private /* synthetic */ Object zzbtD;
    private /* synthetic */ String zzbth;
    private /* synthetic */ zzchk zzbtt;
    private /* synthetic */ long zzbty;

    zzcht(zzchk com_google_android_gms_internal_zzchk, String str, String str2, Object obj, long j) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
        this.zzbth = str;
        this.val$name = str2;
        this.zzbtD = obj;
        this.zzbty = j;
    }

    public final void run() {
        this.zzbtt.zza(this.zzbth, this.val$name, this.zzbtD, this.zzbty);
    }
}
