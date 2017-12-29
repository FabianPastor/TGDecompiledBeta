package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import com.google.firebase.FirebaseApp;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

final class zzu {
    private final Context zzair;
    private String zzct;
    private String zznzk;
    private int zznzl;
    private int zznzm = 0;

    public zzu(Context context) {
        this.zzair = context;
    }

    public static String zzb(KeyPair keyPair) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(keyPair.getPublic().getEncoded());
            digest[0] = (byte) ((digest[0] & 15) + 112);
            return Base64.encodeToString(digest, 0, 8, 11);
        } catch (NoSuchAlgorithmException e) {
            Log.w("FirebaseInstanceId", "Unexpected error, device missing required algorithms");
            return null;
        }
    }

    private final synchronized void zzcjj() {
        PackageInfo zzoa = zzoa(this.zzair.getPackageName());
        if (zzoa != null) {
            this.zznzk = Integer.toString(zzoa.versionCode);
            this.zzct = zzoa.versionName;
        }
    }

    public static String zzf(FirebaseApp firebaseApp) {
        String gcmSenderId = firebaseApp.getOptions().getGcmSenderId();
        if (gcmSenderId != null) {
            return gcmSenderId;
        }
        gcmSenderId = firebaseApp.getOptions().getApplicationId();
        if (!gcmSenderId.startsWith("1:")) {
            return gcmSenderId;
        }
        String[] split = gcmSenderId.split(":");
        if (split.length < 2) {
            return null;
        }
        gcmSenderId = split[1];
        return gcmSenderId.isEmpty() ? null : gcmSenderId;
    }

    private final PackageInfo zzoa(String str) {
        try {
            return this.zzair.getPackageManager().getPackageInfo(str, 0);
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Failed to find package ").append(valueOf).toString());
            return null;
        }
    }

    public final synchronized int zzcjf() {
        int i = 0;
        synchronized (this) {
            if (this.zznzm != 0) {
                i = this.zznzm;
            } else {
                PackageManager packageManager = this.zzair.getPackageManager();
                if (packageManager.checkPermission("com.google.android.c2dm.permission.SEND", "com.google.android.gms") == -1) {
                    Log.e("FirebaseInstanceId", "Google Play services missing or without correct permission.");
                } else {
                    Intent intent;
                    List queryIntentServices;
                    if (!zzq.isAtLeastO()) {
                        intent = new Intent("com.google.android.c2dm.intent.REGISTER");
                        intent.setPackage("com.google.android.gms");
                        queryIntentServices = packageManager.queryIntentServices(intent, 0);
                        if (queryIntentServices != null && queryIntentServices.size() > 0) {
                            this.zznzm = 1;
                            i = this.zznzm;
                        }
                    }
                    intent = new Intent("com.google.iid.TOKEN_REQUEST");
                    intent.setPackage("com.google.android.gms");
                    queryIntentServices = packageManager.queryBroadcastReceivers(intent, 0);
                    if (queryIntentServices == null || queryIntentServices.size() <= 0) {
                        Log.w("FirebaseInstanceId", "Failed to resolve IID implementation package, falling back");
                        if (zzq.isAtLeastO()) {
                            this.zznzm = 2;
                        } else {
                            this.zznzm = 1;
                        }
                        i = this.zznzm;
                    } else {
                        this.zznzm = 2;
                        i = this.zznzm;
                    }
                }
            }
        }
        return i;
    }

    public final synchronized String zzcjg() {
        if (this.zznzk == null) {
            zzcjj();
        }
        return this.zznzk;
    }

    public final synchronized String zzcjh() {
        if (this.zzct == null) {
            zzcjj();
        }
        return this.zzct;
    }

    public final synchronized int zzcji() {
        if (this.zznzl == 0) {
            PackageInfo zzoa = zzoa("com.google.android.gms");
            if (zzoa != null) {
                this.zznzl = zzoa.versionCode;
            }
        }
        return this.zznzl;
    }
}
