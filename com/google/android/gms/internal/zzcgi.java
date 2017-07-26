package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.lang.Thread.UncaughtExceptionHandler;

final class zzcgi implements UncaughtExceptionHandler {
    private final String zzbsg;
    private /* synthetic */ zzcgg zzbsh;

    public zzcgi(zzcgg com_google_android_gms_internal_zzcgg, String str) {
        this.zzbsh = com_google_android_gms_internal_zzcgg;
        zzbo.zzu(str);
        this.zzbsg = str;
    }

    public final synchronized void uncaughtException(Thread thread, Throwable th) {
        this.zzbsh.zzwF().zzyx().zzj(this.zzbsg, th);
    }
}
