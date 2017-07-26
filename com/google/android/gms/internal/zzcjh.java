package com.google.android.gms.internal;

final class zzcjh extends zzcer {
    private /* synthetic */ zzcjg zzbux;

    zzcjh(zzcjg com_google_android_gms_internal_zzcjg, zzcgl com_google_android_gms_internal_zzcgl) {
        this.zzbux = com_google_android_gms_internal_zzcjg;
        super(com_google_android_gms_internal_zzcgl);
    }

    public final void run() {
        this.zzbux.zzwF().zzyD().log("Sending upload intent from DelayedRunnable");
        this.zzbux.zzzr();
    }
}
