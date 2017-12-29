package com.google.android.gms.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

final class zzcjy implements Callable<String> {
    private /* synthetic */ zzcjn zzjhc;

    zzcjy(zzcjn com_google_android_gms_internal_zzcjn) {
        this.zzjhc = com_google_android_gms_internal_zzcjn;
    }

    public final /* synthetic */ Object call() throws Exception {
        Object zzazn = this.zzjhc.zzawz().zzazn();
        if (zzazn == null) {
            zzcjk zzawm = this.zzjhc.zzawm();
            if (zzawm.zzawx().zzazs()) {
                zzawm.zzawy().zzazd().log("Cannot retrieve app instance id from analytics worker thread");
                zzazn = null;
            } else {
                zzawm.zzawx();
                if (zzcih.zzau()) {
                    zzawm.zzawy().zzazd().log("Cannot retrieve app instance id from main thread");
                    zzazn = null;
                } else {
                    long elapsedRealtime = zzawm.zzws().elapsedRealtime();
                    zzazn = zzawm.zzbd(120000);
                    elapsedRealtime = zzawm.zzws().elapsedRealtime() - elapsedRealtime;
                    if (zzazn == null && elapsedRealtime < 120000) {
                        zzazn = zzawm.zzbd(120000 - elapsedRealtime);
                    }
                }
            }
            if (zzazn == null) {
                throw new TimeoutException();
            }
            this.zzjhc.zzawz().zzjp(zzazn);
        }
        return zzazn;
    }
}
