package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Map;

public final class zzj {
    private static Map<String, zzj> zzbgQ = new ArrayMap();
    static String zzbgW;
    private static zzr zzckB;
    private static zzl zzckC;
    private Context mContext;
    private KeyPair zzbgT;
    private String zzbgU = "";

    private zzj(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbgU = str;
    }

    public static zzr zzJQ() {
        return zzckB;
    }

    public static zzl zzJR() {
        return zzckC;
    }

    public static synchronized zzj zzb(Context context, Bundle bundle) {
        zzj com_google_firebase_iid_zzj;
        synchronized (zzj.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (zzckB == null) {
                zzckB = new zzr(applicationContext);
                zzckC = new zzl(applicationContext);
            }
            zzbgW = Integer.toString(FirebaseInstanceId.zzbF(applicationContext));
            com_google_firebase_iid_zzj = (zzj) zzbgQ.get(str);
            if (com_google_firebase_iid_zzj == null) {
                com_google_firebase_iid_zzj = new zzj(applicationContext, str, bundle);
                zzbgQ.put(str, com_google_firebase_iid_zzj);
            }
        }
        return com_google_firebase_iid_zzj;
    }

    public final long getCreationTime() {
        return zzckB.zzhk(this.zzbgU);
    }

    public final String getToken(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        Object obj = 1;
        if (bundle.getString("ttl") != null || "jwt".equals(bundle.getString("type"))) {
            obj = null;
        } else {
            zzs zzp = zzckB.zzp(this.zzbgU, str, str2);
            if (!(zzp == null || zzp.zzhp(zzbgW))) {
                return zzp.zzbPH;
            }
        }
        String zzc = zzc(str, str2, bundle);
        if (zzc == null || r0 == null) {
            return zzc;
        }
        zzckB.zza(this.zzbgU, str, str2, zzc, zzbgW);
        return zzc;
    }

    public final void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzckB.zzg(this.zzbgU, str, str2);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("delete", "1");
        zzc(str, str2, bundle);
    }

    public final String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        if (!"".equals(this.zzbgU)) {
            str = this.zzbgU;
        }
        bundle.putString("subtype", str);
        bundle.putString("X-subtype", str);
        Intent zza = zzckC.zza(bundle, zzvK());
        if (zza == null) {
            throw new IOException("SERVICE_NOT_AVAILABLE");
        }
        String stringExtra = zza.getStringExtra("registration_id");
        if (stringExtra == null) {
            stringExtra = zza.getStringExtra("unregistered");
        }
        if (stringExtra != null) {
            return stringExtra;
        }
        stringExtra = zza.getStringExtra("error");
        if (stringExtra != null) {
            throw new IOException(stringExtra);
        }
        String valueOf = String.valueOf(zza.getExtras());
        Log.w("InstanceID/Rpc", new StringBuilder(String.valueOf(valueOf).length() + 29).append("Unexpected response from GCM ").append(valueOf).toString(), new Throwable());
        throw new IOException("SERVICE_NOT_AVAILABLE");
    }

    final KeyPair zzvK() {
        if (this.zzbgT == null) {
            this.zzbgT = zzckB.zzhn(this.zzbgU);
        }
        if (this.zzbgT == null) {
            this.zzbgT = zzckB.zzhl(this.zzbgU);
        }
        return this.zzbgT;
    }

    public final void zzvL() {
        zzckB.zzhm(this.zzbgU);
        this.zzbgT = null;
    }
}
