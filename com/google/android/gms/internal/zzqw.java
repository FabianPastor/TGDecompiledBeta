package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.gms.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzaj;

@Deprecated
public final class zzqw {
    private static zzqw yP;
    private static Object zzaok = new Object();
    private final String yQ;
    private final Status yR;
    private final String yS;
    private final String yT;
    private final String yU;
    private final boolean yV;
    private final boolean yW;
    private final String zzcpe;

    zzqw(Context context) {
        boolean z = true;
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("google_app_measurement_enable", "integer", resources.getResourcePackageName(R.string.common_google_play_services_unknown_issue));
        if (identifier != 0) {
            boolean z2 = resources.getInteger(identifier) != 0;
            if (z2) {
                z = false;
            }
            this.yW = z;
            z = z2;
        } else {
            this.yW = false;
        }
        this.yV = z;
        zzaj com_google_android_gms_common_internal_zzaj = new zzaj(context);
        this.yS = com_google_android_gms_common_internal_zzaj.getString("firebase_database_url");
        this.yU = com_google_android_gms_common_internal_zzaj.getString("google_storage_bucket");
        this.yT = com_google_android_gms_common_internal_zzaj.getString("gcm_defaultSenderId");
        this.yQ = com_google_android_gms_common_internal_zzaj.getString("google_api_key");
        Object zzcg = zzaa.zzcg(context);
        if (zzcg == null) {
            zzcg = com_google_android_gms_common_internal_zzaj.getString("google_app_id");
        }
        if (TextUtils.isEmpty(zzcg)) {
            this.yR = new Status(10, "Missing google app id value from from string resources with name google_app_id.");
            this.zzcpe = null;
            return;
        }
        this.zzcpe = zzcg;
        this.yR = Status.vY;
    }

    zzqw(String str, boolean z) {
        this(str, z, null, null, null);
    }

    zzqw(String str, boolean z, String str2, String str3, String str4) {
        this.zzcpe = str;
        this.yQ = null;
        this.yR = Status.vY;
        this.yV = z;
        this.yW = !z;
        this.yS = str2;
        this.yT = str4;
        this.yU = str3;
    }

    public static String zzasl() {
        return zzhf("getGoogleAppId").zzcpe;
    }

    public static boolean zzasm() {
        return zzhf("isMeasurementExplicitlyDisabled").yW;
    }

    public static Status zzb(Context context, String str, boolean z) {
        Status zzhe;
        zzac.zzb((Object) context, (Object) "Context must not be null.");
        zzac.zzh(str, "App ID must be nonempty.");
        synchronized (zzaok) {
            if (yP != null) {
                zzhe = yP.zzhe(str);
            } else {
                yP = new zzqw(str, z);
                zzhe = yP.yR;
            }
        }
        return zzhe;
    }

    public static Status zzcb(Context context) {
        Status status;
        zzac.zzb((Object) context, (Object) "Context must not be null.");
        synchronized (zzaok) {
            if (yP == null) {
                yP = new zzqw(context);
            }
            status = yP.yR;
        }
        return status;
    }

    private static zzqw zzhf(String str) {
        zzqw com_google_android_gms_internal_zzqw;
        synchronized (zzaok) {
            if (yP == null) {
                throw new IllegalStateException(new StringBuilder(String.valueOf(str).length() + 34).append("Initialize must be called before ").append(str).append(".").toString());
            }
            com_google_android_gms_internal_zzqw = yP;
        }
        return com_google_android_gms_internal_zzqw;
    }

    Status zzhe(String str) {
        if (this.zzcpe == null || this.zzcpe.equals(str)) {
            return Status.vY;
        }
        String str2 = this.zzcpe;
        return new Status(10, new StringBuilder(String.valueOf(str2).length() + 97).append("Initialize was called with two different Google App IDs.  Only the first app ID will be used: '").append(str2).append("'.").toString());
    }
}
