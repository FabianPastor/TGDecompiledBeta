package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class zzd {
    static Map<String, zzd> zzbha = new HashMap();
    static String zzbhg;
    private static zzh zzcja;
    private static zzf zzcjb;
    Context mContext;
    KeyPair zzbhd;
    String zzbhe = "";

    protected zzd(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbhe = str;
    }

    public static synchronized zzd zzb(Context context, Bundle bundle) {
        zzd com_google_firebase_iid_zzd;
        synchronized (zzd.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (zzcja == null) {
                zzcja = new zzh(applicationContext);
                zzcjb = new zzf(applicationContext);
            }
            zzbhg = Integer.toString(FirebaseInstanceId.zzbU(applicationContext));
            com_google_firebase_iid_zzd = (zzd) zzbha.get(str);
            if (com_google_firebase_iid_zzd == null) {
                com_google_firebase_iid_zzd = new zzd(applicationContext, str, bundle);
                zzbha.put(str, com_google_firebase_iid_zzd);
            }
        }
        return com_google_firebase_iid_zzd;
    }

    public long getCreationTime() {
        return zzcja.zzjz(this.zzbhe);
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
            zza zzq = zzcja.zzq(this.zzbhe, str, str2);
            if (!(zzq == null || zzq.zzjC(zzbhg))) {
                return zzq.zzbwP;
            }
        }
        String zzc = zzc(str, str2, bundle);
        if (zzc == null || r0 == null) {
            return zzc;
        }
        zzcja.zza(this.zzbhe, str, str2, zzc, zzbhg);
        return zzc;
    }

    KeyPair zzGt() {
        if (this.zzbhd == null) {
            this.zzbhd = zzcja.zzeM(this.zzbhe);
        }
        if (this.zzbhd == null) {
            this.zzbhd = zzcja.zzjA(this.zzbhe);
        }
        return this.zzbhd;
    }

    public void zzGu() {
        zzcja.zzeN(this.zzbhe);
        this.zzbhd = null;
    }

    public zzh zzaag() {
        return zzcja;
    }

    public zzf zzaah() {
        return zzcjb;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzcja.zzi(this.zzbhe, str, str2);
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
        bundle.putString("subtype", "".equals(this.zzbhe) ? str : this.zzbhe);
        String str3 = "X-subtype";
        if (!"".equals(this.zzbhe)) {
            str = this.zzbhe;
        }
        bundle.putString(str3, str);
        zzcjb.zzt(zzcjb.zza(bundle, zzGt()));
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = "".equals(this.zzbhe) ? str : this.zzbhe;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return zzcjb.zzt(zzcjb.zza(bundle, zzGt()));
    }
}
