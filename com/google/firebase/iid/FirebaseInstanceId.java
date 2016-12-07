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
    private static Map<String, FirebaseInstanceId> zzbha = new ArrayMap();
    private static zze zzciQ;
    private final FirebaseApp zzciR;
    private final zzd zzciS;
    private final String zzciT = zzaac();

    private FirebaseInstanceId(FirebaseApp firebaseApp, zzd com_google_firebase_iid_zzd) {
        this.zzciR = firebaseApp;
        this.zzciS = com_google_firebase_iid_zzd;
        if (this.zzciT == null) {
            throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
        }
        FirebaseInstanceIdService.zza(this.zzciR.getApplicationContext(), this);
    }

    public static FirebaseInstanceId getInstance() {
        return getInstance(FirebaseApp.getInstance());
    }

    @Keep
    public static synchronized FirebaseInstanceId getInstance(@NonNull FirebaseApp firebaseApp) {
        FirebaseInstanceId firebaseInstanceId;
        synchronized (FirebaseInstanceId.class) {
            firebaseInstanceId = (FirebaseInstanceId) zzbha.get(firebaseApp.getOptions().getApplicationId());
            if (firebaseInstanceId == null) {
                zzd zzb = zzd.zzb(firebaseApp.getApplicationContext(), null);
                if (zzciQ == null) {
                    zzciQ = new zze(zzb.zzaag());
                }
                firebaseInstanceId = new FirebaseInstanceId(firebaseApp, zzb);
                zzbha.put(firebaseApp.getOptions().getApplicationId(), firebaseInstanceId);
            }
        }
        return firebaseInstanceId;
    }

    static int zzK(Context context, String str) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Failed to find package ").append(valueOf).toString());
            return i;
        }
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

    static void zza(Context context, zzh com_google_firebase_iid_zzh) {
        com_google_firebase_iid_zzh.zzGA();
        Intent intent = new Intent();
        intent.putExtra("CMD", "RST");
        zzg.zzaaj().zzf(context, intent);
    }

    static String zzbT(Context context) {
        return getInstance().zzciR.getOptions().getApplicationId();
    }

    static int zzbU(Context context) {
        return zzK(context, context.getPackageName());
    }

    static String zzbg(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static void zzbh(Context context) {
        Intent intent = new Intent();
        intent.putExtra("CMD", "SYNC");
        zzg.zzaaj().zzf(context, intent);
    }

    static String zzv(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public void deleteInstanceId() throws IOException {
        this.zzciS.zzb("*", "*", null);
        this.zzciS.zzGu();
    }

    @WorkerThread
    public void deleteToken(String str, String str2) throws IOException {
        this.zzciS.zzb(str, str2, null);
    }

    public long getCreationTime() {
        return this.zzciS.getCreationTime();
    }

    public String getId() {
        return zza(this.zzciS.zzGt());
    }

    @Nullable
    public String getToken() {
        zza zzaad = zzaad();
        if (zzaad == null || zzaad.zzjC(zzd.zzbhg)) {
            FirebaseInstanceIdService.zzbV(this.zzciR.getApplicationContext());
        }
        return zzaad != null ? zzaad.zzbwP : null;
    }

    @WorkerThread
    public String getToken(String str, String str2) throws IOException {
        return this.zzciS.getToken(str, str2, null);
    }

    String zzaac() {
        String gcmSenderId = this.zzciR.getOptions().getGcmSenderId();
        if (gcmSenderId != null) {
            return gcmSenderId;
        }
        gcmSenderId = this.zzciR.getOptions().getApplicationId();
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
    zza zzaad() {
        return this.zzciS.zzaag().zzq("", this.zzciT, "*");
    }

    String zzaae() throws IOException {
        return getToken(this.zzciT, "*");
    }

    zze zzaaf() {
        return zzciQ;
    }

    public void zzju(String str) {
        zzciQ.zzju(str);
        FirebaseInstanceIdService.zzbV(this.zzciR.getApplicationContext());
    }

    void zzjv(String str) throws IOException {
        zza zzaad = zzaad();
        if (zzaad == null || zzaad.zzjC(zzd.zzbhg)) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        zzd com_google_firebase_iid_zzd = this.zzciS;
        String str3 = zzaad.zzbwP;
        valueOf = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        com_google_firebase_iid_zzd.getToken(str3, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), bundle);
    }

    void zzjw(String str) throws IOException {
        zza zzaad = zzaad();
        if (zzaad == null || zzaad.zzjC(zzd.zzbhg)) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        zzd com_google_firebase_iid_zzd = this.zzciS;
        String str3 = zzaad.zzbwP;
        valueOf = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        com_google_firebase_iid_zzd.zzb(str3, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), bundle);
    }
}
