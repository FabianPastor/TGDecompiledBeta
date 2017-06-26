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
    private static Map<String, FirebaseInstanceId> zzbgQ = new ArrayMap();
    private static zzk zzckr;
    private final FirebaseApp zzcks;
    private final zzj zzckt;
    private final String zzcku;

    private FirebaseInstanceId(FirebaseApp firebaseApp, zzj com_google_firebase_iid_zzj) {
        this.zzcks = firebaseApp;
        this.zzckt = com_google_firebase_iid_zzj;
        String gcmSenderId = this.zzcks.getOptions().getGcmSenderId();
        if (gcmSenderId == null) {
            gcmSenderId = this.zzcks.getOptions().getApplicationId();
            if (gcmSenderId.startsWith("1:")) {
                String[] split = gcmSenderId.split(":");
                if (split.length < 2) {
                    gcmSenderId = null;
                } else {
                    gcmSenderId = split[1];
                    if (gcmSenderId.isEmpty()) {
                        gcmSenderId = null;
                    }
                }
            }
        }
        this.zzcku = gcmSenderId;
        if (this.zzcku == null) {
            throw new IllegalStateException("IID failing to initialize, FirebaseApp is missing project ID");
        }
        FirebaseInstanceIdService.zza(this.zzcks.getApplicationContext(), this);
    }

    public static FirebaseInstanceId getInstance() {
        return getInstance(FirebaseApp.getInstance());
    }

    @Keep
    public static synchronized FirebaseInstanceId getInstance(@NonNull FirebaseApp firebaseApp) {
        FirebaseInstanceId firebaseInstanceId;
        synchronized (FirebaseInstanceId.class) {
            firebaseInstanceId = (FirebaseInstanceId) zzbgQ.get(firebaseApp.getOptions().getApplicationId());
            if (firebaseInstanceId == null) {
                zzj zzb = zzj.zzb(firebaseApp.getApplicationContext(), null);
                if (zzckr == null) {
                    zzckr = new zzk(zzj.zzJQ());
                }
                firebaseInstanceId = new FirebaseInstanceId(firebaseApp, zzb);
                zzbgQ.put(firebaseApp.getOptions().getApplicationId(), firebaseInstanceId);
            }
        }
        return firebaseInstanceId;
    }

    private final void zzF(Bundle bundle) {
        bundle.putString("gmp_app_id", this.zzcks.getOptions().getApplicationId());
    }

    static zzk zzJP() {
        return zzckr;
    }

    static int zzO(Context context, String str) {
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
            digest[0] = (byte) ((digest[0] & 15) + 112);
            return Base64.encodeToString(digest, 0, 8, 11);
        } catch (NoSuchAlgorithmException e) {
            Log.w("FirebaseInstanceId", "Unexpected error, device missing required alghorithms");
            return null;
        }
    }

    static void zza(Context context, zzr com_google_firebase_iid_zzr) {
        com_google_firebase_iid_zzr.zzvP();
        Intent intent = new Intent();
        intent.putExtra("CMD", "RST");
        zzq.zzJU().zze(context, intent);
    }

    static int zzbF(Context context) {
        return zzO(context, context.getPackageName());
    }

    static void zzbG(Context context) {
        Intent intent = new Intent();
        intent.putExtra("CMD", "SYNC");
        zzq.zzJU().zze(context, intent);
    }

    static String zzbb(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static String zzj(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public void deleteInstanceId() throws IOException {
        this.zzckt.zzb("*", "*", null);
        this.zzckt.zzvL();
    }

    @WorkerThread
    public void deleteToken(String str, String str2) throws IOException {
        Bundle bundle = new Bundle();
        zzF(bundle);
        this.zzckt.zzb(str, str2, bundle);
    }

    public long getCreationTime() {
        return this.zzckt.getCreationTime();
    }

    public String getId() {
        return zza(this.zzckt.zzvK());
    }

    @Nullable
    public String getToken() {
        zzs zzJN = zzJN();
        if (zzJN == null || zzJN.zzhp(zzj.zzbgW)) {
            FirebaseInstanceIdService.zzbI(this.zzcks.getApplicationContext());
        }
        return zzJN != null ? zzJN.zzbPH : null;
    }

    @WorkerThread
    public String getToken(String str, String str2) throws IOException {
        Bundle bundle = new Bundle();
        zzF(bundle);
        return this.zzckt.getToken(str, str2, bundle);
    }

    @Nullable
    final zzs zzJN() {
        return zzj.zzJQ().zzp("", this.zzcku, "*");
    }

    final String zzJO() throws IOException {
        return getToken(this.zzcku, "*");
    }

    public final void zzhf(String str) {
        zzckr.zzhf(str);
        FirebaseInstanceIdService.zzbI(this.zzcks.getApplicationContext());
    }

    final void zzhg(String str) throws IOException {
        zzs zzJN = zzJN();
        if (zzJN == null || zzJN.zzhp(zzj.zzbgW)) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        String str3 = zzJN.zzbPH;
        str2 = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        valueOf2 = valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2);
        zzF(bundle);
        this.zzckt.zzc(str3, valueOf2, bundle);
    }

    final void zzhh(String str) throws IOException {
        zzs zzJN = zzJN();
        if (zzJN == null || zzJN.zzhp(zzj.zzbgW)) {
            throw new IOException("token not available");
        }
        Bundle bundle = new Bundle();
        String str2 = "gcm.topic";
        String valueOf = String.valueOf("/topics/");
        String valueOf2 = String.valueOf(str);
        bundle.putString(str2, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        zzj com_google_firebase_iid_zzj = this.zzckt;
        String str3 = zzJN.zzbPH;
        valueOf = String.valueOf("/topics/");
        valueOf2 = String.valueOf(str);
        com_google_firebase_iid_zzj.zzb(str3, valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf), bundle);
    }
}
