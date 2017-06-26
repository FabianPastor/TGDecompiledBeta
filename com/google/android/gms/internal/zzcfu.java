package com.google.android.gms.internal;

final class zzcfu implements Runnable {
    private /* synthetic */ boolean zzbrg;
    private /* synthetic */ zzcft zzbrh;

    zzcfu(zzcft com_google_android_gms_internal_zzcft, boolean z) {
        this.zzbrh = com_google_android_gms_internal_zzcft;
        this.zzbrg = z;
    }

    public final void run() {
        this.zzbrh.zzboe.zzam(this.zzbrg);
    }
}
