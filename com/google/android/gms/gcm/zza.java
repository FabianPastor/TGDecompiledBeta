package com.google.android.gms.gcm;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

final class zza {
    static zza zzbfw;
    private final Context mContext;

    private zza(Context context) {
        this.mContext = context.getApplicationContext();
    }

    static synchronized zza zzaX(Context context) {
        zza com_google_android_gms_gcm_zza;
        synchronized (zza.class) {
            if (zzbfw == null) {
                zzbfw = new zza(context);
            }
            com_google_android_gms_gcm_zza = zzbfw;
        }
        return com_google_android_gms_gcm_zza;
    }

    static boolean zzaY(Context context) {
        if (((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            return false;
        }
        int myPid = Process.myPid();
        List<RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (runningAppProcessInfo.pid == myPid) {
                    return runningAppProcessInfo.importance == 100;
                }
            }
        }
        return false;
    }

    static String zze(Bundle bundle, String str) {
        String string = bundle.getString(str);
        return string == null ? bundle.getString(str.replace("gcm.n.", "gcm.notification.")) : string;
    }

    private final String zzf(Bundle bundle, String str) {
        Object zze = zze(bundle, str);
        if (!TextUtils.isEmpty(zze)) {
            return zze;
        }
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("_loc_key");
        valueOf = zze(bundle, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        if (TextUtils.isEmpty(valueOf)) {
            return null;
        }
        Resources resources = this.mContext.getResources();
        int identifier = resources.getIdentifier(valueOf, "string", this.mContext.getPackageName());
        if (identifier == 0) {
            String str2 = "GcmNotification";
            String valueOf3 = String.valueOf(str);
            valueOf2 = String.valueOf("_loc_key");
            valueOf2 = String.valueOf((valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3)).substring(6));
            Log.w(str2, new StringBuilder((String.valueOf(valueOf2).length() + 49) + String.valueOf(valueOf).length()).append(valueOf2).append(" resource not found: ").append(valueOf).append(" Default value will be used.").toString());
            return null;
        }
        String valueOf4 = String.valueOf(str);
        valueOf2 = String.valueOf("_loc_args");
        valueOf4 = zze(bundle, valueOf2.length() != 0 ? valueOf4.concat(valueOf2) : new String(valueOf4));
        if (TextUtils.isEmpty(valueOf4)) {
            return resources.getString(identifier);
        }
        try {
            JSONArray jSONArray = new JSONArray(valueOf4);
            String[] strArr = new String[jSONArray.length()];
            for (int i = 0; i < strArr.length; i++) {
                strArr[i] = jSONArray.opt(i);
            }
            return resources.getString(identifier, strArr);
        } catch (JSONException e) {
            valueOf = "GcmNotification";
            str2 = String.valueOf(str);
            valueOf2 = String.valueOf("_loc_args");
            valueOf2 = String.valueOf((valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2)).substring(6));
            Log.w(valueOf, new StringBuilder((String.valueOf(valueOf2).length() + 41) + String.valueOf(valueOf4).length()).append("Malformed ").append(valueOf2).append(": ").append(valueOf4).append("  Default value will be used.").toString());
            return null;
        } catch (Throwable e2) {
            Log.w("GcmNotification", new StringBuilder((String.valueOf(valueOf).length() + 58) + String.valueOf(valueOf4).length()).append("Missing format argument for ").append(valueOf).append(": ").append(valueOf4).append(" Default value will be used.").toString(), e2);
            return null;
        }
    }

    static void zzu(Bundle bundle) {
        String str;
        Bundle bundle2 = new Bundle();
        Iterator it = bundle.keySet().iterator();
        while (it.hasNext()) {
            str = (String) it.next();
            String string = bundle.getString(str);
            if (str.startsWith("gcm.notification.")) {
                str = str.replace("gcm.notification.", "gcm.n.");
            }
            if (str.startsWith("gcm.n.")) {
                if (!"gcm.n.e".equals(str)) {
                    bundle2.putString(str.substring(6), string);
                }
                it.remove();
            }
        }
        str = bundle2.getString("sound2");
        if (str != null) {
            bundle2.remove("sound2");
            bundle2.putString("sound", str);
        }
        if (!bundle2.isEmpty()) {
            bundle.putBundle("notification", bundle2);
        }
    }

    private final PendingIntent zzw(Bundle bundle) {
        Intent intent;
        Object zze = zze(bundle, "gcm.n.click_action");
        Intent launchIntentForPackage;
        if (TextUtils.isEmpty(zze)) {
            launchIntentForPackage = this.mContext.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName());
            if (launchIntentForPackage == null) {
                Log.w("GcmNotification", "No activity found to launch app");
                return null;
            }
            intent = launchIntentForPackage;
        } else {
            launchIntentForPackage = new Intent(zze);
            launchIntentForPackage.setPackage(this.mContext.getPackageName());
            launchIntentForPackage.setFlags(268435456);
            intent = launchIntentForPackage;
        }
        Bundle bundle2 = new Bundle(bundle);
        GcmListenerService.zzt(bundle2);
        intent.putExtras(bundle2);
        for (String str : bundle2.keySet()) {
            if (str.startsWith("gcm.n.") || str.startsWith("gcm.notification.")) {
                intent.removeExtra(str);
            }
        }
        return PendingIntent.getActivity(this.mContext, (int) SystemClock.uptimeMillis(), intent, NUM);
    }

    final boolean zzv(Bundle bundle) {
        int identifier;
        Object zze;
        Uri uri;
        PendingIntent zzw;
        Builder smallIcon;
        Notification build;
        NotificationManager notificationManager;
        CharSequence zzf = zzf(bundle, "gcm.n.title");
        CharSequence zzf2 = zzf(bundle, "gcm.n.body");
        String zze2 = zze(bundle, "gcm.n.icon");
        if (!TextUtils.isEmpty(zze2)) {
            Resources resources = this.mContext.getResources();
            identifier = resources.getIdentifier(zze2, "drawable", this.mContext.getPackageName());
            if (identifier == 0) {
                identifier = resources.getIdentifier(zze2, "mipmap", this.mContext.getPackageName());
                if (identifier == 0) {
                    Log.w("GcmNotification", new StringBuilder(String.valueOf(zze2).length() + 57).append("Icon resource ").append(zze2).append(" not found. Notification will use app icon.").toString());
                }
            }
            zze = zze(bundle, "gcm.n.color");
            zze2 = zze(bundle, "gcm.n.sound2");
            if (TextUtils.isEmpty(zze2)) {
                uri = null;
            } else if (!"default".equals(zze2) || this.mContext.getResources().getIdentifier(zze2, "raw", this.mContext.getPackageName()) == 0) {
                uri = RingtoneManager.getDefaultUri(2);
            } else {
                String valueOf = String.valueOf("android.resource://");
                String valueOf2 = String.valueOf(this.mContext.getPackageName());
                uri = Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 5) + String.valueOf(valueOf2).length()) + String.valueOf(zze2).length()).append(valueOf).append(valueOf2).append("/raw/").append(zze2).toString());
            }
            zzw = zzw(bundle);
            smallIcon = new Builder(this.mContext).setAutoCancel(true).setSmallIcon(identifier);
            if (TextUtils.isEmpty(zzf)) {
                smallIcon.setContentTitle(zzf);
            } else {
                smallIcon.setContentTitle(this.mContext.getApplicationInfo().loadLabel(this.mContext.getPackageManager()));
            }
            if (!TextUtils.isEmpty(zzf2)) {
                smallIcon.setContentText(zzf2);
            }
            if (!TextUtils.isEmpty(zze)) {
                smallIcon.setColor(Color.parseColor(zze));
            }
            if (uri != null) {
                smallIcon.setSound(uri);
            }
            if (zzw != null) {
                smallIcon.setContentIntent(zzw);
            }
            build = smallIcon.build();
            zze2 = zze(bundle, "gcm.n.tag");
            if (Log.isLoggable("GcmNotification", 3)) {
                Log.d("GcmNotification", "Showing notification");
            }
            notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
            if (TextUtils.isEmpty(zze2)) {
                zze2 = "GCM-Notification:" + SystemClock.uptimeMillis();
            }
            notificationManager.notify(zze2, 0, build);
            return true;
        }
        identifier = this.mContext.getApplicationInfo().icon;
        if (identifier == 0) {
            identifier = 17301651;
        }
        zze = zze(bundle, "gcm.n.color");
        zze2 = zze(bundle, "gcm.n.sound2");
        if (TextUtils.isEmpty(zze2)) {
            uri = null;
        } else {
            if ("default".equals(zze2)) {
            }
            uri = RingtoneManager.getDefaultUri(2);
        }
        zzw = zzw(bundle);
        smallIcon = new Builder(this.mContext).setAutoCancel(true).setSmallIcon(identifier);
        if (TextUtils.isEmpty(zzf)) {
            smallIcon.setContentTitle(this.mContext.getApplicationInfo().loadLabel(this.mContext.getPackageManager()));
        } else {
            smallIcon.setContentTitle(zzf);
        }
        if (TextUtils.isEmpty(zzf2)) {
            smallIcon.setContentText(zzf2);
        }
        if (TextUtils.isEmpty(zze)) {
            smallIcon.setColor(Color.parseColor(zze));
        }
        if (uri != null) {
            smallIcon.setSound(uri);
        }
        if (zzw != null) {
            smallIcon.setContentIntent(zzw);
        }
        build = smallIcon.build();
        zze2 = zze(bundle, "gcm.n.tag");
        if (Log.isLoggable("GcmNotification", 3)) {
            Log.d("GcmNotification", "Showing notification");
        }
        notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (TextUtils.isEmpty(zze2)) {
            zze2 = "GCM-Notification:" + SystemClock.uptimeMillis();
        }
        notificationManager.notify(zze2, 0, build);
        return true;
    }
}
