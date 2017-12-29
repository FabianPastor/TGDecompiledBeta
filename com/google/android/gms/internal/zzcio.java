package com.google.android.gms.internal;

import java.util.concurrent.Callable;

final class zzcio implements Callable<String> {
    private /* synthetic */ String zzimf;
    private /* synthetic */ zzcim zzjgh;

    zzcio(zzcim com_google_android_gms_internal_zzcim, String str) {
        this.zzjgh = com_google_android_gms_internal_zzcim;
        this.zzimf = str;
    }

    public final /* synthetic */ Object call() throws Exception {
        zzcgh zzjb = this.zzjgh.zzaws().zzjb(this.zzimf);
        if (zzjb != null) {
            return zzjb.getAppInstanceId();
        }
        this.zzjgh.zzawy().zzazf().log("App info was null when attempting to get app instance id");
        return null;
    }
}
