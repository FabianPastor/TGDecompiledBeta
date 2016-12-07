package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;

abstract class zzasv {
    private static volatile Handler zzaec;
    private volatile long zzaed;
    private final zzatp zzbpw;
    private boolean zzbqB = true;
    private final Runnable zzv = new Runnable(this) {
        final /* synthetic */ zzasv zzbqC;

        {
            this.zzbqC = r1;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.zzbqC.zzbpw.zzJs().zzm(this);
                return;
            }
            boolean zzcv = this.zzbqC.zzcv();
            this.zzbqC.zzaed = 0;
            if (zzcv && this.zzbqC.zzbqB) {
                this.zzbqC.run();
            }
        }
    };

    zzasv(zzatp com_google_android_gms_internal_zzatp) {
        zzac.zzw(com_google_android_gms_internal_zzatp);
        this.zzbpw = com_google_android_gms_internal_zzatp;
    }

    private Handler getHandler() {
        if (zzaec != null) {
            return zzaec;
        }
        Handler handler;
        synchronized (zzasv.class) {
            if (zzaec == null) {
                zzaec = new Handler(this.zzbpw.getContext().getMainLooper());
            }
            handler = zzaec;
        }
        return handler;
    }

    public void cancel() {
        this.zzaed = 0;
        getHandler().removeCallbacks(this.zzv);
    }

    public abstract void run();

    public boolean zzcv() {
        return this.zzaed != 0;
    }

    public void zzx(long j) {
        cancel();
        if (j >= 0) {
            this.zzaed = this.zzbpw.zznq().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzv, j)) {
                this.zzbpw.zzJt().zzLa().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
