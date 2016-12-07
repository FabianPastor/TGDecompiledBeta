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
import org.telegram.messenger.exoplayer.C;

class zza {
    static zza agO;
    private final Context mContext;

    private class zza extends IllegalArgumentException {
    }

    private zza(Context context) {
        this.mContext = context.getApplicationContext();
    }

    private void zza(String str, Notification notification) {
        if (Log.isLoggable("GcmNotification", 3)) {
            Log.d("GcmNotification", "Showing notification");
        }
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (TextUtils.isEmpty(str)) {
            str = "GCM-Notification:" + SystemClock.uptimeMillis();
        }
        notificationManager.notify(str, 0, notification);
    }

    static boolean zzad(Bundle bundle) {
        return "1".equals(zzf(bundle, "gcm.n.e")) || zzf(bundle, "gcm.n.icon") != null;
    }

    static void zzae(Bundle bundle) {
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
                    bundle2.putString(str.substring("gcm.n.".length()), string);
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

    private Notification zzag(Bundle bundle) {
        CharSequence zzg = zzg(bundle, "gcm.n.title");
        CharSequence zzg2 = zzg(bundle, "gcm.n.body");
        int zzkl = zzkl(zzf(bundle, "gcm.n.icon"));
        Object zzf = zzf(bundle, "gcm.n.color");
        Uri zzkm = zzkm(zzf(bundle, "gcm.n.sound2"));
        PendingIntent zzah = zzah(bundle);
        Builder smallIcon = new Builder(this.mContext).setAutoCancel(true).setSmallIcon(zzkl);
        if (TextUtils.isEmpty(zzg)) {
            smallIcon.setContentTitle(this.mContext.getApplicationInfo().loadLabel(this.mContext.getPackageManager()));
        } else {
            smallIcon.setContentTitle(zzg);
        }
        if (!TextUtils.isEmpty(zzg2)) {
            smallIcon.setContentText(zzg2);
        }
        if (!TextUtils.isEmpty(zzf)) {
            smallIcon.setColor(Color.parseColor(zzf));
        }
        if (zzkm != null) {
            smallIcon.setSound(zzkm);
        }
        if (zzah != null) {
            smallIcon.setContentIntent(zzah);
        }
        return smallIcon.build();
    }

    private PendingIntent zzah(Bundle bundle) {
        Intent intent;
        Object zzf = zzf(bundle, "gcm.n.click_action");
        Intent launchIntentForPackage;
        if (TextUtils.isEmpty(zzf)) {
            launchIntentForPackage = this.mContext.getPackageManager().getLaunchIntentForPackage(this.mContext.getPackageName());
            if (launchIntentForPackage == null) {
                Log.w("GcmNotification", "No activity found to launch app");
                return null;
            }
            intent = launchIntentForPackage;
        } else {
            launchIntentForPackage = new Intent(zzf);
            launchIntentForPackage.setPackage(this.mContext.getPackageName());
            launchIntentForPackage.setFlags(268435456);
            intent = launchIntentForPackage;
        }
        Bundle bundle2 = new Bundle(bundle);
        GcmListenerService.zzac(bundle2);
        intent.putExtras(bundle2);
        for (String str : bundle2.keySet()) {
            if (str.startsWith("gcm.n.") || str.startsWith("gcm.notification.")) {
                intent.removeExtra(str);
            }
        }
        return PendingIntent.getActivity(this.mContext, zzbnr(), intent, C.ENCODING_PCM_32BIT);
    }

    private int zzbnr() {
        return (int) SystemClock.uptimeMillis();
    }

    static synchronized zza zzcz(Context context) {
        zza com_google_android_gms_gcm_zza;
        synchronized (zza.class) {
            if (agO == null) {
                agO = new zza(context);
            }
            com_google_android_gms_gcm_zza = agO;
        }
        return com_google_android_gms_gcm_zza;
    }

    static boolean zzda(Context context) {
        if (((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode()) {
            return false;
        }
        int myPid = Process.myPid();
        List<RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return false;
        }
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.pid == myPid) {
                return runningAppProcessInfo.importance == 100;
            }
        }
        return false;
    }

    static String zzf(Bundle bundle, String str) {
        String string = bundle.getString(str);
        return string == null ? bundle.getString(str.replace("gcm.n.", "gcm.notification.")) : string;
    }

    private String zzg(Bundle bundle, String str) {
        Object zzf = zzf(bundle, str);
        if (!TextUtils.isEmpty(zzf)) {
            return zzf;
        }
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("_loc_key");
        valueOf = zzf(bundle, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        if (TextUtils.isEmpty(valueOf)) {
            return null;
        }
        Resources resources = this.mContext.getResources();
        int identifier = resources.getIdentifier(valueOf, "string", this.mContext.getPackageName());
        if (identifier == 0) {
            String str2 = "GcmNotification";
            String valueOf3 = String.valueOf(str);
            valueOf2 = String.valueOf("_loc_key");
            valueOf2 = String.valueOf(zzkk(valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3)));
            Log.w(str2, new StringBuilder((String.valueOf(valueOf2).length() + 49) + String.valueOf(valueOf).length()).append(valueOf2).append(" resource not found: ").append(valueOf).append(" Default value will be used.").toString());
            return null;
        }
        String valueOf4 = String.valueOf(str);
        valueOf2 = String.valueOf("_loc_args");
        valueOf4 = zzf(bundle, valueOf2.length() != 0 ? valueOf4.concat(valueOf2) : new String(valueOf4));
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
            valueOf2 = String.valueOf(zzkk(valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2)));
            Log.w(valueOf, new StringBuilder((String.valueOf(valueOf2).length() + 41) + String.valueOf(valueOf4).length()).append("Malformed ").append(valueOf2).append(": ").append(valueOf4).append("  Default value will be used.").toString());
            return null;
        } catch (Throwable e2) {
            Log.w("GcmNotification", new StringBuilder((String.valueOf(valueOf).length() + 58) + String.valueOf(valueOf4).length()).append("Missing format argument for ").append(valueOf).append(": ").append(valueOf4).append(" Default value will be used.").toString(), e2);
            return null;
        }
    }

    private String zzkk(String str) {
        return str.substring("gcm.n.".length());
    }

    private int zzkl(String str) {
        int identifier;
        if (!TextUtils.isEmpty(str)) {
            Resources resources = this.mContext.getResources();
            identifier = resources.getIdentifier(str, "drawable", this.mContext.getPackageName());
            if (identifier != 0) {
                return identifier;
            }
            identifier = resources.getIdentifier(str, "mipmap", this.mContext.getPackageName());
            if (identifier != 0) {
                return identifier;
            }
            Log.w("GcmNotification", new StringBuilder(String.valueOf(str).length() + 57).append("Icon resource ").append(str).append(" not found. Notification will use app icon.").toString());
        }
        identifier = this.mContext.getApplicationInfo().icon;
        return identifier == 0 ? 17301651 : identifier;
    }

    private Uri zzkm(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if ("default".equals(str) || this.mContext.getResources().getIdentifier(str, "raw", this.mContext.getPackageName()) == 0) {
            return RingtoneManager.getDefaultUri(2);
        }
        String valueOf = String.valueOf("android.resource://");
        String valueOf2 = String.valueOf(this.mContext.getPackageName());
        return Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 5) + String.valueOf(valueOf2).length()) + String.valueOf(str).length()).append(valueOf).append(valueOf2).append("/raw/").append(str).toString());
    }

    boolean zzaf(Bundle bundle) {
        try {
            zza(zzf(bundle, "gcm.n.tag"), zzag(bundle));
            return true;
        } catch (zza e) {
            String str = "GcmNotification";
            String str2 = "Failed to show notification: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            return false;
        }
    }
}
