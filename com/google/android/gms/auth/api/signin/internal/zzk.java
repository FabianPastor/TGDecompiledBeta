package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.internal.zzaa;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONException;

public class zzk {
    private static final Lock jS = new ReentrantLock();
    private static zzk jT;
    private final Lock jU = new ReentrantLock();
    private final SharedPreferences jV;

    zzk(Context context) {
        this.jV = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static zzk zzba(Context context) {
        zzaa.zzy(context);
        jS.lock();
        try {
            if (jT == null) {
                jT = new zzk(context.getApplicationContext());
            }
            zzk com_google_android_gms_auth_api_signin_internal_zzk = jT;
            return com_google_android_gms_auth_api_signin_internal_zzk;
        } finally {
            jS.unlock();
        }
    }

    private String zzx(String str, String str2) {
        String valueOf = String.valueOf(":");
        return new StringBuilder(((String.valueOf(str).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    void zza(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzaa.zzy(googleSignInAccount);
        zzaa.zzy(googleSignInOptions);
        String zzaip = googleSignInAccount.zzaip();
        zzw(zzx("googleSignInAccount", zzaip), googleSignInAccount.zzair());
        zzw(zzx("googleSignInOptions", zzaip), googleSignInOptions.zzaiq());
    }

    public GoogleSignInAccount zzajm() {
        return zzgd(zzgf("defaultGoogleSignInAccount"));
    }

    public GoogleSignInOptions zzajn() {
        return zzge(zzgf("defaultGoogleSignInAccount"));
    }

    public void zzajo() {
        String zzgf = zzgf("defaultGoogleSignInAccount");
        zzgh("defaultGoogleSignInAccount");
        zzgg(zzgf);
    }

    public void zzb(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzaa.zzy(googleSignInAccount);
        zzaa.zzy(googleSignInOptions);
        zzw("defaultGoogleSignInAccount", googleSignInAccount.zzaip());
        zza(googleSignInAccount, googleSignInOptions);
    }

    GoogleSignInAccount zzgd(String str) {
        GoogleSignInAccount googleSignInAccount = null;
        if (!TextUtils.isEmpty(str)) {
            String zzgf = zzgf(zzx("googleSignInAccount", str));
            if (zzgf != null) {
                try {
                    googleSignInAccount = GoogleSignInAccount.zzfz(zzgf);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInAccount;
    }

    GoogleSignInOptions zzge(String str) {
        GoogleSignInOptions googleSignInOptions = null;
        if (!TextUtils.isEmpty(str)) {
            String zzgf = zzgf(zzx("googleSignInOptions", str));
            if (zzgf != null) {
                try {
                    googleSignInOptions = GoogleSignInOptions.zzgb(zzgf);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInOptions;
    }

    protected String zzgf(String str) {
        this.jU.lock();
        try {
            String string = this.jV.getString(str, null);
            return string;
        } finally {
            this.jU.unlock();
        }
    }

    void zzgg(String str) {
        if (!TextUtils.isEmpty(str)) {
            zzgh(zzx("googleSignInAccount", str));
            zzgh(zzx("googleSignInOptions", str));
        }
    }

    protected void zzgh(String str) {
        this.jU.lock();
        try {
            this.jV.edit().remove(str).apply();
        } finally {
            this.jU.unlock();
        }
    }

    protected void zzw(String str, String str2) {
        this.jU.lock();
        try {
            this.jV.edit().putString(str, str2).apply();
        } finally {
            this.jU.unlock();
        }
    }
}
