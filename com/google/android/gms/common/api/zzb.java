package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.internal.zzzs;
import java.util.ArrayList;

public class zzb extends Exception {
    private final ArrayMap<zzzs<?>, ConnectionResult> zzaxy;

    public zzb(ArrayMap<zzzs<?>, ConnectionResult> arrayMap) {
        this.zzaxy = arrayMap;
    }

    public String getMessage() {
        Iterable arrayList = new ArrayList();
        Object obj = 1;
        for (zzzs com_google_android_gms_internal_zzzs : this.zzaxy.keySet()) {
            ConnectionResult connectionResult = (ConnectionResult) this.zzaxy.get(com_google_android_gms_internal_zzzs);
            if (connectionResult.isSuccess()) {
                obj = null;
            }
            String valueOf = String.valueOf(com_google_android_gms_internal_zzzs.zzuV());
            String valueOf2 = String.valueOf(connectionResult);
            arrayList.add(new StringBuilder((String.valueOf(valueOf).length() + 2) + String.valueOf(valueOf2).length()).append(valueOf).append(": ").append(valueOf2).toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (obj != null) {
            stringBuilder.append("None of the queried APIs are available. ");
        } else {
            stringBuilder.append("Some of the queried APIs are unavailable. ");
        }
        stringBuilder.append(TextUtils.join("; ", arrayList));
        return stringBuilder.toString();
    }

    public ArrayMap<zzzs<?>, ConnectionResult> zzuK() {
        return this.zzaxy;
    }
}
