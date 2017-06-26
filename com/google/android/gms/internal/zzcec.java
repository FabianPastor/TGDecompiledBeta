package com.google.android.gms.internal;

final class zzcec implements Runnable {
    private /* synthetic */ long zzbot;
    private /* synthetic */ zzceb zzbou;
    private /* synthetic */ String zztF;

    zzcec(zzceb com_google_android_gms_internal_zzceb, String str, long j) {
        this.zzbou = com_google_android_gms_internal_zzceb;
        this.zztF = str;
        this.zzbot = j;
    }

    public final void run() {
        this.zzbou.zzd(this.zztF, this.zzbot);
    }
}
