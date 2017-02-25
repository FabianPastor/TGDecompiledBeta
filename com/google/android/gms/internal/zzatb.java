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
    private final Map<String, Long> zzbqp = new ArrayMap();
    private final Map<String, Integer> zzbqq = new ArrayMap();
    private long zzbqr;

    public zzatb(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @WorkerThread
    private void zzX(long j) {
        for (String put : this.zzbqp.keySet()) {
            this.zzbqp.put(put, Long.valueOf(j));
        }
        if (!this.zzbqp.isEmpty()) {
            this.zzbqr = j;
        }
    }

    @WorkerThread
    private void zza(long j, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
            zzKk().zzMd().log("Not logging ad exposure. No active activity");
        } else if (j < 1000) {
            zzKk().zzMd().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putLong("_xt", j);
            zzauk.zza(com_google_android_gms_measurement_AppMeasurement_zzf, bundle);
            zzJZ().zze("am", "_xa", bundle);
        }
    }

    @WorkerThread
    private void zza(String str, long j, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        if (com_google_android_gms_measurement_AppMeasurement_zzf == null) {
            zzKk().zzMd().log("Not logging ad unit exposure. No active activity");
        } else if (j < 1000) {
            zzKk().zzMd().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(j));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("_ai", str);
            bundle.putLong("_xt", j);
            zzauk.zza(com_google_android_gms_measurement_AppMeasurement_zzf, bundle);
            zzJZ().zze("am", "_xu", bundle);
        }
    }

    @WorkerThread
    private void zzf(String str, long j) {
        zzJV();
        zzmR();
        zzac.zzdr(str);
        if (this.zzbqq.isEmpty()) {
            this.zzbqr = j;
        }
        Integer num = (Integer) this.zzbqq.get(str);
        if (num != null) {
            this.zzbqq.put(str, Integer.valueOf(num.intValue() + 1));
        } else if (this.zzbqq.size() >= 100) {
            zzKk().zzLZ().log("Too many ads visible");
        } else {
            this.zzbqq.put(str, Integer.valueOf(1));
            this.zzbqp.put(str, Long.valueOf(j));
        }
    }

    @WorkerThread
    private void zzg(String str, long j) {
        zzJV();
        zzmR();
        zzac.zzdr(str);
        Integer num = (Integer) this.zzbqq.get(str);
        if (num != null) {
            zzf zzMT = zzKd().zzMT();
            int intValue = num.intValue() - 1;
            if (intValue == 0) {
                this.zzbqq.remove(str);
                Long l = (Long) this.zzbqp.get(str);
                if (l == null) {
                    zzKk().zzLX().log("First ad unit exposure time was never set");
                } else {
                    long longValue = j - l.longValue();
                    this.zzbqp.remove(str);
                    zza(str, longValue, zzMT);
                }
                if (!this.zzbqq.isEmpty()) {
                    return;
                }
                if (this.zzbqr == 0) {
                    zzKk().zzLX().log("First ad exposure time was never set");
                    return;
                }
                zza(j - this.zzbqr, zzMT);
                this.zzbqr = 0;
                return;
            }
            this.zzbqq.put(str, Integer.valueOf(intValue));
            return;
        }
        zzKk().zzLX().zzj("Call to endAdUnitExposure for unknown ad unit id", str);
    }

    public void beginAdUnitExposure(final String str) {
        int i = VERSION.SDK_INT;
        if (str == null || str.length() == 0) {
            zzKk().zzLX().log("Ad unit id must be a non-empty string");
            return;
        }
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzatb zzbqt;

            public void run() {
                this.zzbqt.zzf(str, elapsedRealtime);
            }
        });
    }

    public void endAdUnitExposure(final String str) {
        int i = VERSION.SDK_INT;
        if (str == null || str.length() == 0) {
            zzKk().zzLX().log("Ad unit id must be a non-empty string");
            return;
        }
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzatb zzbqt;

            public void run() {
                this.zzbqt.zzg(str, elapsedRealtime);
            }
        });
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public void zzJT() {
        final long elapsedRealtime = zznR().elapsedRealtime();
        zzKj().zzm(new Runnable(this) {
            final /* synthetic */ zzatb zzbqt;

            public void run() {
                this.zzbqt.zzX(elapsedRealtime);
            }
        });
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

    @WorkerThread
    public void zzW(long j) {
        zzf zzMT = zzKd().zzMT();
        for (String str : this.zzbqp.keySet()) {
            zza(str, j - ((Long) this.zzbqp.get(str)).longValue(), zzMT);
        }
        if (!this.zzbqp.isEmpty()) {
            zza(j - this.zzbqr, zzMT);
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
