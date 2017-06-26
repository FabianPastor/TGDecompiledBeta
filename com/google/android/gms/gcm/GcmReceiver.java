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
import com.google.android.gms.wallet.WalletConstants;
import com.google.firebase.iid.zzh;

public class GcmReceiver extends WakefulBroadcastReceiver {
    private static String zzbfA = "gcm.googleapis.com/refresh";
    private static boolean zzbfB = false;
    private zzh zzbfC;
    private zzh zzbfD;

    private final void doStartService(Context context, Intent intent) {
        if (isOrderedBroadcast()) {
            setResultCode(500);
        }
        ResolveInfo resolveService = context.getPackageManager().resolveService(intent, 0);
        if (resolveService == null || resolveService.serviceInfo == null) {
            Log.e("GcmReceiver", "Failed to resolve target intent service, skipping classname enforcement");
        } else {
            ServiceInfo serviceInfo = resolveService.serviceInfo;
            String valueOf;
            String valueOf2;
            if (!context.getPackageName().equals(serviceInfo.packageName) || serviceInfo.name == null) {
                valueOf = String.valueOf(serviceInfo.packageName);
                valueOf2 = String.valueOf(serviceInfo.name);
                Log.e("GcmReceiver", new StringBuilder((String.valueOf(valueOf).length() + 94) + String.valueOf(valueOf2).length()).append("Error resolving target intent service, skipping classname enforcement. Resolved service was: ").append(valueOf).append("/").append(valueOf2).toString());
            } else {
                String valueOf3;
                valueOf2 = serviceInfo.name;
                if (valueOf2.startsWith(".")) {
                    valueOf3 = String.valueOf(context.getPackageName());
                    valueOf2 = String.valueOf(valueOf2);
                    valueOf2 = valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3);
                }
                if (Log.isLoggable("GcmReceiver", 3)) {
                    valueOf = "GcmReceiver";
                    String str = "Restricting intent to a specific service: ";
                    valueOf3 = String.valueOf(valueOf2);
                    Log.d(valueOf, valueOf3.length() != 0 ? str.concat(valueOf3) : new String(str));
                }
                intent.setClassName(context.getPackageName(), valueOf2);
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
                    setResultCode(WalletConstants.ERROR_CODE_INVALID_PARAMETERS);
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

    private final synchronized zzh zzH(Context context, String str) {
        zzh com_google_firebase_iid_zzh;
        if ("com.google.android.c2dm.intent.RECEIVE".equals(str)) {
            if (this.zzbfD == null) {
                this.zzbfD = new zzh(context, str);
            }
            com_google_firebase_iid_zzh = this.zzbfD;
        } else {
            if (this.zzbfC == null) {
                this.zzbfC = new zzh(context, str);
            }
            com_google_firebase_iid_zzh = this.zzbfC;
        }
        return com_google_firebase_iid_zzh;
    }

    public void onReceive(Context context, Intent intent) {
        if (Log.isLoggable("GcmReceiver", 3)) {
            Log.d("GcmReceiver", "received new intent");
        }
        intent.setComponent(null);
        intent.setPackage(context.getPackageName());
        if (VERSION.SDK_INT <= 18) {
            intent.removeCategory(context.getPackageName());
        }
        String stringExtra = intent.getStringExtra("from");
        if ("google.com/iid".equals(stringExtra) || zzbfA.equals(stringExtra)) {
            intent.setAction("com.google.android.gms.iid.InstanceID");
        }
        stringExtra = intent.getStringExtra("gcm.rawData64");
        if (stringExtra != null) {
            intent.putExtra("rawData", Base64.decode(stringExtra, 0));
            intent.removeExtra("gcm.rawData64");
        }
        if (zzq.isAtLeastO()) {
            if (isOrderedBroadcast()) {
                setResultCode(-1);
            }
            zzH(context, intent.getAction()).zza(intent, goAsync());
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
