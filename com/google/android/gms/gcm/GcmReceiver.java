package com.google.android.gms.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.iid.zzh;

public class GcmReceiver extends WakefulBroadcastReceiver {
    private static boolean zzicc = false;
    private static zzh zzicd;
    private static zzh zzice;

    private final void doStartService(Context context, Intent intent) {
        if (isOrderedBroadcast()) {
            setResultCode(500);
        }
        ResolveInfo resolveService = context.getPackageManager().resolveService(intent, 0);
        if (resolveService == null || resolveService.serviceInfo == null) {
            Log.e("GcmReceiver", "Failed to resolve target intent service, skipping classname enforcement");
        } else {
            ServiceInfo serviceInfo = resolveService.serviceInfo;
            String str;
            String str2;
            if (!context.getPackageName().equals(serviceInfo.packageName) || serviceInfo.name == null) {
                str = serviceInfo.packageName;
                str2 = serviceInfo.name;
                Log.e("GcmReceiver", new StringBuilder((String.valueOf(str).length() + 94) + String.valueOf(str2).length()).append("Error resolving target intent service, skipping classname enforcement. Resolved service was: ").append(str).append("/").append(str2).toString());
            } else {
                String valueOf;
                str2 = serviceInfo.name;
                if (str2.startsWith(".")) {
                    valueOf = String.valueOf(context.getPackageName());
                    str2 = String.valueOf(str2);
                    str2 = str2.length() != 0 ? valueOf.concat(str2) : new String(valueOf);
                }
                if (Log.isLoggable("GcmReceiver", 3)) {
                    str = "GcmReceiver";
                    String str3 = "Restricting intent to a specific service: ";
                    valueOf = String.valueOf(str2);
                    Log.d(str, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                }
                intent.setClassName(context.getPackageName(), str2);
            }
        }
        try {
            ComponentName startWakefulService;
            if (context.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0) {
                startWakefulService = WakefulBroadcastReceiver.startWakefulService(context, intent);
            } else {
                startWakefulService = context.startService(intent);
                Log.d("GcmReceiver", "Missing wake lock permission, service start may be delayed");
            }
            if (startWakefulService == null) {
                Log.e("GcmReceiver", "Error while delivering the message: ServiceIntent not found.");
                if (isOrderedBroadcast()) {
                    setResultCode(404);
                }
            } else if (isOrderedBroadcast()) {
                setResultCode(-1);
            }
        } catch (Throwable e) {
            Log.e("GcmReceiver", "Error while delivering the message to the serviceIntent", e);
            if (isOrderedBroadcast()) {
                setResultCode(401);
            }
        }
    }

    private final synchronized zzh zzae(Context context, String str) {
        zzh com_google_android_gms_iid_zzh;
        if ("com.google.android.c2dm.intent.RECEIVE".equals(str)) {
            if (zzice == null) {
                zzice = new zzh(context, str);
            }
            com_google_android_gms_iid_zzh = zzice;
        } else {
            if (zzicd == null) {
                zzicd = new zzh(context, str);
            }
            com_google_android_gms_iid_zzh = zzicd;
        }
        return com_google_android_gms_iid_zzh;
    }

    public void onReceive(Context context, Intent intent) {
        int i = 0;
        if (Log.isLoggable("GcmReceiver", 3)) {
            Log.d("GcmReceiver", "received new intent");
        }
        intent.setComponent(null);
        intent.setPackage(context.getPackageName());
        if (VERSION.SDK_INT <= 18) {
            intent.removeCategory(context.getPackageName());
        }
        String stringExtra = intent.getStringExtra("from");
        if ("google.com/iid".equals(stringExtra) || "gcm.googleapis.com/refresh".equals(stringExtra)) {
            intent.setAction("com.google.android.gms.iid.InstanceID");
        }
        stringExtra = intent.getStringExtra("gcm.rawData64");
        if (stringExtra != null) {
            intent.putExtra("rawData", Base64.decode(stringExtra, 0));
            intent.removeExtra("gcm.rawData64");
        }
        if (zzq.isAtLeastO() && context.getApplicationInfo().targetSdkVersion > 25) {
            i = 1;
        }
        if (i != 0) {
            if (isOrderedBroadcast()) {
                setResultCode(-1);
            }
            zzae(context, intent.getAction()).zza(intent, goAsync());
            return;
        }
        if ("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            doStartService(context, intent);
        } else {
            doStartService(context, intent);
        }
        if (isOrderedBroadcast() && getResultCode() == 0) {
            setResultCode(-1);
        }
    }
}
