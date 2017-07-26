package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public final class zzcja extends zzchj {
    private Handler mHandler;
    private long zzbur = super.zzkq().elapsedRealtime();
    private final zzcer zzbus = new zzcjb(this, this.zzboe);
    private final zzcer zzbut = new zzcjc(this, this.zzboe);

    zzcja(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
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
            zzchz.zza(super.zzwx().zzzh(), bundle);
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

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
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

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
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
