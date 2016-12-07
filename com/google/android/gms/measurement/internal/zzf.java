package com.google.android.gms.measurement.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;

abstract class zzf {
    private static volatile Handler bY;
    private final zzx anq;
    private boolean aol = true;
    private volatile long bZ;
    private final Runnable zzw = new Runnable(this) {
        final /* synthetic */ zzf aom;

        {
            this.aom = r1;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.aom.anq.zzbvf().zzm(this);
                return;
            }
            boolean zzfl = this.aom.zzfl();
            this.aom.bZ = 0;
            if (zzfl && this.aom.aol) {
                this.aom.run();
            }
        }
    };

    zzf(zzx com_google_android_gms_measurement_internal_zzx) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzx);
        this.anq = com_google_android_gms_measurement_internal_zzx;
    }

    private Handler getHandler() {
        if (bY != null) {
            return bY;
        }
        Handler handler;
        synchronized (zzf.class) {
            if (bY == null) {
                bY = new Handler(this.anq.getContext().getMainLooper());
            }
            handler = bY;
        }
        return handler;
    }

    public void cancel() {
        this.bZ = 0;
        getHandler().removeCallbacks(this.zzw);
    }

    public abstract void run();

    public boolean zzfl() {
        return this.bZ != 0;
    }

    public void zzx(long j) {
        cancel();
        if (j >= 0) {
            this.bZ = this.anq.zzaan().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzw, j)) {
                this.anq.zzbvg().zzbwc().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
