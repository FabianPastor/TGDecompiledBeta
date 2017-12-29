package com.google.android.gms.internal;

final class zzclj implements Runnable {
    private /* synthetic */ long zziwu;
    private /* synthetic */ zzclf zzjjf;

    zzclj(zzclf com_google_android_gms_internal_zzclf, long j) {
        this.zzjjf = com_google_android_gms_internal_zzclf;
        this.zziwu = j;
    }

    public final void run() {
        this.zzjjf.zzbf(this.zziwu);
    }
}
