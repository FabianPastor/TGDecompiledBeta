package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.Map;

public final class zzcgd extends zzcjk {
    private final Map<String, Long> zziwr = new ArrayMap();
    private final Map<String, Integer> zziws = new ArrayMap();
    private long zziwt;

    public zzcgd(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final void zza(long j, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        if (com_google_android_gms_measurement_AppMeasurement_zzb == null) {
            zzawy().zzazj().log("Not logging ad exposure. No active activity");
        } else if (j < 1000) {
            zzawy().zzazj().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("_xt", j);
            zzckc.zza(com_google_android_gms_measurement_AppMeasurement_zzb, bundle);
            zzawm().zzc("am", "_xa", bundle);
        }
    }

    private final void zza(String str, long j, zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        if (com_google_android_gms_measurement_AppMeasurement_zzb == null) {
            zzawy().zzazj().log("Not logging ad unit exposure. No active activity");
        } else if (j < 1000) {
            zzawy().zzazj().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("_ai", str);
            bundle.putLong("_xt", j);
            zzckc.zza(com_google_android_gms_measurement_AppMeasurement_zzb, bundle);
            zzawm().zzc("am", "_xu", bundle);
        }
    }

    private final void zzak(long j) {
        for (String put : this.zziwr.keySet()) {
            this.zziwr.put(put, Long.valueOf(j));
        }
        if (!this.zziwr.isEmpty()) {
            this.zziwt = j;
        }
    }

    private final void zzd(String str, long j) {
        zzve();
        zzbq.zzgm(str);
        if (this.zziws.isEmpty()) {
            this.zziwt = j;
        }
        Integer num = (Integer) this.zziws.get(str);
        if (num != null) {
            this.zziws.put(str, Integer.valueOf(num.intValue() + 1));
        } else if (this.zziws.size() >= 100) {
            zzawy().zzazf().log("Too many ads visible");
        } else {
            this.zziws.put(str, Integer.valueOf(1));
            this.zziwr.put(str, Long.valueOf(j));
        }
    }

    private final void zze(String str, long j) {
        zzve();
        zzbq.zzgm(str);
        Integer num = (Integer) this.zziws.get(str);
        if (num != null) {
            zzb zzbao = zzawq().zzbao();
            int intValue = num.intValue() - 1;
            if (intValue == 0) {
                this.zziws.remove(str);
                Long l = (Long) this.zziwr.get(str);
                if (l == null) {
                    zzawy().zzazd().log("First ad unit exposure time was never set");
                } else {
                    long longValue = j - l.longValue();
                    this.zziwr.remove(str);
                    zza(str, longValue, zzbao);
                }
                if (!this.zziws.isEmpty()) {
                    return;
                }
                if (this.zziwt == 0) {
                    zzawy().zzazd().log("First ad exposure time was never set");
                    return;
                }
                zza(j - this.zziwt, zzbao);
                this.zziwt = 0;
                return;
            }
            this.zziws.put(str, Integer.valueOf(intValue));
            return;
        }
        zzawy().zzazd().zzj("Call to endAdUnitExposure for unknown ad unit id", str);
    }

    public final void beginAdUnitExposure(String str) {
        if (str == null || str.length() == 0) {
            zzawy().zzazd().log("Ad unit id must be a non-empty string");
            return;
        }
        zzawx().zzg(new zzcge(this, str, zzws().elapsedRealtime()));
    }

    public final void endAdUnitExposure(String str) {
        if (str == null || str.length() == 0) {
            zzawy().zzazd().log("Ad unit id must be a non-empty string");
            return;
        }
        zzawx().zzg(new zzcgf(this, str, zzws().elapsedRealtime()));
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final void zzaj(long j) {
        zzb zzbao = zzawq().zzbao();
        for (String str : this.zziwr.keySet()) {
            zza(str, j - ((Long) this.zziwr.get(str)).longValue(), zzbao);
        }
        if (!this.zziwr.isEmpty()) {
            zza(j - this.zziwt, zzbao);
        }
        zzak(j);
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final /* bridge */ /* synthetic */ void zzawj() {
        super.zzawj();
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
