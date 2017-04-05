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
    static Map<String, InstanceID> zzbhH = new HashMap();
    private static zzd zzbhI;
    private static zzc zzbhJ;
    static String zzbhN;
    Context mContext;
    KeyPair zzbhK;
    String zzbhL = "";
    long zzbhM;

    protected InstanceID(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbhL = str;
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
            if (zzbhI == null) {
                zzbhI = new zzd(applicationContext);
                zzbhJ = new zzc(applicationContext);
            }
            zzbhN = Integer.toString(zzbw(applicationContext));
            instanceID = (InstanceID) zzbhH.get(str);
            if (instanceID == null) {
                instanceID = new InstanceID(applicationContext, str, bundle);
                zzbhH.put(str, instanceID);
            }
        }
        return instanceID;
    }

    static String zza(KeyPair keyPair) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(keyPair.getPublic().getEncoded());
            digest[0] = (byte) (((digest[0] & 15) + 112) & 255);
            return Base64.encodeToString(digest, 0, 8, 11);
        } catch (NoSuchAlgorithmException e) {
            Log.w("InstanceID", "Unexpected error, device missing required alghorithms");
            return null;
        }
    }

    static int zzbw(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return i;
        }
    }

    static String zzbx(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static String zzv(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public void deleteInstanceID() throws IOException {
        zzb("*", "*", null);
        zzHi();
    }

    public void deleteToken(String str, String str2) throws IOException {
        zzb(str, str2, null);
    }

    public long getCreationTime() {
        if (this.zzbhM == 0) {
            String str = zzbhI.get(this.zzbhL, "cre");
            if (str != null) {
                this.zzbhM = Long.parseLong(str);
            }
        }
        return this.zzbhM;
    }

    public String getId() {
        return zza(zzHh());
    }

    public String getToken(String str, String str2) throws IOException {
        return getToken(str, str2, null);
    }

    public String getToken(String str, String str2, Bundle bundle) throws IOException {
        Object obj = null;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        Object obj2 = 1;
        String zzh = zzHk() ? null : zzbhI.zzh(this.zzbhL, str, str2);
        if (zzh == null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (bundle.getString("ttl") != null) {
                obj2 = null;
            }
            if (!"jwt".equals(bundle.getString("type"))) {
                obj = obj2;
            }
            zzh = zzc(str, str2, bundle);
            if (!(zzh == null || r1 == null)) {
                zzbhI.zza(this.zzbhL, str, str2, zzh, zzbhN);
            }
        }
        return zzh;
    }

    KeyPair zzHh() {
        if (this.zzbhK == null) {
            this.zzbhK = zzbhI.zzeI(this.zzbhL);
        }
        if (this.zzbhK == null) {
            this.zzbhM = System.currentTimeMillis();
            this.zzbhK = zzbhI.zze(this.zzbhL, this.zzbhM);
        }
        return this.zzbhK;
    }

    public void zzHi() {
        this.zzbhM = 0;
        zzbhI.zzeJ(this.zzbhL);
        this.zzbhK = null;
    }

    public zzd zzHj() {
        return zzbhI;
    }

    boolean zzHk() {
        String str = zzbhI.get("appVersion");
        if (str == null || !str.equals(zzbhN)) {
            return true;
        }
        str = zzbhI.get("lastToken");
        if (str == null) {
            return true;
        }
        return (System.currentTimeMillis() / 1000) - Long.valueOf(Long.parseLong(str)).longValue() > 604800;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzbhI.zzi(this.zzbhL, str, str2);
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
        bundle.putString("subtype", "".equals(this.zzbhL) ? str : this.zzbhL);
        String str3 = "X-subtype";
        if (!"".equals(this.zzbhL)) {
            str = this.zzbhL;
        }
        bundle.putString(str3, str);
        zzbhJ.zzq(zzbhJ.zza(bundle, zzHh()));
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = "".equals(this.zzbhL) ? str : this.zzbhL;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return zzbhJ.zzq(zzbhJ.zza(bundle, zzHh()));
    }
}
