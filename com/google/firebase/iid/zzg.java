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

class zzg {
    SharedPreferences agw;
    Context zzahn;

    static class zza {
        private static final long bhE = TimeUnit.DAYS.toMillis(7);
        final String afY;
        final String auj;
        final long timestamp;

        private zza(String str, String str2, long j) {
            this.auj = str;
            this.afY = str2;
            this.timestamp = j;
        }

        static String zzc(String str, String str2, long j) {
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

        static zza zzty(String str) {
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

        boolean zztz(String str) {
            return System.currentTimeMillis() > this.timestamp + bhE || !str.equals(this.afY);
        }
    }

    public zzg(Context context) {
        this(context, "com.google.android.gms.appid");
    }

    public zzg(Context context, String str) {
        this.zzahn = context;
        this.agw = context.getSharedPreferences(str, 4);
        String valueOf = String.valueOf(str);
        String valueOf2 = String.valueOf("-no-backup");
        zzkq(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf));
    }

    private String zzbu(String str, String str2) {
        String valueOf = String.valueOf("|S|");
        return new StringBuilder(((String.valueOf(str).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()).append(str).append(valueOf).append(str2).toString();
    }

    private void zzkq(String str) {
        File file = new File(zzx.getNoBackupFilesDir(this.zzahn), str);
        if (!file.exists()) {
            try {
                if (file.createNewFile() && !isEmpty()) {
                    Log.i("InstanceID/Store", "App restored, clearing state");
                    FirebaseInstanceId.zza(this.zzahn, this);
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

    private void zzkr(String str) {
        Editor edit = this.agw.edit();
        for (String str2 : this.agw.getAll().keySet()) {
            if (str2.startsWith(str)) {
                edit.remove(str2);
            }
        }
        edit.commit();
    }

    private String zzp(String str, String str2, String str3) {
        String valueOf = String.valueOf("|T|");
        return new StringBuilder((((String.valueOf(str).length() + 1) + String.valueOf(valueOf).length()) + String.valueOf(str2).length()) + String.valueOf(str3).length()).append(str).append(valueOf).append(str2).append("|").append(str3).toString();
    }

    public SharedPreferences K() {
        return this.agw;
    }

    public synchronized boolean isEmpty() {
        return this.agw.getAll().isEmpty();
    }

    public synchronized void zza(String str, String str2, String str3, String str4, String str5) {
        String zzc = zza.zzc(str4, str5, System.currentTimeMillis());
        if (zzc != null) {
            Editor edit = this.agw.edit();
            edit.putString(zzp(str, str2, str3), zzc);
            edit.commit();
        }
    }

    public synchronized void zzbow() {
        this.agw.edit().clear().commit();
    }

    public synchronized void zzi(String str, String str2, String str3) {
        String zzp = zzp(str, str2, str3);
        Editor edit = this.agw.edit();
        edit.remove(zzp);
        edit.commit();
    }

    public synchronized KeyPair zzks(String str) {
        KeyPair keyPair;
        Object e;
        String string = this.agw.getString(zzbu(str, "|P|"), null);
        String string2 = this.agw.getString(zzbu(str, "|K|"), null);
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
                FirebaseInstanceId.zza(this.zzahn, this);
                keyPair = null;
                return keyPair;
            } catch (NoSuchAlgorithmException e3) {
                e = e3;
                string = String.valueOf(e);
                Log.w("InstanceID/Store", new StringBuilder(String.valueOf(string).length() + 19).append("Invalid key stored ").append(string).toString());
                FirebaseInstanceId.zza(this.zzahn, this);
                keyPair = null;
                return keyPair;
            }
        }
        return keyPair;
    }

    synchronized void zzkt(String str) {
        zzkr(String.valueOf(str).concat("|"));
    }

    public synchronized void zzku(String str) {
        zzkr(String.valueOf(str).concat("|T|"));
    }

    public synchronized zza zzq(String str, String str2, String str3) {
        return zza.zzty(this.agw.getString(zzp(str, str2, str3), null));
    }

    public synchronized long zztw(String str) {
        long parseLong;
        String string = this.agw.getString(zzbu(str, "cre"), null);
        if (string != null) {
            try {
                parseLong = Long.parseLong(string);
            } catch (NumberFormatException e) {
            }
        }
        parseLong = 0;
        return parseLong;
    }

    synchronized KeyPair zztx(String str) {
        KeyPair zzboo;
        zzboo = zza.zzboo();
        long currentTimeMillis = System.currentTimeMillis();
        Editor edit = this.agw.edit();
        edit.putString(zzbu(str, "|P|"), FirebaseInstanceId.zzu(zzboo.getPublic().getEncoded()));
        edit.putString(zzbu(str, "|K|"), FirebaseInstanceId.zzu(zzboo.getPrivate().getEncoded()));
        edit.putString(zzbu(str, "cre"), Long.toString(currentTimeMillis));
        edit.commit();
        return zzboo;
    }
}
