package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.internal.zzbhf;

public final class zzu {
    private static final SimpleArrayMap<String, String> zzfzl = new SimpleArrayMap();

    private static String zzcm(Context context) {
        CharSequence charSequence;
        String packageName = context.getPackageName();
        try {
            return zzbhf.zzdb(context).zzgt(packageName).toString();
        } catch (NameNotFoundException e) {
            charSequence = context.getApplicationInfo().name;
            return TextUtils.isEmpty(charSequence) ? charSequence : packageName;
        } catch (NullPointerException e2) {
            charSequence = context.getApplicationInfo().name;
            if (TextUtils.isEmpty(charSequence)) {
            }
        }
    }

    public static String zzcn(Context context) {
        return context.getResources().getString(R.string.common_google_play_services_notification_channel_name);
    }

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
                return zzw(context, "common_google_play_services_invalid_account_title");
            case 7:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return zzw(context, "common_google_play_services_network_error_title");
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
                return zzw(context, "common_google_play_services_sign_in_failed_title");
            case 20:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return zzw(context, "common_google_play_services_restricted_profile_title");
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i);
                return null;
        }
    }

    public static String zzh(Context context, int i) {
        String zzw = i == 6 ? zzw(context, "common_google_play_services_resolution_required_title") : zzg(context, i);
        return zzw == null ? context.getResources().getString(R.string.common_google_play_services_notification_ticker) : zzw;
    }

    public static String zzi(Context context, int i) {
        Resources resources = context.getResources();
        String zzcm = zzcm(context);
        switch (i) {
            case 1:
                return resources.getString(R.string.common_google_play_services_install_text, new Object[]{zzcm});
            case 2:
                if (zzi.zzct(context)) {
                    return resources.getString(R.string.common_google_play_services_wear_update_text);
                }
                return resources.getString(R.string.common_google_play_services_update_text, new Object[]{zzcm});
            case 3:
                return resources.getString(R.string.common_google_play_services_enable_text, new Object[]{zzcm});
            case 5:
                return zzl(context, "common_google_play_services_invalid_account_text", zzcm);
            case 7:
                return zzl(context, "common_google_play_services_network_error_text", zzcm);
            case 9:
                return resources.getString(R.string.common_google_play_services_unsupported_text, new Object[]{zzcm});
            case 16:
                return zzl(context, "common_google_play_services_api_unavailable_text", zzcm);
            case 17:
                return zzl(context, "common_google_play_services_sign_in_failed_text", zzcm);
            case 18:
                return resources.getString(R.string.common_google_play_services_updating_text, new Object[]{zzcm});
            case 20:
                return zzl(context, "common_google_play_services_restricted_profile_text", zzcm);
            default:
                return resources.getString(R.string.common_google_play_services_unknown_issue, new Object[]{zzcm});
        }
    }

    public static String zzj(Context context, int i) {
        return i == 6 ? zzl(context, "common_google_play_services_resolution_required_text", zzcm(context)) : zzi(context, i);
    }

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

    private static String zzl(Context context, String str, String str2) {
        Resources resources = context.getResources();
        String zzw = zzw(context, str);
        if (zzw == null) {
            zzw = resources.getString(R.string.common_google_play_services_unknown_issue);
        }
        return String.format(resources.getConfiguration().locale, zzw, new Object[]{str2});
    }

    private static String zzw(Context context, String str) {
        synchronized (zzfzl) {
            String str2 = (String) zzfzl.get(str);
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
            zzfzl.put(str, string);
            return string;
        }
    }
}
