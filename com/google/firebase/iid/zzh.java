package com.google.firebase.iid;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
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
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

class zzh {
    SharedPreferences zzbij;
    Context zzqn;

    static class zza {
        private static final long zzclx = TimeUnit.DAYS.toMillis(7);
        final long timestamp;
        final String zzbhM;
        final String zzbxX;

        private zza(String str, String str2, long j) {
            this.zzbxX = str;
            this.zzbhM = str2;
            this.timestamp = j;
        }

        static String zzd(String str, String str2, long j) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("token", str);
                jSONObject.put("appVersion", str2);
                jSONObject.put("timestamp", j);
                return jSONObject.toString();
            } catch (JSONException e) {
                String valueOf = String.valueOf(e);
                Log.w("InstanceID/Store", new StringBuilder(String.valueOf(valueOf).length() + 24).append("Failed to encode token: ").append(valueOf).toString());
                return null;
            }
        }

        static zza zzjA(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            if (!str.startsWith("{")) {
                return new zza(str, null, 0);
            }
            try {
                JSONObject jSONObject = new JSONObject(str);
                return new zza(jSONObject.getString("token"), jSONObject.getString("appVersion"), jSONObject.getLong("timestamp"));
            } catch (JSONException e) {
                String valueOf = String.valueOf(e);
                Log.w("InstanceID/Store", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Failed to parse token: ").append(valueOf).toString());
                return null;
            }
        }

        boolean zzjB(String str) {
            return System.currentTimeMillis() > this.timestamp + zzclx || !str.equals(this.zzbhM);
        }
    }

    public zzh(Context context) {
        this(context, "com.google.android.gms.appid");
    }

    public zzh(Context context, String str) {
        this.zzqn = context;
        this.zzbij = context.getSharedPreferences(str, 0);
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("-no-backup");
        zzeG(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
    }

    private String zzaz(String str, String str2) {
        String valueOf = String.valueOf("|S|");
        return new StringBuilder((String.valueOf(str).length() + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    private void zzeG(String str) {
        File file = new File(zzx.getNoBackupFilesDir(this.zzqn), str);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("InstanceID/Store", "App restored, clearing state");
                    FirebaseInstanceId.zza(this.zzqn, this);
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

    private void zzeH(String str) {
        Editor edit = this.zzbij.edit();
        for (String str2 : this.zzbij.getAll().keySet()) {
            if (str2.startsWith(str)) {
                edit.remove(str2);
            }
        }
        edit.commit();
    }

    private String zzt(String str, String str2, String str3) {
        String valueOf = String.valueOf("|T|");
        return new StringBuilder((((String.valueOf(str).length() + 1) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()) + String.valueOf(str3).length()).append(str).append(valueOf).append(str2).append("|").append(str3).toString();
    }

    public synchronized boolean isEmpty() {
        return this.zzbij.getAll().isEmpty();
    }

    public synchronized void zzHn() {
        this.zzbij.edit().clear().commit();
    }

    public synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzd = zza.zzd(str4, str5, System.currentTimeMillis());
        if (zzd != null) {
            Editor edit = this.zzbij.edit();
            edit.putString(zzt(str, str2, str3), zzd);
            edit.commit();
        }
    }

    public SharedPreferences zzabW() {
        return this.zzbij;
    }

    public synchronized KeyPair zzeI(String str) {
        KeyPair keyPair;
        Object e;
        String string = this.zzbij.getString(zzaz(str, "|P|"), null);
        String string2 = this.zzbij.getString(zzaz(str, "|K|"), null);
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
                Log.w("InstanceID/Store", new StringBuilder(String.valueOf(string).length() + 19).append("Invalid key stored ").append(string).toString());
                FirebaseInstanceId.zza(this.zzqn, this);
                keyPair = null;
                return keyPair;
            } catch (NoSuchAlgorithmException e3) {
                e = e3;
                string = String.valueOf(e);
                Log.w("InstanceID/Store", new StringBuilder(String.valueOf(string).length() + 19).append("Invalid key stored ").append(string).toString());
                FirebaseInstanceId.zza(this.zzqn, this);
                keyPair = null;
                return keyPair;
            }
        }
        return keyPair;
    }

    synchronized void zzeJ(String str) {
        zzeH(String.valueOf(str).concat("|"));
    }

    public synchronized void zzeK(String str) {
        zzeH(String.valueOf(str).concat("|T|"));
    }

    public synchronized void zzi(String str, String str2, String str3) {
        String zzt = zzt(str, str2, str3);
        Editor edit = this.zzbij.edit();
        edit.remove(zzt);
        edit.commit();
    }

    public synchronized long zzjy(String str) {
        long parseLong;
        String string = this.zzbij.getString(zzaz(str, "cre"), null);
        if (string != null) {
            try {
                parseLong = Long.parseLong(string);
            } catch (NumberFormatException e) {
            }
        }
        parseLong = 0;
        return parseLong;
    }

    synchronized KeyPair zzjz(String str) {
        KeyPair zzHf;
        zzHf = zza.zzHf();
        long currentTimeMillis = System.currentTimeMillis();
        Editor edit = this.zzbij.edit();
        edit.putString(zzaz(str, "|P|"), FirebaseInstanceId.zzv(zzHf.getPublic().getEncoded()));
        edit.putString(zzaz(str, "|K|"), FirebaseInstanceId.zzv(zzHf.getPrivate().getEncoded()));
        edit.putString(zzaz(str, "cre"), Long.toString(currentTimeMillis));
        edit.commit();
        return zzHf;
    }

    public synchronized zza zzu(String str, String str2, String str3) {
        return zza.zzjA(this.zzbij.getString(zzt(str, str2, str3), null));
    }
}
