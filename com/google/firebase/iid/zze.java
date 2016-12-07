package com.google.firebase.iid;

import android.support.annotation.Nullable;
import android.text.TextUtils;

public class zze {
    private static final Object zzaox = new Object();
    private final zzg bkO;

    zze(zzg com_google_firebase_iid_zzg) {
        this.bkO = com_google_firebase_iid_zzg;
    }

    @Nullable
    String L() {
        String str = null;
        synchronized (zzaox) {
            String string = this.bkO.M().getString("topic_operaion_queue", null);
            if (string != null) {
                String[] split = string.split(",");
                if (split.length > 1 && !TextUtils.isEmpty(split[1])) {
                    str = split[1];
                }
            }
        }
        return str;
    }

    void zztq(String str) {
        synchronized (zzaox) {
            String string = this.bkO.M().getString("topic_operaion_queue", "");
            String valueOf = String.valueOf(",");
            this.bkO.M().edit().putString("topic_operaion_queue", new StringBuilder(((String.valueOf(string).length() + 0) + String.valueOf(valueOf).length()) + String.valueOf(str).length()).append(string).append(valueOf).append(str).toString()).apply();
        }
    }

    boolean zztu(String str) {
        boolean z;
        synchronized (zzaox) {
            String string = this.bkO.M().getString("topic_operaion_queue", "");
            String valueOf = String.valueOf(",");
            String valueOf2 = String.valueOf(str);
            if (string.startsWith(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf))) {
                valueOf = String.valueOf(",");
                valueOf2 = String.valueOf(str);
                this.bkO.M().edit().putString("topic_operaion_queue", string.substring((valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf)).length())).apply();
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }
}
