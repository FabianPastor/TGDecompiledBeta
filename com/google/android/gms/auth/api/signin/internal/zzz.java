package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.internal.zzbq;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public final class zzz {
    private static final Lock zzeiu = new ReentrantLock();
    private static zzz zzeiv;
    private final Lock zzeiw = new ReentrantLock();
    private final SharedPreferences zzeix;

    private zzz(Context context) {
        this.zzeix = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static zzz zzbt(Context context) {
        zzbq.checkNotNull(context);
        zzeiu.lock();
        try {
            if (zzeiv == null) {
                zzeiv = new zzz(context.getApplicationContext());
            }
            zzz com_google_android_gms_auth_api_signin_internal_zzz = zzeiv;
            return com_google_android_gms_auth_api_signin_internal_zzz;
        } finally {
            zzeiu.unlock();
        }
    }

    private final GoogleSignInAccount zzex(String str) {
        GoogleSignInAccount googleSignInAccount = null;
        if (!TextUtils.isEmpty(str)) {
            String zzez = zzez(zzp("googleSignInAccount", str));
            if (zzez != null) {
                try {
                    googleSignInAccount = GoogleSignInAccount.zzeu(zzez);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInAccount;
    }

    private final String zzez(String str) {
        this.zzeiw.lock();
        try {
            String string = this.zzeix.getString(str, null);
            return string;
        } finally {
            this.zzeiw.unlock();
        }
    }

    private static String zzp(String str, String str2) {
        String str3 = ":";
        return new StringBuilder((String.valueOf(str).length() + String.valueOf(str3).length()) + String.valueOf(str2).length()).append(str).append(str3).append(str2).toString();
    }

    public final GoogleSignInAccount zzabt() {
        return zzex(zzez("defaultGoogleSignInAccount"));
    }
}
