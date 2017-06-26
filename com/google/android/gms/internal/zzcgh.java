package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbo;
import java.lang.Thread.UncaughtExceptionHandler;

final class zzcgh implements UncaughtExceptionHandler {
    private final String zzbsg;
    private /* synthetic */ zzcgf zzbsh;

    public zzcgh(zzcgf com_google_android_gms_internal_zzcgf, String str) {
        this.zzbsh = com_google_android_gms_internal_zzcgf;
        zzbo.zzu(str);
        this.zzbsg = str;
    }

    public final synchronized void uncaughtException(Thread thread, Throwable th) {
        this.zzbsh.zzwF().zzyx().zzj(this.zzbsg, th);
    }
}
