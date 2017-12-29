package com.google.android.gms.internal;

import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.common.internal.zzbq;

public final class zzcid {
    private final zzcif zzjds;

    public zzcid(zzcif com_google_android_gms_internal_zzcif) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcif);
        this.zzjds = com_google_android_gms_internal_zzcif;
    }

    public static boolean zzbk(Context context) {
        zzbq.checkNotNull(context);
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            ActivityInfo receiverInfo = packageManager.getReceiverInfo(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementReceiver"), 2);
            return receiverInfo != null && receiverInfo.enabled;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public final void onReceive(Context context, Intent intent) {
        zzcim zzdx = zzcim.zzdx(context);
        zzchm zzawy = zzdx.zzawy();
        if (intent == null) {
            zzawy.zzazf().log("Receiver called with null intent");
            return;
        }
        String action = intent.getAction();
        zzawy.zzazj().zzj("Local receiver got", action);
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            Intent className = new Intent().setClassName(context, "com.google.android.gms.measurement.AppMeasurementService");
            className.setAction("com.google.android.gms.measurement.UPLOAD");
            zzawy.zzazj().log("Starting wakeful intent.");
            this.zzjds.doStartService(context, className);
        } else if ("com.android.vending.INSTALL_REFERRER".equals(action)) {
            PendingResult doGoAsync = this.zzjds.doGoAsync();
            action = intent.getStringExtra("referrer");
            if (action == null) {
                zzawy.zzazj().log("Install referrer extras are null");
                if (doGoAsync != null) {
                    doGoAsync.finish();
                    return;
                }
                return;
            }
            zzawy.zzazh().zzj("Install referrer extras are", action);
            if (!action.contains("?")) {
                String str = "?";
                action = String.valueOf(action);
                action = action.length() != 0 ? str.concat(action) : new String(str);
            }
            Bundle zzp = zzdx.zzawu().zzp(Uri.parse(action));
            if (zzp == null) {
                zzawy.zzazj().log("No campaign defined in install referrer broadcast");
                if (doGoAsync != null) {
                    doGoAsync.finish();
                    return;
                }
                return;
            }
            long longExtra = 1000 * intent.getLongExtra("referrer_timestamp_seconds", 0);
            if (longExtra == 0) {
                zzawy.zzazf().log("Install referrer is missing timestamp");
            }
            zzdx.zzawx().zzg(new zzcie(this, zzdx, longExtra, zzp, context, zzawy, doGoAsync));
        }
    }
}
