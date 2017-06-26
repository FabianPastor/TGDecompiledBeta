package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.Map;

public final class zzceb extends zzchh {
    private final Map<String, Long> zzboq = new ArrayMap();
    private final Map<String, Integer> zzbor = new ArrayMap();
    private long zzbos;

    public zzceb(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    private final void zzK(long j) {
        for (String put : this.zzboq.keySet()) {
            this.zzboq.put(put, Long.valueOf(j));
        }
        if (!this.zzboq.isEmpty()) {
            this.zzbos = j;
        }
    }

    @WorkerThread
    private final void zza(long j, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        if (com_google_android_gms_measurement_AppMeasurement_zzb == null) {
            super.zzwF().zzyD().log("Not logging ad exposure. No active activity");
        } else if (j < 1000) {
            super.zzwF().zzyD().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("_xt", j);
            zzchy.zza(com_google_android_gms_measurement_AppMeasurement_zzb, bundle);
            super.zzwt().zzd("am", "_xa", bundle);
        }
    }

    @WorkerThread
    private final void zza(String str, long j, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        if (com_google_android_gms_measurement_AppMeasurement_zzb == null) {
            super.zzwF().zzyD().log("Not logging ad unit exposure. No active activity");
        } else if (j < 1000) {
            super.zzwF().zzyD().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("_ai", str);
            bundle.putLong("_xt", j);
            zzchy.zza(com_google_android_gms_measurement_AppMeasurement_zzb, bundle);
            super.zzwt().zzd("am", "_xu", bundle);
        }
    }

    @WorkerThread
    private final void zzd(String str, long j) {
        super.zzwp();
        super.zzjC();
        zzbo.zzcF(str);
        if (this.zzbor.isEmpty()) {
            this.zzbos = j;
        }
        Integer num = (Integer) this.zzbor.get(str);
        if (num != null) {
            this.zzbor.put(str, Integer.valueOf(num.intValue() + 1));
        } else if (this.zzbor.size() >= 100) {
            super.zzwF().zzyz().log("Too many ads visible");
        } else {
            this.zzbor.put(str, Integer.valueOf(1));
            this.zzboq.put(str, Long.valueOf(j));
        }
    }

    @WorkerThread
    private final void zze(String str, long j) {
        super.zzwp();
        super.zzjC();
        zzbo.zzcF(str);
        Integer num = (Integer) this.zzbor.get(str);
        if (num != null) {
            zzb zzzh = super.zzwx().zzzh();
            int intValue = num.intValue() - 1;
            if (intValue == 0) {
                this.zzbor.remove(str);
                Long l = (Long) this.zzboq.get(str);
                if (l == null) {
                    super.zzwF().zzyx().log("First ad unit exposure time was never set");
                } else {
                    long longValue = j - l.longValue();
                    this.zzboq.remove(str);
                    zza(str, longValue, zzzh);
                }
                if (!this.zzbor.isEmpty()) {
                    return;
                }
                if (this.zzbos == 0) {
                    super.zzwF().zzyx().log("First ad exposure time was never set");
                    return;
                }
                zza(j - this.zzbos, zzzh);
                this.zzbos = 0;
                return;
            }
            this.zzbor.put(str, Integer.valueOf(intValue));
            return;
        }
        super.zzwF().zzyx().zzj("Call to endAdUnitExposure for unknown ad unit id", str);
    }

    public final void beginAdUnitExposure(String str) {
        if (str == null || str.length() == 0) {
            super.zzwF().zzyx().log("Ad unit id must be a non-empty string");
            return;
        }
        super.zzwE().zzj(new zzcec(this, str, super.zzkq().elapsedRealtime()));
    }

    public final void endAdUnitExposure(String str) {
        if (str == null || str.length() == 0) {
            super.zzwF().zzyx().log("Ad unit id must be a non-empty string");
            return;
        }
        super.zzwE().zzj(new zzced(this, str, super.zzkq().elapsedRealtime()));
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public final void zzJ(long j) {
        zzb zzzh = super.zzwx().zzzh();
        for (String str : this.zzboq.keySet()) {
            zza(str, j - ((Long) this.zzboq.get(str)).longValue(), zzzh);
        }
        if (!this.zzboq.isEmpty()) {
            zza(j - this.zzbos, zzzh);
        }
        zzK(j);
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
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

    public final void zzwn() {
        super.zzwE().zzj(new zzcee(this, super.zzkq().elapsedRealtime()));
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
}
