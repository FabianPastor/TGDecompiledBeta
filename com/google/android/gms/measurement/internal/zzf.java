package com.google.android.gms.measurement.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzaa;

abstract class zzf {
    private static volatile Handler ef;
    private final zzx aqw;
    private boolean arv = true;
    private volatile long eg;
    private final Runnable zzw = new Runnable(this) {
        final /* synthetic */ zzf arw;

        {
            this.arw = r1;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.arw.aqw.zzbwa().zzm(this);
                return;
            }
            boolean zzfy = this.arw.zzfy();
            this.arw.eg = 0;
            if (zzfy && this.arw.arv) {
                this.arw.run();
            }
        }
    };

    zzf(zzx com_google_android_gms_measurement_internal_zzx) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzx);
        this.aqw = com_google_android_gms_measurement_internal_zzx;
    }

    private Handler getHandler() {
        if (ef != null) {
            return ef;
        }
        Handler handler;
        synchronized (zzf.class) {
            if (ef == null) {
                ef = new Handler(this.aqw.getContext().getMainLooper());
            }
            handler = ef;
        }
        return handler;
    }

    public void cancel() {
        this.eg = 0;
        getHandler().removeCallbacks(this.zzw);
    }

    public abstract void run();

    public boolean zzfy() {
        return this.eg != 0;
    }

    public void zzx(long j) {
        cancel();
        if (j >= 0) {
            this.eg = this.aqw.zzabz().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzw, j)) {
                this.aqw.zzbwb().zzbwy().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
