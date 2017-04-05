package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.common.util.zzt;
import com.google.firebase.iid.zzb.zzb;

public final class FirebaseInstanceIdInternalReceiver extends WakefulBroadcastReceiver {
    private static boolean zzbgs = false;
    private zzb zzcll;
    private zzb zzclm;

    private zzb zzK(Context context, String str) {
        if ("com.google.firebase.MESSAGING_EVENT".equals(str)) {
            if (this.zzclm == null) {
                this.zzclm = new zzb(context, str);
            }
            return this.zzclm;
        }
        if (this.zzcll == null) {
            this.zzcll = new zzb(context, str);
        }
        return this.zzcll;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Parcelable parcelableExtra = intent.getParcelableExtra("wrapped_intent");
            if (parcelableExtra instanceof Intent) {
                Intent intent2 = (Intent) parcelableExtra;
                if (zzt.zzzq()) {
                    zzK(context, intent.getAction()).zzb(intent2, goAsync());
                    return;
                } else {
                    zzg.zzabU().zzb(context, intent.getAction(), intent2);
                    return;
                }
            }
            Log.e("FirebaseInstanceId", "Missing or invalid wrapped intent");
        }
    }
}
