package com.google.android.gms.iid;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class InstanceID {
    private static Map<String, InstanceID> zzifg = new ArrayMap();
    private static zzo zzifh;
    private static zzl zzifi;
    private static String zzifm;
    private Context mContext;
    private KeyPair zzifj;
    private String zzifk = TtmlNode.ANONYMOUS_REGION_ID;
    private long zzifl;

    private InstanceID(Context context, String str, Bundle bundle) {
        this.mContext = context.getApplicationContext();
        this.zzifk = str;
    }

    public static InstanceID getInstance(Context context) {
        return getInstance(context, null);
    }

    public static synchronized InstanceID getInstance(Context context, Bundle bundle) {
        InstanceID instanceID;
        synchronized (InstanceID.class) {
            String string = bundle == null ? TtmlNode.ANONYMOUS_REGION_ID : bundle.getString("subtype");
            String str = string == null ? TtmlNode.ANONYMOUS_REGION_ID : string;
            Context applicationContext = context.getApplicationContext();
            if (zzifh == null) {
                zzifh = new zzo(applicationContext);
                zzifi = new zzl(applicationContext);
            }
            zzifm = Integer.toString(zzdm(applicationContext));
            instanceID = (InstanceID) zzifg.get(str);
            if (instanceID == null) {
                instanceID = new InstanceID(applicationContext, str, bundle);
                zzifg.put(str, instanceID);
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
            Log.w("InstanceID", "Unexpected error, device missing required algorithms");
            return null;
        }
    }

    private final KeyPair zzave() {
        if (this.zzifj == null) {
            this.zzifj = zzifh.zzib(this.zzifk);
        }
        if (this.zzifj == null) {
            this.zzifl = System.currentTimeMillis();
            this.zzifj = zzifh.zzc(this.zzifk, this.zzifl);
        }
        return this.zzifj;
    }

    public static zzo zzavg() {
        return zzifh;
    }

    static int zzdm(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return i;
        }
    }

    static String zzdn(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static String zzo(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public String getToken(String str, String str2, Bundle bundle) throws IOException {
        Object obj = null;
        Object obj2 = 1;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException("MAIN_THREAD");
        }
        Object obj3;
        String str3 = zzifh.get("appVersion");
        if (str3 == null || !str3.equals(zzifm)) {
            obj3 = 1;
        } else {
            str3 = zzifh.get("lastToken");
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
        String zze = obj3 != null ? null : zzifh.zze(this.zzifk, str, str2);
        if (zze == null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (bundle.getString("ttl") != null) {
                obj2 = null;
            }
            if (!"jwt".equals(bundle.getString("type"))) {
                obj = obj2;
            }
            zze = zzb(str, str2, bundle);
            if (!(zze == null || r1 == null)) {
                zzifh.zza(this.zzifk, str, str2, zze, zzifm);
            }
        }
        return zze;
    }

    public final void zzavf() {
        this.zzifl = 0;
        zzifh.zzhz(String.valueOf(this.zzifk).concat("|"));
        this.zzifj = null;
    }

    public final String zzb(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString("scope", str2);
        }
        bundle.putString("sender", str);
        String str3 = TtmlNode.ANONYMOUS_REGION_ID.equals(this.zzifk) ? str : this.zzifk;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return zzl.zzj(zzifi.zza(bundle, zzave()));
    }
}
