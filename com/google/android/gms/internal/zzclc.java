package com.google.android.gms.internal;

import android.app.job.JobParameters;

final /* synthetic */ class zzclc implements Runnable {
    private final zzcla zzjiv;
    private final zzchm zzjiz;
    private final JobParameters zzjja;

    zzclc(zzcla com_google_android_gms_internal_zzcla, zzchm com_google_android_gms_internal_zzchm, JobParameters jobParameters) {
        this.zzjiv = com_google_android_gms_internal_zzcla;
        this.zzjiz = com_google_android_gms_internal_zzchm;
        this.zzjja = jobParameters;
    }

    public final void run() {
        this.zzjiv.zza(this.zzjiz, this.zzjja);
    }
}
