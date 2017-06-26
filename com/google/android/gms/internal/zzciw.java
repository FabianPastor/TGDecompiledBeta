package com.google.android.gms.internal;

import android.app.job.JobParameters;

final class zzciw implements Runnable {
    private /* synthetic */ zzcgk zzbrM;
    final /* synthetic */ zzcfk zzbrP;
    final /* synthetic */ Integer zzbun;
    final /* synthetic */ JobParameters zzbuo;
    final /* synthetic */ zzciv zzbup;

    zzciw(zzciv com_google_android_gms_internal_zzciv, zzcgk com_google_android_gms_internal_zzcgk, Integer num, zzcfk com_google_android_gms_internal_zzcfk, JobParameters jobParameters) {
        this.zzbup = com_google_android_gms_internal_zzciv;
        this.zzbrM = com_google_android_gms_internal_zzcgk;
        this.zzbun = num;
        this.zzbrP = com_google_android_gms_internal_zzcfk;
        this.zzbuo = jobParameters;
    }

    public final void run() {
        this.zzbrM.zzze();
        this.zzbrM.zzl(new zzcix(this));
        this.zzbrM.zzza();
    }
}
