package com.google.firebase.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
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

final class zzy {
    private Context zzair;
    private SharedPreferences zzige;

    public zzy(Context context) {
        this(context, "com.google.android.gms.appid");
    }

    private zzy(Context context, String str) {
        this.zzair = context;
        this.zzige = context.getSharedPreferences(str, 0);
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("-no-backup");
        File file = new File(zzv.getNoBackupFilesDir(this.zzair), valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("FirebaseInstanceId", "App restored, clearing state");
                    zzavj();
                    FirebaseInstanceId.getInstance().zzciy();
                }
            } catch (IOException e) {
                if (Log.isLoggable("FirebaseInstanceId", 3)) {
                    valueOf = "FirebaseInstanceId";
                    String str2 = "Error creating file in no backup dir: ";
                    valueOf2 = String.valueOf(e.getMessage());
                    Log.d(valueOf, valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2));
                }
            }
        }
    }

    private final synchronized boolean isEmpty() {
        return this.zzige.getAll().isEmpty();
    }

    private static String zzbm(String str, String str2) {
        String str3 = "|S|";
        return new StringBuilder((String.valueOf(str).length() + String.valueOf(str3).length()) + String.valueOf(str2).length()).append(str).append(str3).append(str2).toString();
    }

    private final void zzhz(String str) {
        Editor edit = this.zzige.edit();
        for (String str2 : this.zzige.getAll().keySet()) {
            if (str2.startsWith(str)) {
                edit.remove(str2);
            }
        }
        edit.commit();
    }

    private static String zzo(String str, String str2, String str3) {
        String str4 = "|T|";
        return new StringBuilder((((String.valueOf(str).length() + 1) + String.valueOf(str4).length()) + String.valueOf(str2).length()) + String.valueOf(str3).length()).append(str).append(str4).append(str2).append("|").append(str3).toString();
    }

    public final synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzc = zzz.zzc(str4, str5, System.currentTimeMillis());
        if (zzc != null) {
            Editor edit = this.zzige.edit();
            edit.putString(zzo(str, str2, str3), zzc);
            edit.commit();
        }
    }

    public final synchronized void zzavj() {
        this.zzige.edit().clear().commit();
    }

    public final synchronized String zzcjm() {
        String str = null;
        synchronized (this) {
            String string = this.zzige.getString("topic_operaion_queue", null);
            if (string != null) {
                String[] split = string.split(",");
                if (split.length > 1 && !TextUtils.isEmpty(split[1])) {
                    str = split[1];
                }
            }
        }
        return str;
    }

    public final synchronized void zzia(String str) {
        zzhz(String.valueOf(str).concat("|T|"));
    }

    public final synchronized zzz zzp(String str, String str2, String str3) {
        return zzz.zzrn(this.zzige.getString(zzo(str, str2, str3), null));
    }

    public final synchronized boolean zzri(String str) {
        boolean z;
        String string = this.zzige.getString("topic_operaion_queue", TtmlNode.ANONYMOUS_REGION_ID);
        String valueOf = String.valueOf(",");
        String valueOf2 = String.valueOf(str);
        if (string.startsWith(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf))) {
            valueOf = String.valueOf(",");
            valueOf2 = String.valueOf(str);
            this.zzige.edit().putString("topic_operaion_queue", string.substring((valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf)).length())).apply();
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    final synchronized KeyPair zzrk(String str) {
        KeyPair zzavc;
        zzavc = zza.zzavc();
        long currentTimeMillis = System.currentTimeMillis();
        Editor edit = this.zzige.edit();
        edit.putString(zzbm(str, "|P|"), Base64.encodeToString(zzavc.getPublic().getEncoded(), 11));
        edit.putString(zzbm(str, "|K|"), Base64.encodeToString(zzavc.getPrivate().getEncoded(), 11));
        edit.putString(zzbm(str, "cre"), Long.toString(currentTimeMillis));
        edit.commit();
        return zzavc;
    }

    final synchronized void zzrl(String str) {
        zzhz(String.valueOf(str).concat("|"));
    }

    public final synchronized KeyPair zzrm(String str) {
        KeyPair keyPair;
        Object e;
        String string = this.zzige.getString(zzbm(str, "|P|"), null);
        String string2 = this.zzige.getString(zzbm(str, "|K|"), null);
        if (string == null || string2 == null) {
            keyPair = null;
        } else {
            try {
                byte[] decode = Base64.decode(string, 8);
                byte[] decode2 = Base64.decode(string2, 8);
                KeyFactory instance = KeyFactory.getInstance("RSA");
                keyPair = new KeyPair(instance.generatePublic(new X509EncodedKeySpec(decode)), instance.generatePrivate(new PKCS8EncodedKeySpec(decode2)));
            } catch (InvalidKeySpecException e2) {
                e = e2;
                string = String.valueOf(e);
                Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(string).length() + 19).append("Invalid key stored ").append(string).toString());
                FirebaseInstanceId.getInstance().zzciy();
                keyPair = null;
                return keyPair;
            } catch (NoSuchAlgorithmException e3) {
                e = e3;
                string = String.valueOf(e);
                Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(string).length() + 19).append("Invalid key stored ").append(string).toString());
                FirebaseInstanceId.getInstance().zzciy();
                keyPair = null;
                return keyPair;
            }
        }
        return keyPair;
    }
}
