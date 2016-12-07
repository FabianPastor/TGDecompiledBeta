package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class zzd {
    static Map<String, zzd> aic = new HashMap();
    static String aii;
    private static zzg bkM;
    private static zzf bkN;
    KeyPair aif;
    String aig = "";
    Context mContext;

    protected zzd(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.aig = str;
    }

    public static synchronized zzd zzb(Context context, Bundle bundle) {
        zzd com_google_firebase_iid_zzd;
        synchronized (zzd.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (bkM == null) {
                bkM = new zzg(applicationContext);
                bkN = new zzf(applicationContext);
            }
            aii = Integer.toString(FirebaseInstanceId.zzeq(applicationContext));
            com_google_firebase_iid_zzd = (zzd) aic.get(str);
            if (com_google_firebase_iid_zzd == null) {
                com_google_firebase_iid_zzd = new zzd(applicationContext, str, bundle);
                aic.put(str, com_google_firebase_iid_zzd);
            }
        }
        return com_google_firebase_iid_zzd;
    }

    public zzg J() {
        return bkM;
    }

    public zzf K() {
        return bkN;
    }

    public long getCreationTime() {
        return bkM.zztv(this.aig);
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
            zza zzq = bkM.zzq(this.aig, str, str2);
            if (!(zzq == null || zzq.zzty(aii))) {
                return zzq.axH;
            }
        }
        String zzc = zzc(str, str2, bundle);
        if (zzc == null || r0 == null) {
            return zzc;
        }
        bkM.zza(this.aig, str, str2, zzc, aii);
        return zzc;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        bkM.zzi(this.aig, str, str2);
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
        bundle.putString("subtype", "".equals(this.aig) ? str : this.aig);
        String str3 = "X-subtype";
        if (!"".equals(this.aig)) {
            str = this.aig;
        }
        bundle.putString(str3, str);
        bkN.zzt(bkN.zza(bundle, zzboi()));
    }

    KeyPair zzboi() {
        if (this.aif == null) {
            this.aif = bkM.zzks(this.aig);
        }
        if (this.aif == null) {
            this.aif = bkM.zztw(this.aig);
        }
        return this.aif;
    }

    public void zzboj() {
        bkM.zzkt(this.aig);
        this.aif = null;
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = "".equals(this.aig) ? str : this.aig;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return bkN.zzt(bkN.zza(bundle, zzboi()));
    }
}
