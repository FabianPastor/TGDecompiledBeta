package com.google.android.gms.gcm;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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
import com.google.android.gms.R;
import com.google.android.gms.common.util.zzq;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;

final class zza {
    static zza zzibw;
    private final Context mContext;
    private String zzibx;
    private final AtomicInteger zziby = new AtomicInteger((int) SystemClock.elapsedRealtime());

    private zza(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private final Bundle zzauu() {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 128);
        } catch (NameNotFoundException e) {
        }
        return (applicationInfo == null || applicationInfo.metaData == null) ? Bundle.EMPTY : applicationInfo.metaData;
    }

    static synchronized zza zzdj(Context context) {
        zza com_google_android_gms_gcm_zza;
        synchronized (zza.class) {
            if (zzibw == null) {
                zzibw = new zza(context);
            }
            com_google_android_gms_gcm_zza = zzibw;
        }
        return com_google_android_gms_gcm_zza;
    }

    static boolean zzdk(Context context) {
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
            valueOf2 = (valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3)).substring(6);
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
            valueOf2 = (valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2)).substring(6);
            Log.w(valueOf, new StringBuilder((String.valueOf(valueOf2).length() + 41) + String.valueOf(valueOf4).length()).append("Malformed ").append(valueOf2).append(": ").append(valueOf4).append("  Default value will be used.").toString());
            return null;
        } catch (Throwable e2) {
            Log.w("GcmNotification", new StringBuilder((String.valueOf(valueOf).length() + 58) + String.valueOf(valueOf4).length()).append("Missing format argument for ").append(valueOf).append(": ").append(valueOf4).append(" Default value will be used.").toString(), e2);
            return null;
        }
    }

    static void zzr(Bundle bundle) {
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

    private final PendingIntent zzt(Bundle bundle) {
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
        GcmListenerService.zzq(bundle2);
        intent.putExtras(bundle2);
        for (String str : bundle2.keySet()) {
            if (str.startsWith("gcm.n.") || str.startsWith("gcm.notification.")) {
                intent.removeExtra(str);
            }
        }
        return PendingIntent.getActivity(this.mContext, this.zziby.getAndIncrement(), intent, NUM);
    }

    final boolean zzs(Bundle bundle) {
        int identifier;
        int i;
        Object zze;
        String zze2;
        Uri uri;
        PendingIntent zzt;
        Builder smallIcon;
        Notification build;
        NotificationManager notificationManager;
        String str = null;
        CharSequence zzf = zzf(bundle, "gcm.n.title");
        CharSequence loadLabel = TextUtils.isEmpty(zzf) ? this.mContext.getApplicationInfo().loadLabel(this.mContext.getPackageManager()) : zzf;
        CharSequence zzf2 = zzf(bundle, "gcm.n.body");
        String zze3 = zze(bundle, "gcm.n.icon");
        if (!TextUtils.isEmpty(zze3)) {
            String packageName;
            Resources resources = this.mContext.getResources();
            identifier = resources.getIdentifier(zze3, "drawable", this.mContext.getPackageName());
            if (identifier != 0) {
                i = identifier;
            } else {
                identifier = resources.getIdentifier(zze3, "mipmap", this.mContext.getPackageName());
                if (identifier != 0) {
                    i = identifier;
                } else {
                    Log.w("GcmNotification", new StringBuilder(String.valueOf(zze3).length() + 57).append("Icon resource ").append(zze3).append(" not found. Notification will use app icon.").toString());
                }
            }
            zze = zze(bundle, "gcm.n.color");
            zze2 = zze(bundle, "gcm.n.sound2");
            if (TextUtils.isEmpty(zze2)) {
                uri = null;
            } else if (!"default".equals(zze2) || this.mContext.getResources().getIdentifier(zze2, "raw", this.mContext.getPackageName()) == 0) {
                uri = RingtoneManager.getDefaultUri(2);
            } else {
                String str2 = "android.resource://";
                packageName = this.mContext.getPackageName();
                uri = Uri.parse(new StringBuilder(((String.valueOf(str2).length() + 5) + String.valueOf(packageName).length()) + String.valueOf(zze2).length()).append(str2).append(packageName).append("/raw/").append(zze2).toString());
            }
            zzt = zzt(bundle);
            if (zzq.isAtLeastO() || this.mContext.getApplicationInfo().targetSdkVersion <= 25) {
                smallIcon = new Builder(this.mContext).setAutoCancel(true).setSmallIcon(i);
                if (!TextUtils.isEmpty(loadLabel)) {
                    smallIcon.setContentTitle(loadLabel);
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
                if (zzt != null) {
                    smallIcon.setContentIntent(zzt);
                }
                build = smallIcon.build();
            } else {
                packageName = zze(bundle, "gcm.n.android_channel_id");
                if (zzq.isAtLeastO()) {
                    notificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
                    if (!TextUtils.isEmpty(packageName)) {
                        if (notificationManager.getNotificationChannel(packageName) != null) {
                            str = packageName;
                        } else {
                            Log.w("GcmNotification", new StringBuilder(String.valueOf(packageName).length() + 122).append("Notification Channel requested (").append(packageName).append(") has not been created by the app. Manifest configuration, or default, value will be used.").toString());
                        }
                    }
                    if (this.zzibx != null) {
                        str = this.zzibx;
                    } else {
                        this.zzibx = zzauu().getString("com.google.android.gms.gcm.default_notification_channel_id");
                        if (TextUtils.isEmpty(this.zzibx)) {
                            Log.w("GcmNotification", "Missing Default Notification Channel metadata in AndroidManifest. Default value will be used.");
                        } else if (notificationManager.getNotificationChannel(this.zzibx) != null) {
                            str = this.zzibx;
                        } else {
                            Log.w("GcmNotification", "Notification Channel set in AndroidManifest.xml has not been created by the app. Default value will be used.");
                        }
                        if (notificationManager.getNotificationChannel("fcm_fallback_notification_channel") == null) {
                            notificationManager.createNotificationChannel(new NotificationChannel("fcm_fallback_notification_channel", this.mContext.getString(R.string.gcm_fallback_notification_channel_label), 3));
                        }
                        this.zzibx = "fcm_fallback_notification_channel";
                        str = this.zzibx;
                    }
                }
                Notification.Builder smallIcon2 = new Notification.Builder(this.mContext).setAutoCancel(true).setSmallIcon(i);
                if (!TextUtils.isEmpty(loadLabel)) {
                    smallIcon2.setContentTitle(loadLabel);
                }
                if (!TextUtils.isEmpty(zzf2)) {
                    smallIcon2.setContentText(zzf2);
                    smallIcon2.setStyle(new BigTextStyle().bigText(zzf2));
                }
                if (!TextUtils.isEmpty(zze)) {
                    smallIcon2.setColor(Color.parseColor(zze));
                }
                if (uri != null) {
                    smallIcon2.setSound(uri);
                }
                if (zzt != null) {
                    smallIcon2.setContentIntent(zzt);
                }
                if (str != null) {
                    smallIcon2.setChannelId(str);
                }
                build = smallIcon2.build();
            }
            zze3 = zze(bundle, "gcm.n.tag");
            if (Log.isLoggable("GcmNotification", 3)) {
                Log.d("GcmNotification", "Showing notification");
            }
            notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
            if (TextUtils.isEmpty(zze3)) {
                zze3 = "GCM-Notification:" + SystemClock.uptimeMillis();
            }
            notificationManager.notify(zze3, 0, build);
            return true;
        }
        identifier = this.mContext.getApplicationInfo().icon;
        if (identifier == 0) {
            identifier = 17301651;
        }
        i = identifier;
        zze = zze(bundle, "gcm.n.color");
        zze2 = zze(bundle, "gcm.n.sound2");
        if (TextUtils.isEmpty(zze2)) {
            uri = null;
        } else {
            if ("default".equals(zze2)) {
            }
            uri = RingtoneManager.getDefaultUri(2);
        }
        zzt = zzt(bundle);
        if (zzq.isAtLeastO()) {
        }
        smallIcon = new Builder(this.mContext).setAutoCancel(true).setSmallIcon(i);
        if (TextUtils.isEmpty(loadLabel)) {
            smallIcon.setContentTitle(loadLabel);
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
        if (zzt != null) {
            smallIcon.setContentIntent(zzt);
        }
        build = smallIcon.build();
        zze3 = zze(bundle, "gcm.n.tag");
        if (Log.isLoggable("GcmNotification", 3)) {
            Log.d("GcmNotification", "Showing notification");
        }
        notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (TextUtils.isEmpty(zze3)) {
            zze3 = "GCM-Notification:" + SystemClock.uptimeMillis();
        }
        notificationManager.notify(zze3, 0, build);
        return true;
    }
}
