package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

final class zzcik<V> extends FutureTask<V> implements Comparable<zzcik> {
    private final String zzjep;
    private /* synthetic */ zzcih zzjeq;
    private final long zzjer = zzcih.zzjeo.getAndIncrement();
    final boolean zzjes;

    zzcik(zzcih com_google_android_gms_internal_zzcih, Runnable runnable, boolean z, String str) {
        this.zzjeq = com_google_android_gms_internal_zzcih;
        super(runnable, null);
        zzbq.checkNotNull(str);
        this.zzjep = str;
        this.zzjes = false;
        if (this.zzjer == Long.MAX_VALUE) {
            com_google_android_gms_internal_zzcih.zzawy().zzazd().log("Tasks index overflow");
        }
    }

    zzcik(zzcih com_google_android_gms_internal_zzcih, Callable<V> callable, boolean z, String str) {
        this.zzjeq = com_google_android_gms_internal_zzcih;
        super(callable);
        zzbq.checkNotNull(str);
        this.zzjep = str;
        this.zzjes = z;
        if (this.zzjer == Long.MAX_VALUE) {
            com_google_android_gms_internal_zzcih.zzawy().zzazd().log("Tasks index overflow");
        }
    }

    public final /* synthetic */ int compareTo(Object obj) {
        zzcik com_google_android_gms_internal_zzcik = (zzcik) obj;
        if (this.zzjes != com_google_android_gms_internal_zzcik.zzjes) {
            return this.zzjes ? -1 : 1;
        } else {
            if (this.zzjer < com_google_android_gms_internal_zzcik.zzjer) {
                return -1;
            }
            if (this.zzjer > com_google_android_gms_internal_zzcik.zzjer) {
                return 1;
            }
            this.zzjeq.zzawy().zzaze().zzj("Two tasks share the same index. index", Long.valueOf(this.zzjer));
            return 0;
        }
    }

    protected final void setException(Throwable th) {
        this.zzjeq.zzawy().zzazd().zzj(this.zzjep, th);
        if (th instanceof zzcii) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
        }
        super.setException(th);
    }
}
