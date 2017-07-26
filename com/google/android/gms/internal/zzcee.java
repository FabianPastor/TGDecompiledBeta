package com.google.android.gms.internal;

final class zzcee implements Runnable {
    private /* synthetic */ long zzbot;
    private /* synthetic */ zzcec zzbou;
    private /* synthetic */ String zztD;

    zzcee(zzcec com_google_android_gms_internal_zzcec, String str, long j) {
        this.zzbou = com_google_android_gms_internal_zzcec;
        this.zztD = str;
        this.zzbot = j;
    }

    public final void run() {
        this.zzbou.zze(this.zztD, this.zzbot);
    }
}
