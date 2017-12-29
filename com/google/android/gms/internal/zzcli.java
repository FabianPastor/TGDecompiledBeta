package com.google.android.gms.internal;

final class zzcli implements Runnable {
    private /* synthetic */ long zziwu;
    private /* synthetic */ zzclf zzjjf;

    zzcli(zzclf com_google_android_gms_internal_zzclf, long j) {
        this.zzjjf = com_google_android_gms_internal_zzclf;
        this.zziwu = j;
    }

    public final void run() {
        this.zzjjf.zzbe(this.zziwu);
    }
}
