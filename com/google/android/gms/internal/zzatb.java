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

public class zzatb extends zzaug {
    private final Map<String, Long> zzbql = new ArrayMap();
    private final Map<String, Integer> zzbqm = new ArrayMap();
    private long zzbqn;

    public zzatb(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @WorkerThread
    private void zzX(long j) {
        for (String put : this.zzbql.keySet()) {
            this.zzbql.put(put, Long.valueOf(j));
        }
        if (!this.zzbql.isEmpty()) {
            this.zzbqn = j;
        }
    }

    @WorkerThread
    private void zza(long j, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
            zzKl().zzMe().log("Not logging ad exposure. No active activity");
        } else if (j < 1000) {
            zzKl().zzMe().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("_xt", j);
            zzauk.zza(com_google_android_gms_measurement_AppMeasurement_zzf, bundle);
            zzKa().zze("am", "_xa", bundle);
        }
    }

    @WorkerThread
    private void zza(String str, long j, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
            zzKl().zzMe().log("Not logging ad unit exposure. No active activity");
        } else if (j < 1000) {
            zzKl().zzMe().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("_ai", str);
            bundle.putLong("_xt", j);
            zzauk.zza(com_google_android_gms_measurement_AppMeasurement_zzf, bundle);
            zzKa().zze("am", "_xu", bundle);
        }
    }

    @WorkerThread
    private void zzf(String str, long j) {
        zzJW();
        zzmR();
        zzac.zzdr(str);
        if (this.zzbqm.isEmpty()) {
            this.zzbqn = j;
        }
        Integer num = (Integer) this.zzbqm.get(str);
        if (num != null) {
            this.zzbqm.put(str, Integer.valueOf(num.intValue() + 1));
        } else if (this.zzbqm.size() >= 100) {
            zzKl().zzMa().log("Too many ads visible");
        } else {
            this.zzbqm.put(str, Integer.valueOf(1));
            this.zzbql.put(str, Long.valueOf(j));
        }
    }

    @WorkerThread
    private void zzg(String str, long j) {
        zzJW();
        zzmR();
        zzac.zzdr(str);
        Integer num = (Integer) this.zzbqm.get(str);
        if (num != null) {
            zzf zzMU = zzKe().zzMU();
            int intValue = num.intValue() - 1;
            if (intValue == 0) {
                this.zzbqm.remove(str);
                Long l = (Long) this.zzbql.get(str);
                if (l == null) {
                    zzKl().zzLY().log("First ad unit exposure time was never set");
                } else {
                    long longValue = j - l.longValue();
                    this.zzbql.remove(str);
                    zza(str, longValue, zzMU);
                }
                if (!this.zzbqm.isEmpty()) {
                    return;
                }
                if (this.zzbqn == 0) {
                    zzKl().zzLY().log("First ad exposure time was never set");
                    return;
                }
                zza(j - this.zzbqn, zzMU);
                this.zzbqn = 0;
                return;
            }
            this.zzbqm.put(str, Integer.valueOf(intValue));
            return;
        }
        zzKl().zzLY().zzj("Call to endAdUnitExposure for unknown ad unit id", str);
    }

    public void beginAdUnitExposure(final String str) {
        int i = VERSION.SDK_INT;
        if (str == null || str.length() == 0) {
            zzKl().zzLY().log("Ad unit id must be a non-empty string");
            return;
        }
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzatb zzbqp;

            public void run() {
                this.zzbqp.zzf(str, elapsedRealtime);
            }
        });
    }

    public void endAdUnitExposure(final String str) {
        int i = VERSION.SDK_INT;
        if (str == null || str.length() == 0) {
            zzKl().zzLY().log("Ad unit id must be a non-empty string");
            return;
        }
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzatb zzbqp;

            public void run() {
                this.zzbqp.zzg(str, elapsedRealtime);
            }
        });
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public void zzJU() {
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKk().zzm(new Runnable(this) {
            final /* synthetic */ zzatb zzbqp;

            public void run() {
                this.zzbqp.zzX(elapsedRealtime);
            }
        });
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    @WorkerThread
    public void zzW(long j) {
        zzf zzMU = zzKe().zzMU();
        for (String str : this.zzbql.keySet()) {
            zza(str, j - ((Long) this.zzbql.get(str)).longValue(), zzMU);
        }
        if (!this.zzbql.isEmpty()) {
            zza(j - this.zzbqn, zzMU);
        }
        zzX(j);
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }
}
