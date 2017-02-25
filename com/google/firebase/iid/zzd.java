package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class zzd {
    static Map<String, zzd> zzbhG = new HashMap();
    static String zzbhM;
    private static zzh zzcll;
    private static zzf zzclm;
    Context mContext;
    KeyPair zzbhJ;
    String zzbhK = "";

    protected zzd(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbhK = str;
    }

    public static synchronized zzd zzb(Context context, Bundle bundle) {
        zzd com_google_firebase_iid_zzd;
        synchronized (zzd.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (zzcll == null) {
                zzcll = new zzh(applicationContext);
                zzclm = new zzf(applicationContext);
            }
            zzbhM = Integer.toString(FirebaseInstanceId.zzcs(applicationContext));
            com_google_firebase_iid_zzd = (zzd) zzbhG.get(str);
            if (com_google_firebase_iid_zzd == null) {
                com_google_firebase_iid_zzd = new zzd(applicationContext, str, bundle);
                zzbhG.put(str, com_google_firebase_iid_zzd);
            }
        }
        return com_google_firebase_iid_zzd;
    }

    public long getCreationTime() {
        return zzcll.zzjy(this.zzbhK);
    }

    public String getToken(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        if (bundle == null) {
            bundle = new Bundle();
        }
        Object obj = 1;
        if (bundle.getString("ttl") != null || "jwt".equals(bundle.getString("type"))) {
            obj = null;
        } else {
            zza zzu = zzcll.zzu(this.zzbhK, str, str2);
            if (!(zzu == null || zzu.zzjB(zzbhM))) {
                return zzu.zzbxX;
            }
        }
        String zzc = zzc(str, str2, bundle);
        if (zzc == null || r0 == null) {
            return zzc;
        }
        zzcll.zza(this.zzbhK, str, str2, zzc, zzbhM);
        return zzc;
    }

    KeyPair zzHg() {
        if (this.zzbhJ == null) {
            this.zzbhJ = zzcll.zzeI(this.zzbhK);
        }
        if (this.zzbhJ == null) {
            this.zzbhJ = zzcll.zzjz(this.zzbhK);
        }
        return this.zzbhJ;
    }

    public void zzHh() {
        zzcll.zzeJ(this.zzbhK);
        this.zzbhJ = null;
    }

    public zzh zzabP() {
        return zzcll;
    }

    public zzf zzabQ() {
        return zzclm;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzcll.zzi(this.zzbhK, str, str2);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("sender", str);
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("subscription", str);
        bundle.putString("delete", "1");
        bundle.putString("X-delete", "1");
        bundle.putString("subtype", "".equals(this.zzbhK) ? str : this.zzbhK);
        String str3 = "X-subtype";
        if (!"".equals(this.zzbhK)) {
            str = this.zzbhK;
        }
        bundle.putString(str3, str);
        zzclm.zzt(zzclm.zza(bundle, zzHg()));
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = "".equals(this.zzbhK) ? str : this.zzbhK;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return zzclm.zzt(zzclm.zza(bundle, zzHg()));
    }
}
