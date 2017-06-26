package com.google.android.gms.iid;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class InstanceID {
    public static final String ERROR_BACKOFF = "RETRY_LATER";
    public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
    public static final String ERROR_MISSING_INSTANCEID_SERVICE = "MISSING_INSTANCEID_SERVICE";
    public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
    public static final String ERROR_TIMEOUT = "TIMEOUT";
    private static Map<String, InstanceID> zzbgQ = new HashMap();
    private static zzh zzbgR;
    private static zze zzbgS;
    private static String zzbgW;
    private Context mContext;
    private KeyPair zzbgT;
    private String zzbgU = "";
    private long zzbgV;

    private InstanceID(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbgU = str;
    }

    public static InstanceID getInstance(Context context) {
        return zza(context, null);
    }

    public static synchronized InstanceID zza(Context context, Bundle bundle) {
        InstanceID instanceID;
        synchronized (InstanceID.class) {
            String string = bundle == null ? "" : bundle.getString("subtype");
            String str = string == null ? "" : string;
            Context applicationContext = context.getApplicationContext();
            if (zzbgR == null) {
                zzbgR = new zzh(applicationContext);
                zzbgS = new zze(applicationContext);
            }
            zzbgW = Integer.toString(zzba(applicationContext));
            instanceID = (InstanceID) zzbgQ.get(str);
            if (instanceID == null) {
                instanceID = new InstanceID(applicationContext, str, bundle);
                zzbgQ.put(str, instanceID);
            }
        }
        return instanceID;
    }

    static String zza(KeyPair keyPair) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(keyPair.getPublic().getEncoded());
            digest[0] = (byte) ((digest[0] & 15) + 112);
            return Base64.encodeToString(digest, 0, 8, 11);
        } catch (NoSuchAlgorithmException e) {
            Log.w("InstanceID", "Unexpected error, device missing required alghorithms");
            return null;
        }
    }

    static int zzba(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return i;
        }
    }

    static String zzbb(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static String zzj(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    private final KeyPair zzvK() {
        if (this.zzbgT == null) {
            this.zzbgT = zzbgR.zzds(this.zzbgU);
        }
        if (this.zzbgT == null) {
            this.zzbgV = System.currentTimeMillis();
            this.zzbgT = zzbgR.zzc(this.zzbgU, this.zzbgV);
        }
        return this.zzbgT;
    }

    public static zzh zzvM() {
        return zzbgR;
    }

    public void deleteInstanceID() throws IOException {
        zzb("*", "*", null);
        zzvL();
    }

    public void deleteToken(String str, String str2) throws IOException {
        zzb(str, str2, null);
    }

    public long getCreationTime() {
        if (this.zzbgV == 0) {
            String str = zzbgR.get(this.zzbgU, "cre");
            if (str != null) {
                this.zzbgV = Long.parseLong(str);
            }
        }
        return this.zzbgV;
    }

    public String getId() {
        return zza(zzvK());
    }

    public String getToken(String str, String str2) throws IOException {
        return getToken(str, str2, null);
    }

    public String getToken(String str, String str2, Bundle bundle) throws IOException {
        Object obj = null;
        Object obj2 = 1;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        Object obj3;
        String str3 = zzbgR.get("appVersion");
        if (str3 == null || !str3.equals(zzbgW)) {
            obj3 = 1;
        } else {
            str3 = zzbgR.get("lastToken");
            int i;
            if (str3 == null) {
                i = 1;
            } else {
                if ((System.currentTimeMillis() / 1000) - Long.valueOf(Long.parseLong(str3)).longValue() > 604800) {
                    i = 1;
                } else {
                    obj3 = null;
                }
            }
        }
        String zzf = obj3 != null ? null : zzbgR.zzf(this.zzbgU, str, str2);
        if (zzf == null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (bundle.getString("ttl") != null) {
                obj2 = null;
            }
            if (!"jwt".equals(bundle.getString("type"))) {
                obj = obj2;
            }
            zzf = zzc(str, str2, bundle);
            if (!(zzf == null || r1 == null)) {
                zzbgR.zza(this.zzbgU, str, str2, zzf, zzbgW);
            }
        }
        return zzf;
    }

    public final void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzbgR.zzg(this.zzbgU, str, str2);
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
        bundle.putString("subtype", "".equals(this.zzbgU) ? str : this.zzbgU);
        String str3 = "X-subtype";
        if (!"".equals(this.zzbgU)) {
            str = this.zzbgU;
        }
        bundle.putString(str3, str);
        zze.zzh(zzbgS.zza(bundle, zzvK()));
    }

    public final String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = "".equals(this.zzbgU) ? str : this.zzbgU;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return zze.zzh(zzbgS.zza(bundle, zzvK()));
    }

    public final void zzvL() {
        this.zzbgV = 0;
        zzbgR.zzdq(String.valueOf(this.zzbgU).concat("|"));
        this.zzbgT = null;
    }
}
