package com.google.firebase.iid;

import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class zzk {
    private static final Object zzuF = new Object();
    private final zzr zzckH;

    zzk(zzr com_google_firebase_iid_zzr) {
        this.zzckH = com_google_firebase_iid_zzr;
    }

    @Nullable
    final String zzJV() {
        String str = null;
        synchronized (zzuF) {
            String string = this.zzckH.zzbho.getString("topic_operaion_queue", null);
            if (string != null) {
                String[] split = string.split(",");
                if (split.length > 1 && !TextUtils.isEmpty(split[1])) {
                    str = split[1];
                }
            }
        }
        return str;
    }

    final void zzhf(String str) {
        synchronized (zzuF) {
            String string = this.zzckH.zzbho.getString("topic_operaion_queue", "");
            String valueOf = String.valueOf(",");
            this.zzckH.zzbho.edit().putString("topic_operaion_queue", new StringBuilder((String.valueOf(string).length() + String.valueOf(valueOf).length()) + String.valueOf(str).length()).append(string).append(valueOf).append(str).toString()).apply();
        }
    }

    final boolean zzhj(String str) {
        boolean z;
        synchronized (zzuF) {
            String string = this.zzckH.zzbho.getString("topic_operaion_queue", "");
            String valueOf = String.valueOf(",");
            String valueOf2 = String.valueOf(str);
            if (string.startsWith(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf))) {
                valueOf = String.valueOf(",");
                valueOf2 = String.valueOf(str);
                this.zzckH.zzbho.edit().putString("topic_operaion_queue", string.substring((valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf)).length())).apply();
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }
}
