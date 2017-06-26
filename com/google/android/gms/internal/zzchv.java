package com.google.android.gms.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

final class zzchv implements Callable<String> {
    private /* synthetic */ zzchk zzbtt;

    zzchv(zzchk com_google_android_gms_internal_zzchk) {
        this.zzbtt = com_google_android_gms_internal_zzchk;
    }

    public final /* synthetic */ Object call() throws Exception {
        Object zzyH = this.zzbtt.zzwG().zzyH();
        if (zzyH == null) {
            zzyH = this.zzbtt.zzwt().zzac(120000);
            if (zzyH == null) {
                throw new TimeoutException();
            }
            this.zzbtt.zzwG().zzee(zzyH);
        }
        return zzyH;
    }
}
