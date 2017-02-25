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

public class zzn {
    private static final Lock zzakO = new ReentrantLock();
    private static zzn zzakP;
    private final Lock zzakQ = new ReentrantLock();
    private final SharedPreferences zzakR;

    zzn(Context context) {
        this.zzakR = context.getSharedPreferences("com.google.android.gms.signin", 0);
    }

    public static zzn zzas(Context context) {
        zzac.zzw(context);
        zzakO.lock();
        try {
            if (zzakP == null) {
                zzakP = new zzn(context.getApplicationContext());
            }
            zzn com_google_android_gms_auth_api_signin_internal_zzn = zzakP;
            return com_google_android_gms_auth_api_signin_internal_zzn;
        } finally {
            zzakO.unlock();
        }
    }

    private String zzy(String str, String str2) {
        String valueOf = String.valueOf(":");
        return new StringBuilder((String.valueOf(str).length() + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    void zza(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzac.zzw(googleSignInAccount);
        zzac.zzw(googleSignInOptions);
        String zzrf = googleSignInAccount.zzrf();
        zzx(zzy("googleSignInAccount", zzrf), googleSignInAccount.zzrh());
        zzx(zzy("googleSignInOptions", zzrf), googleSignInOptions.zzrg());
    }

    public void zzb(GoogleSignInAccount googleSignInAccount, GoogleSignInOptions googleSignInOptions) {
        zzac.zzw(googleSignInAccount);
        zzac.zzw(googleSignInOptions);
        zzx("defaultGoogleSignInAccount", googleSignInAccount.zzrf());
        zza(googleSignInAccount, googleSignInOptions);
    }

    GoogleSignInOptions zzcA(String str) {
        GoogleSignInOptions googleSignInOptions = null;
        if (!TextUtils.isEmpty(str)) {
            String zzcB = zzcB(zzy("googleSignInOptions", str));
            if (zzcB != null) {
                try {
                    googleSignInOptions = GoogleSignInOptions.zzcx(zzcB);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInOptions;
    }

    protected String zzcB(String str) {
        this.zzakQ.lock();
        try {
            String string = this.zzakR.getString(str, null);
            return string;
        } finally {
            this.zzakQ.unlock();
        }
    }

    void zzcC(String str) {
        if (!TextUtils.isEmpty(str)) {
            zzcD(zzy("googleSignInAccount", str));
            zzcD(zzy("googleSignInOptions", str));
        }
    }

    protected void zzcD(String str) {
        this.zzakQ.lock();
        try {
            this.zzakR.edit().remove(str).apply();
        } finally {
            this.zzakQ.unlock();
        }
    }

    GoogleSignInAccount zzcz(String str) {
        GoogleSignInAccount googleSignInAccount = null;
        if (!TextUtils.isEmpty(str)) {
            String zzcB = zzcB(zzy("googleSignInAccount", str));
            if (zzcB != null) {
                try {
                    googleSignInAccount = GoogleSignInAccount.zzcv(zzcB);
                } catch (JSONException e) {
                }
            }
        }
        return googleSignInAccount;
    }

    public GoogleSignInAccount zzrB() {
        return zzcz(zzcB("defaultGoogleSignInAccount"));
    }

    public GoogleSignInOptions zzrC() {
        return zzcA(zzcB("defaultGoogleSignInAccount"));
    }

    public void zzrD() {
        String zzcB = zzcB("defaultGoogleSignInAccount");
        zzcD("defaultGoogleSignInAccount");
        zzcC(zzcB);
    }

    protected void zzx(String str, String str2) {
        this.zzakQ.lock();
        try {
            this.zzakR.edit().putString(str, str2).apply();
        } finally {
            this.zzakQ.unlock();
        }
    }
}
