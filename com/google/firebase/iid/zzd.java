package com.google.firebase.iid;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;

public class zzd {
    static Map<String, zzd> zzbhH = new ArrayMap();
    static String zzbhN;
    private static zzh zzclv;
    private static zzf zzclw;
    Context mContext;
    KeyPair zzbhK;
    String zzbhL = "";

    protected zzd(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbhL = str;
    }

    public static synchronized zzd zzb(Context context, Bundle bundle) {
        zzd com_google_firebase_iid_zzd;
        synchronized (zzd.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (zzclv == null) {
                zzclv = new zzh(applicationContext);
                zzclw = new zzf(applicationContext);
            }
            zzbhN = Integer.toString(FirebaseInstanceId.zzcr(applicationContext));
            com_google_firebase_iid_zzd = (zzd) zzbhH.get(str);
            if (com_google_firebase_iid_zzd == null) {
                com_google_firebase_iid_zzd = new zzd(applicationContext, str, bundle);
                zzbhH.put(str, com_google_firebase_iid_zzd);
            }
        }
        return com_google_firebase_iid_zzd;
    }

    public long getCreationTime() {
        return zzclv.zzjy(this.zzbhL);
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
            zza zzu = zzclv.zzu(this.zzbhL, str, str2);
            if (!(zzu == null || zzu.zzjB(zzbhN))) {
                return zzu.zzbxW;
            }
        }
        String zzc = zzc(str, str2, bundle);
        if (zzc == null || r0 == null) {
            return zzc;
        }
        zzclv.zza(this.zzbhL, str, str2, zzc, zzbhN);
        return zzc;
    }

    KeyPair zzHh() {
        if (this.zzbhK == null) {
            this.zzbhK = zzclv.zzeI(this.zzbhL);
        }
        if (this.zzbhK == null) {
            this.zzbhK = zzclv.zzjz(this.zzbhL);
        }
        return this.zzbhK;
    }

    public void zzHi() {
        zzclv.zzeJ(this.zzbhL);
        this.zzbhK = null;
    }

    public zzh zzabS() {
        return zzclv;
    }

    public zzf zzabT() {
        return zzclw;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzclv.zzi(this.zzbhL, str, str2);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("delete", "1");
        zzc(str, str2, bundle);
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        if (!"".equals(this.zzbhL)) {
            str = this.zzbhL;
        }
        bundle.putString("subtype", str);
        bundle.putString("X-subtype", str);
        return zzclw.zzq(zzclw.zza(bundle, zzHh()));
    }
}
