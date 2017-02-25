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
    static Map<String, InstanceID> zzbhG = new HashMap();
    private static zzd zzbhH;
    private static zzc zzbhI;
    static String zzbhM;
    Context mContext;
    KeyPair zzbhJ;
    String zzbhK = "";
    long zzbhL;

    protected InstanceID(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzbhK = str;
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
            if (zzbhH == null) {
                zzbhH = new zzd(applicationContext);
                zzbhI = new zzc(applicationContext);
            }
            zzbhM = Integer.toString(zzbx(applicationContext));
            instanceID = (InstanceID) zzbhG.get(str);
            if (instanceID == null) {
                instanceID = new InstanceID(applicationContext, str, bundle);
                zzbhG.put(str, instanceID);
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

    static int zzbx(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return i;
        }
    }

    static String zzby(Context context) {
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
        zzHh();
    }

    public void deleteToken(String str, String str2) throws IOException {
        zzb(str, str2, null);
    }

    public long getCreationTime() {
        if (this.zzbhL == 0) {
            String str = zzbhH.get(this.zzbhK, "cre");
            if (str != null) {
                this.zzbhL = Long.parseLong(str);
            }
        }
        return this.zzbhL;
    }

    public String getId() {
        return zza(zzHg());
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
        String zzh = zzHk() ? null : zzbhH.zzh(this.zzbhK, str, str2);
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
                zzbhH.zza(this.zzbhK, str, str2, zzh, zzbhM);
            }
        }
        return zzh;
    }

    KeyPair zzHg() {
        if (this.zzbhJ == null) {
            this.zzbhJ = zzbhH.zzeI(this.zzbhK);
        }
        if (this.zzbhJ == null) {
            this.zzbhL = System.currentTimeMillis();
            this.zzbhJ = zzbhH.zze(this.zzbhK, this.zzbhL);
        }
        return this.zzbhJ;
    }

    public void zzHh() {
        this.zzbhL = 0;
        zzbhH.zzeJ(this.zzbhK);
        this.zzbhJ = null;
    }

    public zzd zzHi() {
        return zzbhH;
    }

    public zzc zzHj() {
        return zzbhI;
    }

    boolean zzHk() {
        String str = zzbhH.get("appVersion");
        if (str == null || !str.equals(zzbhM)) {
            return true;
        }
        str = zzbhH.get("lastToken");
        if (str == null) {
            return true;
        }
        return (System.currentTimeMillis() / 1000) - Long.valueOf(Long.parseLong(str)).longValue() > 604800;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        zzbhH.zzi(this.zzbhK, str, str2);
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
        zzbhI.zzt(zzbhI.zza(bundle, zzHg()));
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
        return zzbhI.zzt(zzbhI.zza(bundle, zzHg()));
    }
}
