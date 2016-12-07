package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzag extends zzaa {
    protected long avM;
    private final zzf avN = new zzf(this, this.aqw) {
        final /* synthetic */ zzag avP;

        @WorkerThread
        public void run() {
            this.avP.zzbze();
        }
    };
    private final zzf avO = new zzf(this, this.aqw) {
        final /* synthetic */ zzag avP;

        @WorkerThread
        public void run() {
            this.avP.zzbzf();
        }
    };
    private Handler mHandler;

    zzag(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @WorkerThread
    private void zzbn(long j) {
        zzzx();
        zzbzc();
        this.avN.cancel();
        this.avO.cancel();
        zzbwb().zzbxe().zzj("Activity resumed, time", Long.valueOf(j));
        this.avM = j;
        if (zzabz().currentTimeMillis() - zzbwc().atj.get() > zzbwc().atl.get()) {
            zzbwc().atk.set(true);
            zzbwc().atm.set(0);
        }
        if (zzbwc().atk.get()) {
            this.avN.zzx(Math.max(0, zzbwc().ati.get() - zzbwc().atm.get()));
        } else {
            this.avO.zzx(Math.max(0, 3600000 - zzbwc().atm.get()));
        }
    }

    @WorkerThread
    private void zzbo(long j) {
        zzzx();
        zzbzc();
        this.avN.cancel();
        this.avO.cancel();
        zzbwb().zzbxe().zzj("Activity paused, time", Long.valueOf(j));
        if (this.avM != 0) {
            zzbwc().atm.set(zzbwc().atm.get() + (j - this.avM));
        }
        zzbwc().atl.set(zzabz().currentTimeMillis());
    }

    private void zzbzc() {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    @WorkerThread
    private void zzbzf() {
        zzzx();
        zzck(false);
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    @MainThread
    protected void zzbzb() {
        final long elapsedRealtime = zzabz().elapsedRealtime();
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzag avP;

            public void run() {
                this.avP.zzbn(elapsedRealtime);
            }
        });
    }

    @MainThread
    protected void zzbzd() {
        final long elapsedRealtime = zzabz().elapsedRealtime();
        zzbwa().zzm(new Runnable(this) {
            final /* synthetic */ zzag avP;

            public void run() {
                this.avP.zzbo(elapsedRealtime);
            }
        });
    }

    @WorkerThread
    protected void zzbze() {
        zzzx();
        zzbwb().zzbxe().zzj("Session started, time", Long.valueOf(zzabz().elapsedRealtime()));
        zzbwc().atk.set(false);
        zzbvq().zzf("auto", "_s", new Bundle());
    }

    @WorkerThread
    public boolean zzck(boolean z) {
        zzzx();
        zzacj();
        long elapsedRealtime = zzabz().elapsedRealtime();
        if (this.avM == 0) {
            this.avM = elapsedRealtime - 3600000;
        }
        long j = elapsedRealtime - this.avM;
        if (z || j >= 1000) {
            zzbwc().atm.set(j);
            zzbwb().zzbxe().zzj("Recording user engagement, ms", Long.valueOf(j));
            Bundle bundle = new Bundle();
            bundle.putLong("_et", j);
            zzad.zza(zzbvu().zzbyt(), bundle);
            zzbvq().zzf("auto", "_e", bundle);
            this.avM = elapsedRealtime;
            this.avO.cancel();
            this.avO.zzx(Math.max(0, 3600000 - zzbwc().atm.get()));
            return true;
        }
        zzbwb().zzbxe().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(j));
        return false;
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}
