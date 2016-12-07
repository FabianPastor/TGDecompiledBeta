package com.google.android.gms.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzw;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class zzd {
    SharedPreferences aiG;
    Context zzahs;

    public zzd(Context context) {
        this(context, "com.google.android.gms.appid");
    }

    public zzd(Context context, String str) {
        this.zzahs = context;
        this.aiG = context.getSharedPreferences(str, 4);
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("-no-backup");
        zzkq(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
    }

    private String zzg(String str, String str2, String str3) {
        String valueOf = String.valueOf("|T|");
        return new StringBuilder((((String.valueOf(str).length() + 1) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()) + String.valueOf(str3).length()).append(str).append(valueOf).append(str2).append("|").append(str3).toString();
    }

    private void zzkq(String str) {
        File file = new File(zzw.getNoBackupFilesDir(this.zzahs), str);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("InstanceID/Store", "App restored, clearing state");
                    InstanceIDListenerService.zza(this.zzahs, this);
                }
            } catch (IOException e) {
                if (Log.isLoggable("InstanceID/Store", 3)) {
                    String str2 = "InstanceID/Store";
                    String str3 = "Error creating file in no backup dir: ";
                    String valueOf = String.valueOf(e.getMessage());
                    Log.d(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                }
            }
        }
    }

    synchronized String get(String str) {
        return this.aiG.getString(str, null);
    }

    synchronized String get(String str, String str2) {
        SharedPreferences sharedPreferences;
        String valueOf;
        sharedPreferences = this.aiG;
        valueOf = String.valueOf("|S|");
        return sharedPreferences.getString(new StringBuilder(((String.valueOf(str).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString(), null);
    }

    public boolean isEmpty() {
        return this.aiG.getAll().isEmpty();
    }

    synchronized void zza(Editor editor, String str, String str2, String str3) {
        String valueOf = String.valueOf("|S|");
        editor.putString(new StringBuilder(((String.valueOf(str).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString(), str3);
    }

    public synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzg = zzg(str, str2, str3);
        Editor edit = this.aiG.edit();
        edit.putString(zzg, str4);
        edit.putString("appVersion", str5);
        edit.putString("lastToken", Long.toString(System.currentTimeMillis() / 1000));
        edit.commit();
    }

    public synchronized void zzbop() {
        this.aiG.edit().clear().commit();
    }

    synchronized KeyPair zze(String str, long j) {
        KeyPair zzboh;
        zzboh = zza.zzboh();
        Editor edit = this.aiG.edit();
        zza(edit, str, "|P|", InstanceID.zzv(zzboh.getPublic().getEncoded()));
        zza(edit, str, "|K|", InstanceID.zzv(zzboh.getPrivate().getEncoded()));
        zza(edit, str, "cre", Long.toString(j));
        edit.commit();
        return zzboh;
    }

    public synchronized String zzh(String str, String str2, String str3) {
        return this.aiG.getString(zzg(str, str2, str3), null);
    }

    public synchronized void zzi(String str, String str2, String str3) {
        String zzg = zzg(str, str2, str3);
        Editor edit = this.aiG.edit();
        edit.remove(zzg);
        edit.commit();
    }

    public synchronized void zzkr(String str) {
        Editor edit = this.aiG.edit();
        for (String str2 : this.aiG.getAll().keySet()) {
            if (str2.startsWith(str)) {
                edit.remove(str2);
            }
        }
        edit.commit();
    }

    public KeyPair zzks(String str) {
        return zzkv(str);
    }

    void zzkt(String str) {
        zzkr(String.valueOf(str).concat("|"));
    }

    public void zzku(String str) {
        zzkr(String.valueOf(str).concat("|T|"));
    }

    KeyPair zzkv(String str) {
        Object e;
        String str2 = get(str, "|P|");
        String str3 = get(str, "|K|");
        if (str2 == null || str3 == null) {
            return null;
        }
        try {
            byte[] decode = Base64.decode(str2, 8);
            byte[] decode2 = Base64.decode(str3, 8);
            KeyFactory instance = KeyFactory.getInstance("RSA");
            return new KeyPair(instance.generatePublic(new X509EncodedKeySpec(decode)), instance.generatePrivate(new PKCS8EncodedKeySpec(decode2)));
        } catch (InvalidKeySpecException e2) {
            e = e2;
            str2 = String.valueOf(e);
            Log.w("InstanceID/Store", new StringBuilder(String.valueOf(str2).length() + 19).append("Invalid key stored ").append(str2).toString());
            InstanceIDListenerService.zza(this.zzahs, this);
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e = e3;
            str2 = String.valueOf(e);
            Log.w("InstanceID/Store", new StringBuilder(String.valueOf(str2).length() + 19).append("Invalid key stored ").append(str2).toString());
            InstanceIDListenerService.zza(this.zzahs, this);
            return null;
        }
    }
}
