package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzaf extends zzaa {
    private long asg;
    private final Runnable ash = new Runnable(this) {
        final /* synthetic */ zzaf ask;

        {
            this.ask = r1;
        }

        @MainThread
        public void run() {
            this.ask.zzbvf().zzm(new Runnable(this) {
                final /* synthetic */ AnonymousClass1 asl;

                {
                    this.asl = r1;
                }

                public void run() {
                    this.asl.ask.zzbyi();
                }
            });
        }
    };
    private final zzf asi = new zzf(this, this.anq) {
        final /* synthetic */ zzaf ask;

        @WorkerThread
        public void run() {
            this.ask.zzbyj();
        }
    };
    private final zzf asj = new zzf(this, this.anq) {
        final /* synthetic */ zzaf ask;

        @WorkerThread
        public void run() {
            this.ask.zzbyk();
        }
    };
    private Handler mHandler;

    zzaf(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @WorkerThread
    private void zzbp(long j) {
        zzyl();
        zzbyg();
        this.asi.cancel();
        this.asj.cancel();
        zzbvg().zzbwj().zzj("Activity resumed, time", Long.valueOf(j));
        this.asg = j;
        if (zzaan().currentTimeMillis() - zzbvh().aqa.get() > zzbvh().aqc.get()) {
            zzbvh().aqb.set(true);
            zzbvh().aqd.set(0);
        }
        if (zzbvh().aqb.get()) {
            this.asi.zzx(Math.max(0, zzbvh().apZ.get() - zzbvh().aqd.get()));
        } else {
            this.asj.zzx(Math.max(0, 3600000 - zzbvh().aqd.get()));
        }
    }

    @WorkerThread
    private void zzbq(long j) {
        zzyl();
        zzbyg();
        this.asi.cancel();
        this.asj.cancel();
        zzbvg().zzbwj().zzj("Activity paused, time", Long.valueOf(j));
        if (this.asg != 0) {
            zzbvh().aqd.set(zzbvh().aqd.get() + (j - this.asg));
        }
        zzbvh().aqc.set(zzaan().currentTimeMillis());
        synchronized (this) {
            if (!zzbvh().aqb.get()) {
                this.mHandler.postDelayed(this.ash, 1000);
            }
        }
    }

    private void zzbyg() {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    @WorkerThread
    private void zzbyj() {
        zzyl();
        zzbvg().zzbwj().zzj("Session started, time", Long.valueOf(zzaan().elapsedRealtime()));
        zzbvh().aqb.set(false);
        zzbux().zzf("auto", "_s", new Bundle());
    }

    @WorkerThread
    private void zzbyk() {
        zzyl();
        long elapsedRealtime = zzaan().elapsedRealtime();
        if (this.asg == 0) {
            this.asg = elapsedRealtime - 3600000;
        }
        long j = zzbvh().aqd.get() + (elapsedRealtime - this.asg);
        zzbvh().aqd.set(j);
        zzbvg().zzbwj().zzj("Recording user engagement, ms", Long.valueOf(j));
        Bundle bundle = new Bundle();
        bundle.putLong("_et", j);
        zzbux().zzf("auto", "_e", bundle);
        zzbvh().aqd.set(0);
        this.asg = elapsedRealtime;
        this.asj.zzx(Math.max(0, 3600000 - zzbvh().aqd.get()));
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzaam() {
        super.zzaam();
    }

    public /* bridge */ /* synthetic */ zze zzaan() {
        return super.zzaan();
    }

    public /* bridge */ /* synthetic */ void zzbuv() {
        super.zzbuv();
    }

    public /* bridge */ /* synthetic */ zzc zzbuw() {
        return super.zzbuw();
    }

    public /* bridge */ /* synthetic */ zzac zzbux() {
        return super.zzbux();
    }

    public /* bridge */ /* synthetic */ zzn zzbuy() {
        return super.zzbuy();
    }

    public /* bridge */ /* synthetic */ zzg zzbuz() {
        return super.zzbuz();
    }

    public /* bridge */ /* synthetic */ zzad zzbva() {
        return super.zzbva();
    }

    public /* bridge */ /* synthetic */ zze zzbvb() {
        return super.zzbvb();
    }

    public /* bridge */ /* synthetic */ zzal zzbvc() {
        return super.zzbvc();
    }

    public /* bridge */ /* synthetic */ zzv zzbvd() {
        return super.zzbvd();
    }

    public /* bridge */ /* synthetic */ zzaf zzbve() {
        return super.zzbve();
    }

    public /* bridge */ /* synthetic */ zzw zzbvf() {
        return super.zzbvf();
    }

    public /* bridge */ /* synthetic */ zzp zzbvg() {
        return super.zzbvg();
    }

    public /* bridge */ /* synthetic */ zzt zzbvh() {
        return super.zzbvh();
    }

    public /* bridge */ /* synthetic */ zzd zzbvi() {
        return super.zzbvi();
    }

    @MainThread
    protected void zzbyf() {
        synchronized (this) {
            zzbyg();
            this.mHandler.removeCallbacks(this.ash);
        }
        final long elapsedRealtime = zzaan().elapsedRealtime();
        zzbvf().zzm(new Runnable(this) {
            final /* synthetic */ zzaf ask;

            public void run() {
                this.ask.zzbp(elapsedRealtime);
            }
        });
    }

    @MainThread
    protected void zzbyh() {
        final long elapsedRealtime = zzaan().elapsedRealtime();
        zzbvf().zzm(new Runnable(this) {
            final /* synthetic */ zzaf ask;

            public void run() {
                this.ask.zzbq(elapsedRealtime);
            }
        });
    }

    @WorkerThread
    public void zzbyi() {
        zzyl();
        zzbvg().zzbwi().log("Application backgrounded. Logging engagement");
        long j = zzbvh().aqd.get();
        if (j > 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("_et", j);
            zzbux().zzf("auto", "_e", bundle);
            zzbvh().aqd.set(0);
            return;
        }
        zzbvg().zzbwe().zzj("Not logging non-positive engagement time", Long.valueOf(j));
    }

    public /* bridge */ /* synthetic */ void zzyl() {
        super.zzyl();
    }

    protected void zzym() {
    }
}
