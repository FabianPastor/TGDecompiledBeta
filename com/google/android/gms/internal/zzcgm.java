package com.google.android.gms.internal;

import java.util.concurrent.Callable;

final class zzcgm implements Callable<String> {
    private /* synthetic */ String zzbjh;
    private /* synthetic */ zzcgk zzbsY;

    zzcgm(zzcgk com_google_android_gms_internal_zzcgk, String str) {
        this.zzbsY = com_google_android_gms_internal_zzcgk;
        this.zzbjh = str;
    }

    public final /* synthetic */ Object call() throws Exception {
        zzcef zzdQ = this.zzbsY.zzwz().zzdQ(this.zzbjh);
        if (zzdQ != null) {
            return zzdQ.getAppInstanceId();
        }
        this.zzbsY.zzwF().zzyz().log("App info was null when attempting to get app instance id");
        return null;
    }
}
