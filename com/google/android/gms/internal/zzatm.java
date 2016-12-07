package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzatm {
    private final zza zzbsC;

    public interface zza {
        void doStartService(Context context, Intent intent);
    }

    public zzatm(zza com_google_android_gms_internal_zzatm_zza) {
        zzac.zzw(com_google_android_gms_internal_zzatm_zza);
        this.zzbsC = com_google_android_gms_internal_zzatm_zza;
    }

    public static boolean zzi(Context context, boolean z) {
        zzac.zzw(context);
        return zzaue.zza(context, z ? "com.google.android.gms.measurement.PackageMeasurementReceiver" : "com.google.android.gms.measurement.AppMeasurementReceiver", false);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        final zzatp zzbu = zzatp.zzbu(context);
        final zzati zzJt = zzbu.zzJt();
        if (intent == null) {
            zzJt.zzLc().log("Receiver called with null intent");
            return;
        }
        zzbu.zzJv().zzKk();
        String action = intent.getAction();
        zzJt.zzLg().zzj("Local receiver got", action);
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            zzatx.zzj(context, false);
            Intent className = new Intent().setClassName(context, "com.google.android.gms.measurement.AppMeasurementService");
            className.setAction("com.google.android.gms.measurement.UPLOAD");
            this.zzbsC.doStartService(context, className);
        } else if ("com.android.vending.INSTALL_REFERRER".equals(action)) {
            action = intent.getStringExtra("referrer");
            if (action == null) {
                zzJt.zzLg().log("Install referrer extras are null");
                return;
            }
            final Bundle zzu = zzbu.zzJp().zzu(Uri.parse(action));
            if (zzu == null) {
                zzJt.zzLg().log("No campaign defined in install referrer broadcast");
                return;
            }
            final long longExtra = 1000 * intent.getLongExtra("referrer_timestamp_seconds", 0);
            if (longExtra == 0) {
                zzJt.zzLc().log("Install referrer is missing timestamp");
            }
            final Context context2 = context;
            zzbu.zzJs().zzm(new Runnable(this) {
                public void run() {
                    zzaud zzR = zzbu.zzJo().zzR(zzbu.zzJj().zzjI(), "_fot");
                    long longValue = (zzR == null || !(zzR.zzYe instanceof Long)) ? 0 : ((Long) zzR.zzYe).longValue();
                    long j = longExtra;
                    longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
                    if (longValue > 0) {
                        zzu.putLong("click_timestamp", longValue);
                    }
                    AppMeasurement.getInstance(context2).logEventInternal("auto", "_cmp", zzu);
                    zzJt.zzLg().log("Install campaign recorded");
                }
            });
        }
    }
}
