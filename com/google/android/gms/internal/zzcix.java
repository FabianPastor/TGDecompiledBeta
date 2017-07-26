package com.google.android.gms.internal;

import android.app.job.JobParameters;

final class zzcix implements Runnable {
    private /* synthetic */ zzcgl zzbrM;
    final /* synthetic */ zzcfl zzbrP;
    final /* synthetic */ Integer zzbun;
    final /* synthetic */ JobParameters zzbuo;
    final /* synthetic */ zzciw zzbup;

    zzcix(zzciw com_google_android_gms_internal_zzciw, zzcgl com_google_android_gms_internal_zzcgl, Integer num, zzcfl com_google_android_gms_internal_zzcfl, JobParameters jobParameters) {
        this.zzbup = com_google_android_gms_internal_zzciw;
        this.zzbrM = com_google_android_gms_internal_zzcgl;
        this.zzbun = num;
        this.zzbrP = com_google_android_gms_internal_zzcfl;
        this.zzbuo = jobParameters;
    }

    public final void run() {
        this.zzbrM.zzze();
        this.zzbrM.zzl(new zzciy(this));
        this.zzbrM.zzza();
    }
}
