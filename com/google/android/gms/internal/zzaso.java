package com.google.android.gms.internal;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.Map;

public class zzaso extends zzatr {
    private final Map<String, Long> zzbpF = new ArrayMap();
    private final Map<String, Integer> zzbpG = new ArrayMap();
    private long zzbpH;

    public zzaso(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    @WorkerThread
    private void zzW(long j) {
        for (String put : this.zzbpF.keySet()) {
            this.zzbpF.put(put, Long.valueOf(j));
        }
        if (!this.zzbpF.isEmpty()) {
            this.zzbpH = j;
        }
    }

    @WorkerThread
    private void zza(long j, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
            zzJt().zzLg().log("Not logging ad exposure. No active activity");
        } else if (j < 1000) {
            zzJt().zzLg().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("_xt", j);
            zzatv.zza(com_google_android_gms_measurement_AppMeasurement_zzf, bundle);
            zzJi().zze("am", "_xa", bundle);
        }
    }

    @WorkerThread
    private void zza(String str, long j, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
            zzJt().zzLg().log("Not logging ad unit exposure. No active activity");
        } else if (j < 1000) {
            zzJt().zzLg().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("_ai", str);
            bundle.putLong("_xt", j);
            zzatv.zza(com_google_android_gms_measurement_AppMeasurement_zzf, bundle);
            zzJi().zze("am", "_xu", bundle);
        }
    }

    @WorkerThread
    private void zzf(String str, long j) {
        zzJe();
        zzmq();
        zzac.zzdv(str);
        if (this.zzbpG.isEmpty()) {
            this.zzbpH = j;
        }
        Integer num = (Integer) this.zzbpG.get(str);
        if (num != null) {
            this.zzbpG.put(str, Integer.valueOf(num.intValue() + 1));
        } else if (this.zzbpG.size() >= 100) {
            zzJt().zzLc().log("Too many ads visible");
        } else {
            this.zzbpG.put(str, Integer.valueOf(1));
            this.zzbpF.put(str, Long.valueOf(j));
        }
    }

    @WorkerThread
    private void zzg(String str, long j) {
        zzJe();
        zzmq();
        zzac.zzdv(str);
        Integer num = (Integer) this.zzbpG.get(str);
        if (num != null) {
            zzf zzLU = zzJm().zzLU();
            int intValue = num.intValue() - 1;
            if (intValue == 0) {
                this.zzbpG.remove(str);
                Long l = (Long) this.zzbpF.get(str);
                if (l == null) {
                    zzJt().zzLa().log("First ad unit exposure time was never set");
                } else {
                    long longValue = j - l.longValue();
                    this.zzbpF.remove(str);
                    zza(str, longValue, zzLU);
                }
                if (!this.zzbpG.isEmpty()) {
                    return;
                }
                if (this.zzbpH == 0) {
                    zzJt().zzLa().log("First ad exposure time was never set");
                    return;
                }
                zza(j - this.zzbpH, zzLU);
                this.zzbpH = 0;
                return;
            }
            this.zzbpG.put(str, Integer.valueOf(intValue));
            return;
        }
        zzJt().zzLa().zzj("Call to endAdUnitExposure for unknown ad unit id", str);
    }

    public void beginAdUnitExposure(final String str) {
        if (VERSION.SDK_INT >= 14) {
            if (str == null || str.length() == 0) {
                zzJt().zzLa().log("Ad unit id must be a non-empty string");
                return;
            }
            final long elapsedRealtime = zznq().elapsedRealtime();
            zzJs().zzm(new Runnable(this) {
                final /* synthetic */ zzaso zzbpJ;

                public void run() {
                    this.zzbpJ.zzf(str, elapsedRealtime);
                }
            });
        }
    }

    public void endAdUnitExposure(final String str) {
        if (VERSION.SDK_INT >= 14) {
            if (str == null || str.length() == 0) {
                zzJt().zzLa().log("Ad unit id must be a non-empty string");
                return;
            }
            final long elapsedRealtime = zznq().elapsedRealtime();
            zzJs().zzm(new Runnable(this) {
                final /* synthetic */ zzaso zzbpJ;

                public void run() {
                    this.zzbpJ.zzg(str, elapsedRealtime);
                }
            });
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public void zzJc() {
        final long elapsedRealtime = zznq().elapsedRealtime();
        zzJs().zzm(new Runnable(this) {
            final /* synthetic */ zzaso zzbpJ;

            public void run() {
                this.zzbpJ.zzW(elapsedRealtime);
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    @WorkerThread
    public void zzV(long j) {
        zzf zzLU = zzJm().zzLU();
        for (String str : this.zzbpF.keySet()) {
            zza(str, j - ((Long) this.zzbpF.get(str)).longValue(), zzLU);
        }
        if (!this.zzbpF.isEmpty()) {
            zza(j - this.zzbpH, zzLU);
        }
        zzW(j);
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
