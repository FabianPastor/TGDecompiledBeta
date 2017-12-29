package com.google.firebase.iid;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.Queue;

public final class zzx {
    private static zzx zznzp;
    private final SimpleArrayMap<String, String> zznzq = new SimpleArrayMap();
    private Boolean zznzr = null;
    final Queue<Intent> zznzs = new ArrayDeque();
    private Queue<Intent> zznzt = new ArrayDeque();

    private zzx() {
    }

    public static synchronized zzx zzcjk() {
        zzx com_google_firebase_iid_zzx;
        synchronized (zzx.class) {
            if (zznzp == null) {
                zznzp = new zzx();
            }
            com_google_firebase_iid_zzx = zznzp;
        }
        return com_google_firebase_iid_zzx;
    }

    private final int zze(Context context, Intent intent) {
        String str;
        ComponentName startWakefulService;
        synchronized (this.zznzq) {
            str = (String) this.zznzq.get(intent.getAction());
        }
        if (str == null) {
            ResolveInfo resolveService = context.getPackageManager().resolveService(intent, 0);
            if (resolveService == null || resolveService.serviceInfo == null) {
                Log.e("FirebaseInstanceId", "Failed to resolve target intent service, skipping classname enforcement");
                if (this.zznzr == null) {
                    this.zznzr = Boolean.valueOf(context.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0);
                }
                if (this.zznzr.booleanValue()) {
                    startWakefulService = WakefulBroadcastReceiver.startWakefulService(context, intent);
                } else {
                    startWakefulService = context.startService(intent);
                    Log.d("FirebaseInstanceId", "Missing wake lock permission, service start may be delayed");
                }
                if (startWakefulService == null) {
                    return -1;
                }
                Log.e("FirebaseInstanceId", "Error while delivering the message: ServiceIntent not found.");
                return 404;
            }
            ServiceInfo serviceInfo = resolveService.serviceInfo;
            if (!context.getPackageName().equals(serviceInfo.packageName) || serviceInfo.name == null) {
                String str2 = serviceInfo.packageName;
                str = serviceInfo.name;
                Log.e("FirebaseInstanceId", new StringBuilder((String.valueOf(str2).length() + 94) + String.valueOf(str).length()).append("Error resolving target intent service, skipping classname enforcement. Resolved service was: ").append(str2).append("/").append(str).toString());
                if (this.zznzr == null) {
                    if (context.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0) {
                    }
                    this.zznzr = Boolean.valueOf(context.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0);
                }
                if (this.zznzr.booleanValue()) {
                    startWakefulService = context.startService(intent);
                    Log.d("FirebaseInstanceId", "Missing wake lock permission, service start may be delayed");
                } else {
                    startWakefulService = WakefulBroadcastReceiver.startWakefulService(context, intent);
                }
                if (startWakefulService == null) {
                    return -1;
                }
                Log.e("FirebaseInstanceId", "Error while delivering the message: ServiceIntent not found.");
                return 404;
            }
            str = serviceInfo.name;
            if (str.startsWith(".")) {
                String valueOf = String.valueOf(context.getPackageName());
                str = String.valueOf(str);
                str = str.length() != 0 ? valueOf.concat(str) : new String(valueOf);
            }
            synchronized (this.zznzq) {
                this.zznzq.put(intent.getAction(), str);
            }
        }
        if (Log.isLoggable("FirebaseInstanceId", 3)) {
            str2 = "FirebaseInstanceId";
            String str3 = "Restricting intent to a specific service: ";
            valueOf = String.valueOf(str);
            Log.d(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        }
        intent.setClassName(context.getPackageName(), str);
        try {
            if (this.zznzr == null) {
                if (context.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0) {
                }
                this.zznzr = Boolean.valueOf(context.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") == 0);
            }
            if (this.zznzr.booleanValue()) {
                startWakefulService = WakefulBroadcastReceiver.startWakefulService(context, intent);
            } else {
                startWakefulService = context.startService(intent);
                Log.d("FirebaseInstanceId", "Missing wake lock permission, service start may be delayed");
            }
            if (startWakefulService == null) {
                return -1;
            }
            Log.e("FirebaseInstanceId", "Error while delivering the message: ServiceIntent not found.");
            return 404;
        } catch (Throwable e) {
            Log.e("FirebaseInstanceId", "Error while delivering the message to the serviceIntent", e);
            return 401;
        } catch (IllegalStateException e2) {
            str = String.valueOf(e2);
            Log.e("FirebaseInstanceId", new StringBuilder(String.valueOf(str).length() + 45).append("Failed to start service while in background: ").append(str).toString());
            return 402;
        }
    }

    public final int zza(Context context, String str, Intent intent) {
        Object obj = -1;
        switch (str.hashCode()) {
            case -842411455:
                if (str.equals("com.google.firebase.INSTANCE_ID_EVENT")) {
                    obj = null;
                    break;
                }
                break;
            case 41532704:
                if (str.equals("com.google.firebase.MESSAGING_EVENT")) {
                    obj = 1;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                this.zznzs.offer(intent);
                break;
            case 1:
                this.zznzt.offer(intent);
                break;
            default:
                String str2 = "FirebaseInstanceId";
                String str3 = "Unknown service action: ";
                String valueOf = String.valueOf(str);
                Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                return 500;
        }
        Intent intent2 = new Intent(str);
        intent2.setPackage(context.getPackageName());
        return zze(context, intent2);
    }
}
