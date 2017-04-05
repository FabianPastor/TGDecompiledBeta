package com.google.android.gms.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzx;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class zzd {
    SharedPreferences zzbif;
    Context zzqn;

    public zzd(Context context) {
        this(context, "com.google.android.gms.appid");
    }

    public zzd(Context context, String str) {
        this.zzqn = context;
        this.zzbif = context.getSharedPreferences(str, 0);
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("-no-backup");
        zzeG(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
    }

    private void zzeG(String str) {
        File file = new File(zzx.getNoBackupFilesDir(this.zzqn), str);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("InstanceID/Store", "App restored, clearing state");
                    InstanceIDListenerService.zza(this.zzqn, this);
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

    private String zzg(String str, String str2, String str3) {
        String valueOf = String.valueOf("|T|");
        return new StringBuilder((((String.valueOf(str).length() + 1) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()) + String.valueOf(str3).length()).append(str).append(valueOf).append(str2).append("|").append(str3).toString();
    }

    synchronized String get(String str) {
        return this.zzbif.getString(str, null);
    }

    synchronized String get(String str, String str2) {
        SharedPreferences sharedPreferences;
        String valueOf;
        sharedPreferences = this.zzbif;
        valueOf = String.valueOf("|S|");
        return sharedPreferences.getString(new StringBuilder((String.valueOf(str).length() + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString(), null);
    }

    public boolean isEmpty() {
        return this.zzbif.getAll().isEmpty();
    }

    public synchronized void zzHo() {
        this.zzbif.edit().clear().commit();
    }

    synchronized void zza(Editor editor, String str, String str2, String str3) {
        String valueOf = String.valueOf("|S|");
        editor.putString(new StringBuilder((String.valueOf(str).length() + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString(), str3);
    }

    public synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzg = zzg(str, str2, str3);
        Editor edit = this.zzbif.edit();
        edit.putString(zzg, str4);
        edit.putString("appVersion", str5);
        edit.putString("lastToken", Long.toString(System.currentTimeMillis() / 1000));
        edit.commit();
    }

    synchronized KeyPair zze(String str, long j) {
        KeyPair zzHg;
        zzHg = zza.zzHg();
        Editor edit = this.zzbif.edit();
        zza(edit, str, "|P|", InstanceID.zzv(zzHg.getPublic().getEncoded()));
        zza(edit, str, "|K|", InstanceID.zzv(zzHg.getPrivate().getEncoded()));
        zza(edit, str, "cre", Long.toString(j));
        edit.commit();
        return zzHg;
    }

    public synchronized void zzeH(String str) {
        Editor edit = this.zzbif.edit();
        for (String str2 : this.zzbif.getAll().keySet()) {
            if (str2.startsWith(str)) {
                edit.remove(str2);
            }
        }
        edit.commit();
    }

    public KeyPair zzeI(String str) {
        return zzeL(str);
    }

    void zzeJ(String str) {
        zzeH(String.valueOf(str).concat("|"));
    }

    public void zzeK(String str) {
        zzeH(String.valueOf(str).concat("|T|"));
    }

    KeyPair zzeL(String str) {
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
            InstanceIDListenerService.zza(this.zzqn, this);
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e = e3;
            str2 = String.valueOf(e);
            Log.w("InstanceID/Store", new StringBuilder(String.valueOf(str2).length() + 19).append("Invalid key stored ").append(str2).toString());
            InstanceIDListenerService.zza(this.zzqn, this);
            return null;
        }
    }

    public synchronized String zzh(String str, String str2, String str3) {
        return this.zzbif.getString(zzg(str, str2, str3), null);
    }

    public synchronized void zzi(String str, String str2, String str3) {
        String zzg = zzg(str, str2, str3);
        Editor edit = this.zzbif.edit();
        edit.remove(zzg);
        edit.commit();
    }
}
