package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.common.zzp;

public final class zzb {
    private SharedPreferences zzani;

    public zzb(Context context) {
        try {
            Context remoteContext = zzp.getRemoteContext(context);
            this.zzani = remoteContext == null ? null : remoteContext.getSharedPreferences("google_ads_flags", 0);
        } catch (Throwable th) {
            Log.w("GmscoreFlag", "Error while getting SharedPreferences ", th);
            this.zzani = null;
        }
    }

    public final boolean getBoolean(String str, boolean z) {
        boolean z2 = false;
        try {
            if (this.zzani != null) {
                z2 = this.zzani.getBoolean(str, false);
            }
        } catch (Throwable th) {
            Log.w("GmscoreFlag", "Error while reading from SharedPreferences ", th);
        }
        return z2;
    }

    final float getFloat(String str, float f) {
        float f2 = 0.0f;
        try {
            if (this.zzani != null) {
                f2 = this.zzani.getFloat(str, 0.0f);
            }
        } catch (Throwable th) {
            Log.w("GmscoreFlag", "Error while reading from SharedPreferences ", th);
        }
        return f2;
    }

    final String getString(String str, String str2) {
        try {
            if (this.zzani != null) {
                str2 = this.zzani.getString(str, str2);
            }
        } catch (Throwable th) {
            Log.w("GmscoreFlag", "Error while reading from SharedPreferences ", th);
        }
        return str2;
    }
}
