package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzaun extends zzauh {
    private Handler mHandler;
    protected long zzbwa;
    private final zzatk zzbwb = new zzatk(this, this.zzbqg) {
        final /* synthetic */ zzaun zzbwd;

        @WorkerThread
        public void run() {
            this.zzbwd.zzNe();
        }
    };
    private final zzatk zzbwc = new zzatk(this, this.zzbqg) {
        final /* synthetic */ zzaun zzbwd;

        @WorkerThread
        public void run() {
            this.zzbwd.zzNf();
        }
    };

    zzaun(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private void zzNc() {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    @WorkerThread
    private void zzNf() {
        zzmR();
        zzaO(false);
        zzJX().zzW(zznR().elapsedRealtime());
    }

    @WorkerThread
    private void zzas(long j) {
        zzmR();
        zzNc();
        this.zzbwb.cancel();
        this.zzbwc.cancel();
        zzKk().zzMd().zzj("Activity resumed, time", Long.valueOf(j));
        this.zzbwa = j;
        if (zznR().currentTimeMillis() - zzKl().zzbtq.get() > zzKl().zzbts.get()) {
            zzKl().zzbtr.set(true);
            zzKl().zzbtt.set(0);
        }
        if (zzKl().zzbtr.get()) {
            this.zzbwb.zzy(Math.max(0, zzKl().zzbtp.get() - zzKl().zzbtt.get()));
        } else {
            this.zzbwc.zzy(Math.max(0, 3600000 - zzKl().zzbtt.get()));
        }
    }

    @WorkerThread
    private void zzat(long j) {
        zzmR();
        zzNc();
        this.zzbwb.cancel();
        this.zzbwc.cancel();
        zzKk().zzMd().zzj("Activity paused, time", Long.valueOf(j));
        if (this.zzbwa != 0) {
            zzKl().zzbtt.set(zzKl().zzbtt.get() + (j - this.zzbwa));
        }
        zzKl().zzbts.set(zznR().currentTimeMillis());
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJU() {
        super.zzJU();
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ zzatb zzJX() {
        return super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatf zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzauj zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzatu zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatl zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzaul zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzauk zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzatv zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatj zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzaut zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzauc zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzaun zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaud zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzatx zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzaua zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzati zzKm() {
        return super.zzKm();
    }

    @MainThread
    protected void zzNb() {
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzaun zzbwd;

            public void run() {
                this.zzbwd.zzas(elapsedRealtime);
            }
        });
    }

    @MainThread
    protected void zzNd() {
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzaun zzbwd;

            public void run() {
                this.zzbwd.zzat(elapsedRealtime);
            }
        });
    }

    @WorkerThread
    protected void zzNe() {
        zzmR();
        zzKk().zzMd().zzj("Session started, time", Long.valueOf(zznR().elapsedRealtime()));
        zzKl().zzbtr.set(false);
        zzJZ().zze("auto", "_s", new Bundle());
    }

    @WorkerThread
    public boolean zzaO(boolean z) {
        zzmR();
        zzob();
        long elapsedRealtime = zznR().elapsedRealtime();
        if (this.zzbwa == 0) {
            this.zzbwa = elapsedRealtime - 3600000;
        }
        long j = elapsedRealtime - this.zzbwa;
        if (z || j >= 1000) {
            zzKl().zzbtt.set(j);
            zzKk().zzMd().zzj("Recording user engagement, ms", Long.valueOf(j));
            Bundle bundle = new Bundle();
            bundle.putLong("_et", j);
            zzauk.zza(zzKd().zzMT(), bundle);
            zzJZ().zze("auto", "_e", bundle);
            this.zzbwa = elapsedRealtime;
            this.zzbwc.cancel();
            this.zzbwc.zzy(Math.max(0, 3600000 - zzKl().zzbtt.get()));
            return true;
        }
        zzKk().zzMd().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(j));
        return false;
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }
}
