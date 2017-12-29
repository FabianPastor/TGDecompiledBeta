package com.google.android.gms.common;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller.SessionInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.google.android.gms.R;
import com.google.android.gms.common.internal.zzbf;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.internal.zzbhf;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.MessagesController;

public class zzp {
    @Deprecated
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = 11910000;
    private static boolean zzflj = false;
    private static boolean zzflk = false;
    private static boolean zzfll = false;
    private static boolean zzflm = false;
    static final AtomicBoolean zzfln = new AtomicBoolean();
    private static final AtomicBoolean zzflo = new AtomicBoolean();

    @Deprecated
    public static String getErrorString(int i) {
        return ConnectionResult.getStatusString(i);
    }

    public static Context getRemoteContext(Context context) {
        try {
            return context.createPackageContext("com.google.android.gms", 3);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static Resources getRemoteResource(Context context) {
        try {
            return context.getPackageManager().getResourcesForApplication("com.google.android.gms");
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    @Deprecated
    public static int isGooglePlayServicesAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            context.getResources().getString(R.string.common_google_play_services_unknown_issue);
        } catch (Throwable th) {
            Log.e("GooglePlayServicesUtil", "The Google Play services resources were not found. Check your project configuration to ensure that the resources are included.");
        }
        if (!("com.google.android.gms".equals(context.getPackageName()) || zzflo.get())) {
            int zzcq = zzbf.zzcq(context);
            if (zzcq == 0) {
                throw new IllegalStateException("A required meta-data tag in your app's AndroidManifest.xml does not exist.  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
            } else if (zzcq != GOOGLE_PLAY_SERVICES_VERSION_CODE) {
                String str = "com.google.android.gms.version";
                throw new IllegalStateException(new StringBuilder(String.valueOf(str).length() + 290).append("The meta-data tag in your app's AndroidManifest.xml does not have the right value.  Expected ").append(GOOGLE_PLAY_SERVICES_VERSION_CODE).append(" but found ").append(zzcq).append(".  You must have the following declaration within the <application> element:     <meta-data android:name=\"").append(str).append("\" android:value=\"@integer/google_play_services_version\" />").toString());
            }
        }
        int i = (zzi.zzct(context) || zzi.zzcv(context)) ? 0 : 1;
        PackageInfo packageInfo = null;
        if (i != 0) {
            try {
                packageInfo = packageManager.getPackageInfo("com.android.vending", 8256);
            } catch (NameNotFoundException e) {
                Log.w("GooglePlayServicesUtil", "Google Play Store is missing.");
                return 9;
            }
        }
        try {
            PackageInfo packageInfo2 = packageManager.getPackageInfo("com.google.android.gms", 64);
            zzq.zzci(context);
            if (i != 0) {
                if (zzq.zza(packageInfo, zzk.zzflf) == null) {
                    Log.w("GooglePlayServicesUtil", "Google Play Store signature invalid.");
                    return 9;
                }
                if (zzq.zza(packageInfo2, zzq.zza(packageInfo, zzk.zzflf)) == null) {
                    Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
                    return 9;
                }
            } else if (zzq.zza(packageInfo2, zzk.zzflf) == null) {
                Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
                return 9;
            }
            if (packageInfo2.versionCode / 1000 < GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000) {
                Log.w("GooglePlayServicesUtil", "Google Play services out of date.  Requires " + GOOGLE_PLAY_SERVICES_VERSION_CODE + " but found " + packageInfo2.versionCode);
                return 2;
            }
            ApplicationInfo applicationInfo = packageInfo2.applicationInfo;
            if (applicationInfo == null) {
                try {
                    applicationInfo = packageManager.getApplicationInfo("com.google.android.gms", 0);
                } catch (Throwable e2) {
                    Log.wtf("GooglePlayServicesUtil", "Google Play services missing when getting application info.", e2);
                    return 1;
                }
            }
            return !applicationInfo.enabled ? 3 : 0;
        } catch (NameNotFoundException e3) {
            Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
            return 1;
        }
    }

    @Deprecated
    public static boolean isUserRecoverableError(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 9:
                return true;
            default:
                return false;
        }
    }

    @TargetApi(19)
    @Deprecated
    public static boolean zzb(Context context, int i, String str) {
        return zzx.zzb(context, i, str);
    }

    @Deprecated
    public static void zzce(Context context) {
        if (!zzfln.getAndSet(true)) {
            try {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
                if (notificationManager != null) {
                    notificationManager.cancel(10436);
                }
            } catch (SecurityException e) {
            }
        }
    }

    @Deprecated
    public static int zzcf(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
            return i;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean zzch(Context context) {
        if (!zzflm) {
            try {
                PackageInfo packageInfo = zzbhf.zzdb(context).getPackageInfo("com.google.android.gms", 64);
                if (packageInfo != null) {
                    zzq.zzci(context);
                    if (zzq.zza(packageInfo, zzk.zzflf[1]) != null) {
                        zzfll = true;
                        zzflm = true;
                    }
                }
                zzfll = false;
                zzflm = true;
            } catch (Throwable e) {
                Log.w("GooglePlayServicesUtil", "Cannot find Google Play services package name.", e);
            } catch (Throwable th) {
                zzflm = true;
            }
        }
        return zzfll || !"user".equals(Build.TYPE);
    }

    @Deprecated
    public static boolean zze(Context context, int i) {
        return i == 18 ? true : i == 1 ? zzv(context, "com.google.android.gms") : false;
    }

    @TargetApi(21)
    static boolean zzv(Context context, String str) {
        boolean equals = str.equals("com.google.android.gms");
        if (zzq.zzamn()) {
            try {
                for (SessionInfo appPackageName : context.getPackageManager().getPackageInstaller().getAllSessions()) {
                    if (str.equals(appPackageName.getAppPackageName())) {
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, MessagesController.UPDATE_MASK_CHANNEL);
            if (equals) {
                return applicationInfo.enabled;
            }
            if (applicationInfo.enabled) {
                Object obj;
                if (zzq.zzamk()) {
                    Bundle applicationRestrictions = ((UserManager) context.getSystemService("user")).getApplicationRestrictions(context.getPackageName());
                    if (applicationRestrictions != null && "true".equals(applicationRestrictions.getString("restricted_profile"))) {
                        obj = 1;
                        if (obj == null) {
                            return true;
                        }
                    }
                }
                obj = null;
                if (obj == null) {
                    return true;
                }
            }
            return false;
        } catch (NameNotFoundException e2) {
            return false;
        }
    }
}
