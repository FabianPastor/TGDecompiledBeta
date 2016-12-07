package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzx;
import com.google.android.gms.internal.zzql;
import java.util.ArrayList;

public class zzb extends Exception {
    private final ArrayMap<zzql<?>, ConnectionResult> xo;

    public zzb(ArrayMap<zzql<?>, ConnectionResult> arrayMap) {
        this.xo = arrayMap;
    }

    public String getMessage() {
        Iterable arrayList = new ArrayList();
        Object obj = 1;
        for (zzql com_google_android_gms_internal_zzql : this.xo.keySet()) {
            ConnectionResult connectionResult = (ConnectionResult) this.xo.get(com_google_android_gms_internal_zzql);
            if (connectionResult.isSuccess()) {
                obj = null;
            }
            String valueOf = String.valueOf(com_google_android_gms_internal_zzql.zzarl());
            String valueOf2 = String.valueOf(connectionResult);
            arrayList.add(new StringBuilder((String.valueOf(valueOf).length() + 2) + String.valueOf(valueOf2).length()).append(valueOf).append(": ").append(valueOf2).toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (obj != null) {
            stringBuilder.append("None of the queried APIs are available. ");
        } else {
            stringBuilder.append("Some of the queried APIs are unavailable. ");
        }
        zzx.zzia("; ").zza(stringBuilder, arrayList);
        return stringBuilder.toString();
    }

    public ArrayMap<zzql<?>, ConnectionResult> zzara() {
        return this.xo;
    }
}
