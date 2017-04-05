package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzaub {
    private final zza zzbty;

    public interface zza {
        void doStartService(Context context, Intent intent);
    }

    public zzaub(zza com_google_android_gms_internal_zzaub_zza) {
        zzac.zzw(com_google_android_gms_internal_zzaub_zza);
        this.zzbty = com_google_android_gms_internal_zzaub_zza;
    }

    public static boolean zzi(Context context, boolean z) {
        zzac.zzw(context);
        return zzaut.zza(context, z ? "com.google.android.gms.measurement.PackageMeasurementReceiver" : "com.google.android.gms.measurement.AppMeasurementReceiver", false);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        final zzaue zzbM = zzaue.zzbM(context);
        final zzatx zzKl = zzbM.zzKl();
        if (intent == null) {
            zzKl.zzMa().log("Receiver called with null intent");
            return;
        }
        zzbM.zzKn().zzLg();
        String action = intent.getAction();
        zzKl.zzMe().zzj("Local receiver got", action);
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            zzaum.zzj(context, false);
            Intent className = new Intent().setClassName(context, "com.google.android.gms.measurement.AppMeasurementService");
            className.setAction("com.google.android.gms.measurement.UPLOAD");
            this.zzbty.doStartService(context, className);
        } else if ("com.android.vending.INSTALL_REFERRER".equals(action)) {
            action = intent.getStringExtra("referrer");
            if (action == null) {
                zzKl.zzMe().log("Install referrer extras are null");
                return;
            }
            final Bundle zzu = zzbM.zzKh().zzu(Uri.parse(action));
            if (zzu == null) {
                zzKl.zzMe().log("No campaign defined in install referrer broadcast");
                return;
            }
            final long longExtra = 1000 * intent.getLongExtra("referrer_timestamp_seconds", 0);
            if (longExtra == 0) {
                zzKl.zzMa().log("Install referrer is missing timestamp");
            }
            final Context context2 = context;
            zzbM.zzKk().zzm(new Runnable(this) {
                public void run() {
                    zzaus zzS = zzbM.zzKg().zzS(zzbM.zzKb().zzke(), "_fot");
                    long longValue = (zzS == null || !(zzS.mValue instanceof Long)) ? 0 : ((Long) zzS.mValue).longValue();
                    long j = longExtra;
                    longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
                    if (longValue > 0) {
                        zzu.putLong("click_timestamp", longValue);
                    }
                    AppMeasurement.getInstance(context2).logEventInternal("auto", "_cmp", zzu);
                    zzKl.zzMe().log("Install campaign recorded");
                }
            });
        }
    }
}
