package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public final class zzciz extends zzchi {
    private Handler mHandler;
    private long zzbur = super.zzkq().elapsedRealtime();
    private final zzceq zzbus = new zzcja(this, this.zzboe);
    private final zzceq zzbut = new zzcjb(this, this.zzboe);

    zzciz(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    private final void zzae(long j) {
        super.zzjC();
        zzzn();
        this.zzbus.cancel();
        this.zzbut.cancel();
        super.zzwF().zzyD().zzj("Activity resumed, time", Long.valueOf(j));
        this.zzbur = j;
        if (super.zzkq().currentTimeMillis() - super.zzwG().zzbry.get() > super.zzwG().zzbrA.get()) {
            super.zzwG().zzbrz.set(true);
            super.zzwG().zzbrB.set(0);
        }
        if (super.zzwG().zzbrz.get()) {
            this.zzbus.zzs(Math.max(0, super.zzwG().zzbrx.get() - super.zzwG().zzbrB.get()));
        } else {
            this.zzbut.zzs(Math.max(0, 3600000 - super.zzwG().zzbrB.get()));
        }
    }

    @WorkerThread
    private final void zzaf(long j) {
        super.zzjC();
        zzzn();
        this.zzbus.cancel();
        this.zzbut.cancel();
        super.zzwF().zzyD().zzj("Activity paused, time", Long.valueOf(j));
        if (this.zzbur != 0) {
            super.zzwG().zzbrB.set(super.zzwG().zzbrB.get() + (j - this.zzbur));
        }
    }

    private final void zzzn() {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    @WorkerThread
    private final void zzzp() {
        super.zzjC();
        zzap(false);
        super.zzwr().zzJ(super.zzkq().elapsedRealtime());
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public final boolean zzap(boolean z) {
        super.zzjC();
        zzkD();
        long elapsedRealtime = super.zzkq().elapsedRealtime();
        super.zzwG().zzbrA.set(super.zzkq().currentTimeMillis());
        long j = elapsedRealtime - this.zzbur;
        if (z || j >= 1000) {
            super.zzwG().zzbrB.set(j);
            super.zzwF().zzyD().zzj("Recording user engagement, ms", Long.valueOf(j));
            Bundle bundle = new Bundle();
            bundle.putLong("_et", j);
            zzchy.zza(super.zzwx().zzzh(), bundle);
            super.zzwt().zzd("auto", "_e", bundle);
            this.zzbur = elapsedRealtime;
            this.zzbut.cancel();
            this.zzbut.zzs(Math.max(0, 3600000 - super.zzwG().zzbrB.get()));
            return true;
        }
        super.zzwF().zzyD().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(j));
        return false;
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }

    @WorkerThread
    protected final void zzzo() {
        super.zzjC();
        super.zzwF().zzyD().zzj("Session started, time", Long.valueOf(super.zzkq().elapsedRealtime()));
        super.zzwG().zzbrz.set(false);
        super.zzwt().zzd("auto", "_s", new Bundle());
        super.zzwG().zzbrA.set(super.zzkq().currentTimeMillis());
    }
}