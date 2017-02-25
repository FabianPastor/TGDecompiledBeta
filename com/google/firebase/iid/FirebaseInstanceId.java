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
    private static Map<String, FirebaseInstanceId> zzbhG = new ArrayMap();
    private static zze zzclb;
    private final FirebaseApp zzclc;
    private final zzd zzcld;
    private final String zzcle = zzabL();

    private FirebaseInstanceId(FirebaseApp firebaseApp, zzd com_google_firebase_iid_zzd) {
        this.zzclc = firebaseApp;
        this.zzcld = com_google_firebase_iid_zzd;
        if (this.zzcle == null) {
            throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
        }
        FirebaseInstanceIdService.zza(this.zzclc.getApplicationContext(), this);
    }

    public static FirebaseInstanceId getInstance() {
        return getInstance(FirebaseApp.getInstance());
    }

    @Keep
    public static synchronized FirebaseInstanceId getInstance(@NonNull FirebaseApp firebaseApp) {
        FirebaseInstanceId firebaseInstanceId;
        synchronized (FirebaseInstanceId.class) {
            firebaseInstanceId = (FirebaseInstanceId) zzbhG.get(firebaseApp.getOptions().getApplicationId());
            if (firebaseInstanceId == null) {
                zzd zzb = zzd.zzb(firebaseApp.getApplicationContext(), null);
                if (zzclb == null) {
                    zzclb = new zze(zzb.zzabP());
                }
                firebaseInstanceId = new FirebaseInstanceId(firebaseApp, zzb);
                zzbhG.put(firebaseApp.getOptions().getApplicationId(), firebaseInstanceId);
            }
        }
        return firebaseInstanceId;
    }

    static int zzQ(Context context, String str) {
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
        com_google_firebase_iid_zzh.zzHn();
        Intent intent = new Intent();
        intent.putExtra("CMD", "RST");
        zzg.zzabT().zzf(context, intent);
    }

    static String zzby(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static void zzbz(Context context) {
        Intent intent = new Intent();
        intent.putExtra("CMD", "SYNC");
        zzg.zzabT().zzf(context, intent);
    }

    static String zzcr(Context context) {
        return getInstance().zzclc.getOptions().getApplicationId();
    }

    static int zzcs(Context context) {
        return zzQ(context, context.getPackageName());
    }

    static String zzv(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public void deleteInstanceId() throws IOException {
        this.zzcld.zzb("*", "*", null);
        this.zzcld.zzHh();
    }

    @WorkerThread
    public void deleteToken(String str, String str2) throws IOException {
        this.zzcld.zzb(str, str2, null);
    }

    public long getCreationTime() {
        return this.zzcld.getCreationTime();
    }

    public String getId() {
        return zza(this.zzcld.zzHg());
    }

    @Nullable
    public String getToken() {
        zza zzabM = zzabM();
        if (zzabM == null || zzabM.zzjB(zzd.zzbhM)) {
            FirebaseInstanceIdService.zzct(this.zzclc.getApplicationContext());
        }
        return zzabM != null ? zzabM.zzbxX : null;
    }

    @WorkerThread
    public String getToken(String str, String str2) throws IOException {
        return this.zzcld.getToken(str, str2, null);
    }

    String zzabL() {
        String gcmSenderId = this.zzclc.getOptions().getGcmSenderId();
        if (gcmSenderId != null) {
            return gcmSenderId;
        }
        gcmSenderId = this.zzclc.getOptions().getApplicationId();
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
    zza zzabM() {
        return this.zzcld.zzabP().zzu("", this.zzcle, "*");
    }

    String zzabN() throws IOException {
        return getToken(this.zzcle, "*");
    }

    zze zzabO() {
        return zzclb;
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        return this.zzcld.zzc(str, str2, bundle);
    }

    public void zzjt(String str) {
        zzclb.zzjt(str);
        FirebaseInstanceIdService.zzct(this.zzclc.getApplicationContext());
    }

    void zzju(String str) throws IOException {
        zza zzabM = zzabM();
        if (zzabM == null || zzabM.zzjB(zzd.zzbhM)) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        String str3 = zzabM.zzbxX;
        str2 = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        zzc(str3, valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2), bundle);
    }

    void zzjv(String str) throws IOException {
        zza zzabM = zzabM();
        if (zzabM == null || zzabM.zzjB(zzd.zzbhM)) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        zzd com_google_firebase_iid_zzd = this.zzcld;
        String str3 = zzabM.zzbxX;
        valueOf = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        com_google_firebase_iid_zzd.zzb(str3, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), bundle);
    }
}
