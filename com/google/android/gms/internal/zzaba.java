package com.google.android.gms.internal;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import com.google.android.gms.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzam;
import com.google.android.gms.common.internal.zzz;

@Deprecated
public final class zzaba {
    private static zzaba zzaCM;
    private static final Object zztX = new Object();
    private final String mAppId;
    private final Status zzaCN;
    private final boolean zzaCO;
    private final boolean zzaCP;

    zzaba(Context context) {
        boolean z = true;
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("google_app_measurement_enable", "integer", resources.getResourcePackageName(R.string.common_google_play_services_unknown_issue));
        if (identifier != 0) {
            boolean z2 = resources.getInteger(identifier) != 0;
            if (z2) {
                z = false;
            }
            this.zzaCP = z;
            z = z2;
        } else {
            this.zzaCP = false;
        }
        this.zzaCO = z;
        Object zzaV = zzz.zzaV(context);
        if (zzaV == null) {
            zzaV = new zzam(context).getString("google_app_id");
        }
        if (TextUtils.isEmpty(zzaV)) {
            this.zzaCN = new Status(10, "Missing google app id value from from string resources with name google_app_id.");
            this.mAppId = null;
            return;
        }
        this.mAppId = zzaV;
        this.zzaCN = Status.zzazx;
    }

    public static Status zzaQ(Context context) {
        Status status;
        zzac.zzb((Object) context, (Object) "Context must not be null.");
        synchronized (zztX) {
            if (zzaCM == null) {
                zzaCM = new zzaba(context);
            }
            status = zzaCM.zzaCN;
        }
        return status;
    }

    private static zzaba zzde(String str) {
        zzaba com_google_android_gms_internal_zzaba;
        synchronized (zztX) {
            if (zzaCM == null) {
                throw new IllegalStateException(new StringBuilder(String.valueOf(str).length() + 34).append("Initialize must be called before ").append(str).append(".").toString());
            }
            com_google_android_gms_internal_zzaba = zzaCM;
        }
        return com_google_android_gms_internal_zzaba;
    }

    public static String zzwQ() {
        return zzde("getGoogleAppId").mAppId;
    }

    public static boolean zzwR() {
        return zzde("isMeasurementExplicitlyDisabled").zzaCP;
    }
}
