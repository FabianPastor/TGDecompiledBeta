package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public final class zzy {
    private static final Lock zzamD = new ReentrantLock();
    private static zzy zzamE;
    private final Lock zzamF = new ReentrantLock();
    private final SharedPreferences zzamG;

    private zzy(Context context) {
        this.zzamG = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static zzy zzaj(Context context) {
        zzbo.zzu(context);
        zzamD.lock();
        try {
            if (zzamE == null) {
                zzamE = new zzy(context.getApplicationContext());
            }
            zzy com_google_android_gms_auth_api_signin_internal_zzy = zzamE;
            return com_google_android_gms_auth_api_signin_internal_zzy;
        } finally {
            zzamD.unlock();
        }
    }

    private final GoogleSignInAccount zzbS(String str) {
        GoogleSignInAccount googleSignInAccount = null;
        if (!TextUtils.isEmpty(str)) {
            String zzbU = zzbU(zzs("googleSignInAccount", str));
            if (zzbU != null) {
                try {
                    googleSignInAccount = GoogleSignInAccount.zzbP(zzbU);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInAccount;
    }

    private final GoogleSignInOptions zzbT(String str) {
        GoogleSignInOptions googleSignInOptions = null;
        if (!TextUtils.isEmpty(str)) {
            String zzbU = zzbU(zzs("googleSignInOptions", str));
            if (zzbU != null) {
                try {
                    googleSignInOptions = GoogleSignInOptions.zzbQ(zzbU);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInOptions;
    }

    private final String zzbU(String str) {
        this.zzamF.lock();
        try {
            String string = this.zzamG.getString(str, null);
            return string;
        } finally {
            this.zzamF.unlock();
        }
    }

    private final void zzbV(String str) {
        this.zzamF.lock();
        try {
            this.zzamG.edit().remove(str).apply();
        } finally {
            this.zzamF.unlock();
        }
    }

    private final void zzr(String str, String str2) {
        this.zzamF.lock();
        try {
            this.zzamG.edit().putString(str, str2).apply();
        } finally {
            this.zzamF.unlock();
        }
    }

    private static String zzs(String str, String str2) {
        String valueOf = String.valueOf(":");
        return new StringBuilder((String.valueOf(str).length() + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    public final void zza(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzbo.zzu(googleSignInAccount);
        zzbo.zzu(googleSignInOptions);
        zzr("defaultGoogleSignInAccount", googleSignInAccount.zzmx());
        zzbo.zzu(googleSignInAccount);
        zzbo.zzu(googleSignInOptions);
        String zzmx = googleSignInAccount.zzmx();
        zzr(zzs("googleSignInAccount", zzmx), googleSignInAccount.zzmy());
        zzr(zzs("googleSignInOptions", zzmx), googleSignInOptions.zzmC());
    }

    public final GoogleSignInAccount zzmN() {
        return zzbS(zzbU("defaultGoogleSignInAccount"));
    }

    public final GoogleSignInOptions zzmO() {
        return zzbT(zzbU("defaultGoogleSignInAccount"));
    }

    public final void zzmP() {
        String zzbU = zzbU("defaultGoogleSignInAccount");
        zzbV("defaultGoogleSignInAccount");
        if (!TextUtils.isEmpty(zzbU)) {
            zzbV(zzs("googleSignInAccount", zzbU));
            zzbV(zzs("googleSignInOptions", zzbU));
        }
    }
}
