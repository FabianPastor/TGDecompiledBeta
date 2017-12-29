package com.google.android.gms.internal;

import android.os.Handler;
import com.google.android.gms.common.internal.zzbq;

abstract class zzcgs {
    private static volatile Handler zzdvp;
    private volatile long zzdvq;
    private final zzcim zziwf;
    private boolean zzizd = true;
    private final Runnable zzz = new zzcgt(this);

    zzcgs(zzcim com_google_android_gms_internal_zzcim) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcim);
        this.zziwf = com_google_android_gms_internal_zzcim;
    }

    private final Handler getHandler() {
        if (zzdvp != null) {
            return zzdvp;
        }
        Handler handler;
        synchronized (zzcgs.class) {
            if (zzdvp == null) {
                zzdvp = new Handler(this.zziwf.getContext().getMainLooper());
            }
            handler = zzdvp;
        }
        return handler;
    }

    public final void cancel() {
        this.zzdvq = 0;
        getHandler().removeCallbacks(this.zzz);
    }

    public abstract void run();

    public final boolean zzdx() {
        return this.zzdvq != 0;
    }

    public final void zzs(long j) {
        cancel();
        if (j >= 0) {
            this.zzdvq = this.zziwf.zzws().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzz, j)) {
                this.zziwf.zzawy().zzazd().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
