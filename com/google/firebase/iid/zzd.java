package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class zzd {
    static Map<String, zzd> afS = new HashMap();
    static String afY;
    private static zzg bhA;
    private static zzf bhB;
    KeyPair afV;
    String afW = "";
    Context mContext;

    protected zzd(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.afW = str;
    }

    public static synchronized zzd zzb(Context context, Bundle bundle) {
        zzd com_google_firebase_iid_zzd;
        synchronized (zzd.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (bhA == null) {
                bhA = new zzg(applicationContext);
                bhB = new zzf(applicationContext);
            }
            afY = Integer.toString(FirebaseInstanceId.zzdg(applicationContext));
            com_google_firebase_iid_zzd = (zzd) afS.get(str);
            if (com_google_firebase_iid_zzd == null) {
                com_google_firebase_iid_zzd = new zzd(applicationContext, str, bundle);
                afS.put(str, com_google_firebase_iid_zzd);
            }
        }
        return com_google_firebase_iid_zzd;
    }

    public zzg H() {
        return bhA;
    }

    public zzf I() {
        return bhB;
    }

    public long getCreationTime() {
        return bhA.zztw(this.afW);
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
            zza zzq = bhA.zzq(this.afW, str, str2);
            if (!(zzq == null || zzq.zztz(afY))) {
                return zzq.auj;
            }
        }
        String zzc = zzc(str, str2, bundle);
        if (zzc == null || r0 == null) {
            return zzc;
        }
        bhA.zza(this.afW, str, str2, zzc, afY);
        return zzc;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        bhA.zzi(this.afW, str, str2);
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
        bundle.putString("subtype", "".equals(this.afW) ? str : this.afW);
        String str3 = "X-subtype";
        if (!"".equals(this.afW)) {
            str = this.afW;
        }
        bundle.putString(str3, str);
        bhB.zzt(bhB.zza(bundle, zzbop()));
    }

    KeyPair zzbop() {
        if (this.afV == null) {
            this.afV = bhA.zzks(this.afW);
        }
        if (this.afV == null) {
            this.afV = bhA.zztx(this.afW);
        }
        return this.afV;
    }

    public void zzboq() {
        bhA.zzkt(this.afW);
        this.afV = null;
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = "".equals(this.afW) ? str : this.afW;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return bhB.zzt(bhB.zza(bundle, zzbop()));
    }
}
