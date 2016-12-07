package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzu {
    static Boolean aqm;
    static Boolean aqn;
    private final zza aqo;

    public interface zza {
        void doStartService(Context context, Intent intent);
    }

    public zzu(zza com_google_android_gms_measurement_internal_zzu_zza) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzu_zza);
        this.aqo = com_google_android_gms_measurement_internal_zzu_zza;
    }

    public static boolean zzh(Context context, boolean z) {
        zzac.zzy(context);
        if (aqm != null && !z) {
            return aqm.booleanValue();
        }
        if (aqn != null && z) {
            return aqn.booleanValue();
        }
        boolean zza = zzal.zza(context, z ? "com.google.android.gms.measurement.PackageMeasurementReceiver" : "com.google.android.gms.measurement.AppMeasurementReceiver", false);
        if (z) {
            aqn = Boolean.valueOf(zza);
            return zza;
        }
        aqm = Boolean.valueOf(zza);
        return zza;
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        final zzx zzdt = zzx.zzdt(context);
        final zzp zzbvg = zzdt.zzbvg();
        if (intent == null) {
            zzbvg.zzbwe().log("Receiver called with null intent");
            return;
        }
        boolean zzact = zzdt.zzbvi().zzact();
        String action = intent.getAction();
        if (zzact) {
            zzbvg.zzbwj().zzj("Device receiver got", action);
        } else {
            zzbvg.zzbwj().zzj("Local receiver got", action);
        }
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            boolean z = zzact && !zzdt.zzbxg();
            zzae.zzi(context, z);
            Intent intent2 = new Intent();
            action = (!zzact || zzdt.zzbxg()) ? "com.google.android.gms.measurement.AppMeasurementService" : "com.google.android.gms.measurement.PackageMeasurementService";
            Intent className = intent2.setClassName(context, action);
            className.setAction("com.google.android.gms.measurement.UPLOAD");
            this.aqo.doStartService(context, className);
        } else if (!zzact && "com.android.vending.INSTALL_REFERRER".equals(action)) {
            action = intent.getStringExtra("referrer");
            if (action == null) {
                zzbvg.zzbwj().log("Install referrer extras are null");
                return;
            }
            final Bundle zzt = zzdt.zzbvc().zzt(Uri.parse(action));
            if (zzt == null) {
                zzbvg.zzbwj().log("No campaign defined in install referrer broadcast");
                return;
            }
            final long longExtra = 1000 * intent.getLongExtra("referrer_timestamp_seconds", 0);
            if (longExtra == 0) {
                zzbvg.zzbwe().log("Install referrer is missing timestamp");
            }
            final Context context2 = context;
            zzdt.zzbvf().zzm(new Runnable(this) {
                final /* synthetic */ zzu aqt;

                public void run() {
                    zzak zzas = zzdt.zzbvb().zzas(zzdt.zzbuy().zzti(), "_fot");
                    long longValue = (zzas == null || !(zzas.zzctv instanceof Long)) ? 0 : ((Long) zzas.zzctv).longValue();
                    long j = longExtra;
                    longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
                    if (longValue > 0) {
                        zzt.putLong("click_timestamp", longValue);
                    }
                    AppMeasurement.getInstance(context2).zze("auto", "_cmp", zzt);
                    zzbvg.zzbwj().log("Install campaign recorded");
                }
            });
        }
    }
}
