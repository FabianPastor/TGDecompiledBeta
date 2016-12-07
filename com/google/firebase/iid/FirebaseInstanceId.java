package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class FirebaseInstanceId {
    private static Map<String, FirebaseInstanceId> aic = new ArrayMap();
    private static zze bkA;
    private final FirebaseApp bkB;
    private final zzd bkC;
    private final String bkD = D();

    private FirebaseInstanceId(FirebaseApp firebaseApp, zzd com_google_firebase_iid_zzd) {
        this.bkB = firebaseApp;
        this.bkC = com_google_firebase_iid_zzd;
        if (this.bkD == null) {
            throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
        }
        FirebaseInstanceIdService.zza(this.bkB.getApplicationContext(), this);
    }

    public static FirebaseInstanceId getInstance() {
        return getInstance(FirebaseApp.getInstance());
    }

    @Keep
    public static synchronized FirebaseInstanceId getInstance(@NonNull FirebaseApp firebaseApp) {
        FirebaseInstanceId firebaseInstanceId;
        synchronized (FirebaseInstanceId.class) {
            firebaseInstanceId = (FirebaseInstanceId) aic.get(firebaseApp.getOptions().getApplicationId());
            if (firebaseInstanceId == null) {
                zzd zzb = zzd.zzb(firebaseApp.getApplicationContext(), null);
                if (bkA == null) {
                    bkA = new zze(zzb.J());
                }
                firebaseInstanceId = new FirebaseInstanceId(firebaseApp, zzb);
                aic.put(firebaseApp.getOptions().getApplicationId(), firebaseInstanceId);
            }
        }
        return firebaseInstanceId;
    }

    static String zza(KeyPair keyPair) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(keyPair.getPublic().getEncoded());
            digest[0] = (byte) (((digest[0] & 15) + 112) & 255);
            return Base64.encodeToString(digest, 0, 8, 11);
        } catch (NoSuchAlgorithmException e) {
            Log.w("FirebaseInstanceId", "Unexpected error, device missing required alghorithms");
            return null;
        }
    }

    static void zza(Context context, zzg com_google_firebase_iid_zzg) {
        com_google_firebase_iid_zzg.zzbop();
        Intent intent = new Intent();
        intent.putExtra("CMD", "RST");
        context.sendBroadcast(FirebaseInstanceIdInternalReceiver.zzf(context, intent));
    }

    static int zzaj(Context context, String str) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Failed to find package ").append(valueOf).toString());
            return i;
        }
    }

    static String zzde(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static void zzdf(Context context) {
        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.putExtra("CMD", "SYNC");
        context.sendBroadcast(FirebaseInstanceIdInternalReceiver.zzf(context, intent));
    }

    static String zzep(Context context) {
        return getInstance().bkB.getOptions().getApplicationId();
    }

    static int zzeq(Context context) {
        return zzaj(context, context.getPackageName());
    }

    static String zzv(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    String D() {
        String gcmSenderId = this.bkB.getOptions().getGcmSenderId();
        if (gcmSenderId != null) {
            return gcmSenderId;
        }
        gcmSenderId = this.bkB.getOptions().getApplicationId();
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

    @Nullable
    zza E() {
        return this.bkC.J().zzq("", this.bkD, "*");
    }

    String F() throws IOException {
        return getToken(this.bkD, "*");
    }

    zze G() {
        return bkA;
    }

    public void deleteInstanceId() throws IOException {
        this.bkC.zzb("*", "*", null);
        this.bkC.zzboj();
    }

    @WorkerThread
    public void deleteToken(String str, String str2) throws IOException {
        this.bkC.zzb(str, str2, null);
    }

    public long getCreationTime() {
        return this.bkC.getCreationTime();
    }

    public String getId() {
        return zza(this.bkC.zzboi());
    }

    @Nullable
    public String getToken() {
        zza E = E();
        if (E == null || E.zzty(zzd.aii)) {
            FirebaseInstanceIdService.zzer(this.bkB.getApplicationContext());
        }
        return E != null ? E.axH : null;
    }

    @WorkerThread
    public String getToken(String str, String str2) throws IOException {
        return this.bkC.getToken(str, str2, null);
    }

    public void zztq(String str) {
        bkA.zztq(str);
        FirebaseInstanceIdService.zzer(this.bkB.getApplicationContext());
    }

    void zztr(String str) throws IOException {
        if (getToken() == null) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        zzd com_google_firebase_iid_zzd = this.bkC;
        valueOf = getToken();
        String valueOf3 = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        com_google_firebase_iid_zzd.getToken(valueOf, valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3), bundle);
    }

    void zzts(String str) throws IOException {
        if (getToken() == null) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        zzd com_google_firebase_iid_zzd = this.bkC;
        valueOf = getToken();
        String valueOf3 = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        com_google_firebase_iid_zzd.zzb(valueOf, valueOf2.length() != 0 ? valueOf3.concat(valueOf2) : new String(valueOf3), bundle);
    }
}
