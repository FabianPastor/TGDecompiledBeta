package com.google.android.gms.internal;

final class zzced implements Runnable {
    private /* synthetic */ long zzbot;
    private /* synthetic */ zzceb zzbou;
    private /* synthetic */ String zztF;

    zzced(zzceb com_google_android_gms_internal_zzceb, String str, long j) {
        this.zzbou = com_google_android_gms_internal_zzceb;
        this.zztF = str;
        this.zzbot = j;
    }

    public final void run() {
        this.zzbou.zze(this.zztF, this.zzbot);
    }
}
