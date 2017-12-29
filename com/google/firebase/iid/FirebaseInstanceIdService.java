package com.google.firebase.iid;

import android.content.Intent;
import android.util.Log;

public class FirebaseInstanceIdService extends zzb {
    public final void handleIntent(Intent intent) {
        if ("com.google.firebase.iid.TOKEN_REFRESH".equals(intent.getAction())) {
            onTokenRefresh();
            return;
        }
        String stringExtra = intent.getStringExtra("CMD");
        if (stringExtra != null) {
            if (Log.isLoggable("FirebaseInstanceId", 3)) {
                String valueOf = String.valueOf(intent.getExtras());
                Log.d("FirebaseInstanceId", new StringBuilder((String.valueOf(stringExtra).length() + 21) + String.valueOf(valueOf).length()).append("Received command: ").append(stringExtra).append(" - ").append(valueOf).toString());
            }
            if ("RST".equals(stringExtra) || "RST_FULL".equals(stringExtra)) {
                FirebaseInstanceId.getInstance().zzciy();
            } else if ("SYNC".equals(stringExtra)) {
                FirebaseInstanceId.getInstance().zzciz();
            }
        }
    }

    public void onTokenRefresh() {
    }

    protected final Intent zzp(Intent intent) {
        return (Intent) zzx.zzcjk().zznzs.poll();
    }
}
