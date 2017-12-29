package com.google.android.gms.internal;

final class zzcjw implements Runnable {
    private /* synthetic */ String val$name;
    private /* synthetic */ String zzjgq;
    private /* synthetic */ zzcjn zzjhc;
    private /* synthetic */ long zzjhh;
    private /* synthetic */ Object zzjhm;

    zzcjw(zzcjn com_google_android_gms_internal_zzcjn, String str, String str2, Object obj, long j) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
        this.zzjgq = str;
        this.val$name = str2;
        this.zzjhm = obj;
        this.zzjhh = j;
    }

    public final void run() {
        this.zzjhc.zza(this.zzjgq, this.val$name, this.zzjhm, this.zzjhh);
    }
}
