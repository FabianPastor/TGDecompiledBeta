package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

final class zzcgi<V> extends FutureTask<V> implements Comparable<zzcgi> {
    private final String zzbsg;
    private /* synthetic */ zzcgf zzbsh;
    private final long zzbsi = zzcgf.zzbsf.getAndIncrement();
    private final boolean zzbsj;

    zzcgi(zzcgf com_google_android_gms_internal_zzcgf, Runnable runnable, boolean z, String str) {
        this.zzbsh = com_google_android_gms_internal_zzcgf;
        super(runnable, null);
        zzbo.zzu(str);
        this.zzbsg = str;
        this.zzbsj = false;
        if (this.zzbsi == Long.MAX_VALUE) {
            com_google_android_gms_internal_zzcgf.zzwF().zzyx().log("Tasks index overflow");
        }
    }

    zzcgi(zzcgf com_google_android_gms_internal_zzcgf, Callable<V> callable, boolean z, String str) {
        this.zzbsh = com_google_android_gms_internal_zzcgf;
        super(callable);
        zzbo.zzu(str);
        this.zzbsg = str;
        this.zzbsj = z;
        if (this.zzbsi == Long.MAX_VALUE) {
            com_google_android_gms_internal_zzcgf.zzwF().zzyx().log("Tasks index overflow");
        }
    }

    public final /* synthetic */ int compareTo(@NonNull Object obj) {
        zzcgi com_google_android_gms_internal_zzcgi = (zzcgi) obj;
        if (this.zzbsj != com_google_android_gms_internal_zzcgi.zzbsj) {
            return this.zzbsj ? -1 : 1;
        } else {
            if (this.zzbsi < com_google_android_gms_internal_zzcgi.zzbsi) {
                return -1;
            }
            if (this.zzbsi > com_google_android_gms_internal_zzcgi.zzbsi) {
                return 1;
            }
            this.zzbsh.zzwF().zzyy().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbsi));
            return 0;
        }
    }

    protected final void setException(Throwable th) {
        this.zzbsh.zzwF().zzyx().zzj(this.zzbsg, th);
        if (th instanceof zzcgg) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
        }
        super.setException(th);
    }
}
