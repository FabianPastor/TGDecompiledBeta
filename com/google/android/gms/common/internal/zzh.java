package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.util.zzj;
import com.google.android.gms.internal.zzadg;

public final class zzh {
    private static final SimpleArrayMap<String, String> zzaFE = new SimpleArrayMap();

    @Nullable
    private static String zzC(Context context, String str) {
        synchronized (zzaFE) {
            String str2 = (String) zzaFE.get(str);
            if (str2 != null) {
                return str2;
            }
            Resources remoteResource = GooglePlayServicesUtil.getRemoteResource(context);
            if (remoteResource == null) {
                return null;
            }
            int identifier = remoteResource.getIdentifier(str, "string", "com.google.android.gms");
            if (identifier == 0) {
                String str3 = "GoogleApiAvailability";
                String str4 = "Missing resource: ";
                str2 = String.valueOf(str);
                Log.w(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4));
                return null;
            }
            Object string = remoteResource.getString(identifier);
            if (TextUtils.isEmpty(string)) {
                str3 = "GoogleApiAvailability";
                str4 = "Got empty resource: ";
                str2 = String.valueOf(str);
                Log.w(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4));
                return null;
            }
            zzaFE.put(str, string);
            return string;
        }
    }

    public static String zzaT(Context context) {
        String str = context.getApplicationInfo().name;
        if (TextUtils.isEmpty(str)) {
            str = context.getPackageName();
            try {
                str = zzadg.zzbi(context).zzdA(context.getPackageName()).toString();
            } catch (NameNotFoundException e) {
            }
        }
        return str;
    }

    @Nullable
    public static String zzg(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case 1:
                return resources.getString(R.string.common_google_play_services_install_title);
            case 2:
                return resources.getString(R.string.common_google_play_services_update_title);
            case 3:
                return resources.getString(R.string.common_google_play_services_enable_title);
            case 4:
            case 6:
            case 18:
                return null;
            case 5:
                Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
                return zzC(context, "common_google_play_services_invalid_account_title");
            case 7:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return zzC(context, "common_google_play_services_network_error_title");
            case 8:
                Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
                return null;
            case 9:
                Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
                return null;
            case 10:
                Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
                return null;
            case 11:
                Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
                return null;
            case 16:
                Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
                return null;
            case 17:
                Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
                return zzC(context, "common_google_play_services_sign_in_failed_title");
            case 20:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return zzC(context, "common_google_play_services_restricted_profile_title");
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i);
                return null;
        }
    }

    @NonNull
    public static String zzh(Context context, int i) {
        String zzC = i == 6 ? zzC(context, "common_google_play_services_resolution_required_title") : zzg(context, i);
        return zzC == null ? context.getResources().getString(R.string.common_google_play_services_notification_ticker) : zzC;
    }

    @NonNull
    public static String zzi(Context context, int i) {
        Resources resources = context.getResources();
        String zzaT = zzaT(context);
        switch (i) {
            case 1:
                return resources.getString(R.string.common_google_play_services_install_text, new Object[]{zzaT});
            case 2:
                if (zzj.zzba(context)) {
                    return resources.getString(R.string.common_google_play_services_wear_update_text);
                }
                return resources.getString(R.string.common_google_play_services_update_text, new Object[]{zzaT});
            case 3:
                return resources.getString(R.string.common_google_play_services_enable_text, new Object[]{zzaT});
            case 5:
                return zzo(context, "common_google_play_services_invalid_account_text", zzaT);
            case 7:
                return zzo(context, "common_google_play_services_network_error_text", zzaT);
            case 9:
                return resources.getString(R.string.common_google_play_services_unsupported_text, new Object[]{zzaT});
            case 16:
                return zzo(context, "common_google_play_services_api_unavailable_text", zzaT);
            case 17:
                return zzo(context, "common_google_play_services_sign_in_failed_text", zzaT);
            case 18:
                return resources.getString(R.string.common_google_play_services_updating_text, new Object[]{zzaT});
            case 20:
                return zzo(context, "common_google_play_services_restricted_profile_text", zzaT);
            default:
                return resources.getString(R.string.common_google_play_services_unknown_issue, new Object[]{zzaT});
        }
    }

    @NonNull
    public static String zzj(Context context, int i) {
        return i == 6 ? zzo(context, "common_google_play_services_resolution_required_text", zzaT(context)) : zzi(context, i);
    }

    @NonNull
    public static String zzk(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case 1:
                return resources.getString(R.string.common_google_play_services_install_button);
            case 2:
                return resources.getString(R.string.common_google_play_services_update_button);
            case 3:
                return resources.getString(R.string.common_google_play_services_enable_button);
            default:
                return resources.getString(17039370);
        }
    }

    private static String zzo(Context context, String str, String str2) {
        Resources resources = context.getResources();
        String zzC = zzC(context, str);
        if (zzC == null) {
            zzC = resources.getString(R.string.common_google_play_services_unknown_issue);
        }
        return String.format(resources.getConfiguration().locale, zzC, new Object[]{str2});
    }
}
