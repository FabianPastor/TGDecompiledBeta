package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public class zzk {
    private static final Lock hI = new ReentrantLock();
    private static zzk hJ;
    private final Lock hK = new ReentrantLock();
    private final SharedPreferences hL;

    zzk(Context context) {
        this.hL = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static zzk zzbd(Context context) {
        zzac.zzy(context);
        hI.lock();
        try {
            if (hJ == null) {
                hJ = new zzk(context.getApplicationContext());
            }
            zzk com_google_android_gms_auth_api_signin_internal_zzk = hJ;
            return com_google_android_gms_auth_api_signin_internal_zzk;
        } finally {
            hI.unlock();
        }
    }

    private String zzy(String str, String str2) {
        String valueOf = String.valueOf(":");
        return new StringBuilder(((String.valueOf(str).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    void zza(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzac.zzy(googleSignInAccount);
        zzac.zzy(googleSignInOptions);
        String zzahf = googleSignInAccount.zzahf();
        zzx(zzy("googleSignInAccount", zzahf), googleSignInAccount.zzahh());
        zzx(zzy("googleSignInOptions", zzahf), googleSignInOptions.zzahg());
    }

    public GoogleSignInAccount zzaic() {
        return zzga(zzgc("defaultGoogleSignInAccount"));
    }

    public GoogleSignInOptions zzaid() {
        return zzgb(zzgc("defaultGoogleSignInAccount"));
    }

    public void zzaie() {
        String zzgc = zzgc("defaultGoogleSignInAccount");
        zzge("defaultGoogleSignInAccount");
        zzgd(zzgc);
    }

    public void zzb(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzac.zzy(googleSignInAccount);
        zzac.zzy(googleSignInOptions);
        zzx("defaultGoogleSignInAccount", googleSignInAccount.zzahf());
        zza(googleSignInAccount, googleSignInOptions);
    }

    GoogleSignInAccount zzga(String str) {
        GoogleSignInAccount googleSignInAccount = null;
        if (!TextUtils.isEmpty(str)) {
            String zzgc = zzgc(zzy("googleSignInAccount", str));
            if (zzgc != null) {
                try {
                    googleSignInAccount = GoogleSignInAccount.zzfw(zzgc);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInAccount;
    }

    GoogleSignInOptions zzgb(String str) {
        GoogleSignInOptions googleSignInOptions = null;
        if (!TextUtils.isEmpty(str)) {
            String zzgc = zzgc(zzy("googleSignInOptions", str));
            if (zzgc != null) {
                try {
                    googleSignInOptions = GoogleSignInOptions.zzfy(zzgc);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInOptions;
    }

    protected String zzgc(String str) {
        this.hK.lock();
        try {
            String string = this.hL.getString(str, null);
            return string;
        } finally {
            this.hK.unlock();
        }
    }

    void zzgd(String str) {
        if (!TextUtils.isEmpty(str)) {
            zzge(zzy("googleSignInAccount", str));
            zzge(zzy("googleSignInOptions", str));
        }
    }

    protected void zzge(String str) {
        this.hK.lock();
        try {
            this.hL.edit().remove(str).apply();
        } finally {
            this.hK.unlock();
        }
    }

    protected void zzx(String str, String str2) {
        this.hK.lock();
        try {
            this.hL.edit().putString(str, str2).apply();
        } finally {
            this.hK.unlock();
        }
    }
}
