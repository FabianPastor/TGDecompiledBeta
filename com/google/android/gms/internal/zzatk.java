package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;

abstract class zzatk {
    private static volatile Handler zzafd;
    private volatile long zzafe;
    private final zzaue zzbqg;
    private boolean zzbrx = true;
    private final Runnable zzw = new Runnable(this) {
        final /* synthetic */ zzatk zzbry;

        {
            this.zzbry = r1;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.zzbry.zzbqg.zzKj().zzm(this);
                return;
            }
            boolean zzcy = this.zzbry.zzcy();
            this.zzbry.zzafe = 0;
            if (zzcy && this.zzbry.zzbrx) {
                this.zzbry.run();
            }
        }
    };

    zzatk(zzaue com_google_android_gms_internal_zzaue) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        this.zzbqg = com_google_android_gms_internal_zzaue;
    }

    private Handler getHandler() {
        if (zzafd != null) {
            return zzafd;
        }
        Handler handler;
        synchronized (zzatk.class) {
            if (zzafd == null) {
                zzafd = new Handler(this.zzbqg.getContext().getMainLooper());
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
            this.zzafe = this.zzbqg.zznR().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzw, j)) {
                this.zzbqg.zzKk().zzLX().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
