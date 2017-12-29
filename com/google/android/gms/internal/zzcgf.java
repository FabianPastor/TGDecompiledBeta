package com.google.android.gms.internal;

final class zzcgf implements Runnable {
    private /* synthetic */ String zzbfa;
    private /* synthetic */ long zziwu;
    private /* synthetic */ zzcgd zziwv;

    zzcgf(zzcgd com_google_android_gms_internal_zzcgd, String str, long j) {
        this.zziwv = com_google_android_gms_internal_zzcgd;
        this.zzbfa = str;
        this.zziwu = j;
    }

    public final void run() {
        this.zziwv.zze(this.zzbfa, this.zziwu);
    }
}
