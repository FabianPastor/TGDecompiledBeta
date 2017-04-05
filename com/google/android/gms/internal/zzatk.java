package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;

abstract class zzatk {
    private static volatile Handler zzafd;
    private volatile long zzafe;
    private final zzaue zzbqc;
    private boolean zzbrt = true;
    private final Runnable zzw = new Runnable(this) {
        final /* synthetic */ zzatk zzbru;

        {
            this.zzbru = r1;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.zzbru.zzbqc.zzKk().zzm(this);
                return;
            }
            boolean zzcy = this.zzbru.zzcy();
            this.zzbru.zzafe = 0;
            if (zzcy && this.zzbru.zzbrt) {
                this.zzbru.run();
            }
        }
    };

    zzatk(zzaue com_google_android_gms_internal_zzaue) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        this.zzbqc = com_google_android_gms_internal_zzaue;
    }

    private Handler getHandler() {
        if (zzafd != null) {
            return zzafd;
        }
        Handler handler;
        synchronized (zzatk.class) {
            if (zzafd == null) {
                zzafd = new Handler(this.zzbqc.getContext().getMainLooper());
            }
            handler = zzafd;
        }
        return handler;
    }

    public void cancel() {
        this.zzafe = 0;
        getHandler().removeCallbacks(this.zzw);
    }

    public abstract void run();

    public boolean zzcy() {
        return this.zzafe != 0;
    }

    public void zzy(long j) {
        cancel();
        if (j >= 0) {
            this.zzafe = this.zzbqc.zznR().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzw, j)) {
                this.zzbqc.zzKl().zzLY().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
