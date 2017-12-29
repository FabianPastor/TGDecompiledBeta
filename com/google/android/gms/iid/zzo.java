package com.google.android.gms.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzv;
import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class zzo {
    private Context zzair;
    private SharedPreferences zzige;

    public zzo(Context context) {
        this(context, "com.google.android.gms.appid");
    }

    private zzo(Context context, String str) {
        this.zzair = context;
        this.zzige = context.getSharedPreferences(str, 0);
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("-no-backup");
        File file = new File(zzv.getNoBackupFilesDir(this.zzair), valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("InstanceID/Store", "App restored, clearing state");
                    InstanceIDListenerService.zza(this.zzair, this);
                }
            } catch (IOException e) {
                if (Log.isLoggable("InstanceID/Store", 3)) {
                    valueOf = "InstanceID/Store";
                    String str2 = "Error creating file in no backup dir: ";
                    valueOf2 = String.valueOf(e.getMessage());
                    Log.d(valueOf, valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2));
                }
            }
        }
    }

    private final synchronized void zza(Editor editor, String str, String str2, String str3) {
        String str4 = "|S|";
        editor.putString(new StringBuilder((String.valueOf(str).length() + String.valueOf(str4).length()) + String.valueOf(str2).length()).append(str).append(str4).append(str2).toString(), str3);
    }

    private static String zzd(String str, String str2, String str3) {
        String str4 = "|T|";
        return new StringBuilder((((String.valueOf(str).length() + 1) + String.valueOf(str4).length()) + String.valueOf(str2).length()) + String.valueOf(str3).length()).append(str).append(str4).append(str2).append("|").append(str3).toString();
    }

    final synchronized String get(String str) {
        return this.zzige.getString(str, null);
    }

    final synchronized String get(String str, String str2) {
        String str3;
        str3 = "|S|";
        return this.zzige.getString(new StringBuilder((String.valueOf(str).length() + String.valueOf(str3).length()) + String.valueOf(str2).length()).append(str).append(str3).append(str2).toString(), null);
    }

    public final boolean isEmpty() {
        return this.zzige.getAll().isEmpty();
    }

    public final synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzd = zzd(str, str2, str3);
        Editor edit = this.zzige.edit();
        edit.putString(zzd, str4);
        edit.putString("appVersion", str5);
        edit.putString("lastToken", Long.toString(System.currentTimeMillis() / 1000));
        edit.commit();
    }

    public final synchronized void zzavj() {
        this.zzige.edit().clear().commit();
    }

    final synchronized KeyPair zzc(String str, long j) {
        KeyPair zzavc;
        zzavc = zza.zzavc();
        Editor edit = this.zzige.edit();
        zza(edit, str, "|P|", InstanceID.zzo(zzavc.getPublic().getEncoded()));
        zza(edit, str, "|K|", InstanceID.zzo(zzavc.getPrivate().getEncoded()));
        zza(edit, str, "cre", Long.toString(j));
        edit.commit();
        return zzavc;
    }

    public final synchronized String zze(String str, String str2, String str3) {
        return this.zzige.getString(zzd(str, str2, str3), null);
    }

    public final synchronized void zzhz(String str) {
        Editor edit = this.zzige.edit();
        for (String str2 : this.zzige.getAll().keySet()) {
            if (str2.startsWith(str)) {
                edit.remove(str2);
            }
        }
        edit.commit();
    }

    public final void zzia(String str) {
        zzhz(String.valueOf(str).concat("|T|"));
    }

    final KeyPair zzib(String str) {
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
            InstanceIDListenerService.zza(this.zzair, this);
            return null;
        } catch (NoSuchAlgorithmException e3) {
            e = e3;
            str2 = String.valueOf(e);
            Log.w("InstanceID/Store", new StringBuilder(String.valueOf(str2).length() + 19).append("Invalid key stored ").append(str2).toString());
            InstanceIDListenerService.zza(this.zzair, this);
            return null;
        }
    }
}
