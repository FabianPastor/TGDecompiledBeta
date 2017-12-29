package com.google.firebase.iid;

import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

final class zzz {
    private static final long zznzu = TimeUnit.DAYS.toMillis(7);
    private long timestamp;
    private String zzifm;
    final String zzldj;

    private zzz(String str, String str2, long j) {
        this.zzldj = str;
        this.zzifm = str2;
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
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 24).append("Failed to encode token: ").append(valueOf).toString());
            return null;
        }
    }

    static zzz zzrn(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (!str.startsWith("{")) {
            return new zzz(str, null, 0);
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            return new zzz(jSONObject.getString("token"), jSONObject.getString("appVersion"), jSONObject.getLong("timestamp"));
        } catch (JSONException e) {
            String valueOf = String.valueOf(e);
            Log.w("FirebaseInstanceId", new StringBuilder(String.valueOf(valueOf).length() + 23).append("Failed to parse token: ").append(valueOf).toString());
            return null;
        }
    }

    final boolean zzro(String str) {
        return System.currentTimeMillis() > this.timestamp + zznzu || !str.equals(this.zzifm);
    }
}
