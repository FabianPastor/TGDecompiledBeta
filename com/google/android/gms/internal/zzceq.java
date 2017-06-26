package com.google.android.gms.internal;

import android.os.Handler;
import com.google.android.gms.common.internal.zzbo;

abstract class zzceq {
    private static volatile Handler zzagY;
    private volatile long zzagZ;
    private final zzcgk zzboe;
    private boolean zzbpA = true;
    private final Runnable zzv = new zzcer(this);

    zzceq(zzcgk com_google_android_gms_internal_zzcgk) {
        zzbo.zzu(com_google_android_gms_internal_zzcgk);
        this.zzboe = com_google_android_gms_internal_zzcgk;
    }

    private final Handler getHandler() {
        if (zzagY != null) {
            return zzagY;
        }
        Handler handler;
        synchronized (zzceq.class) {
            if (zzagY == null) {
                zzagY = new Handler(this.zzboe.getContext().getMainLooper());
            }
            handler = zzagY;
        }
        return handler;
    }

    public final void cancel() {
        this.zzagZ = 0;
        getHandler().removeCallbacks(this.zzv);
    }

    public abstract void run();

    public final boolean zzbo() {
        return this.zzagZ != 0;
    }

    public final void zzs(long j) {
        cancel();
        if (j >= 0) {
            this.zzagZ = this.zzboe.zzkq().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzv, j)) {
                this.zzboe.zzwF().zzyx().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
