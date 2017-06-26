package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbas;
import java.util.ArrayList;

public final class zza extends Exception {
    private final ArrayMap<zzbas<?>, ConnectionResult> zzaAB;

    public zza(ArrayMap<zzbas<?>, ConnectionResult> arrayMap) {
        this.zzaAB = arrayMap;
    }

    public final String getMessage() {
        Iterable arrayList = new ArrayList();
        Object obj = 1;
        for (zzbas com_google_android_gms_internal_zzbas : this.zzaAB.keySet()) {
            ConnectionResult connectionResult = (ConnectionResult) this.zzaAB.get(com_google_android_gms_internal_zzbas);
            if (connectionResult.isSuccess()) {
                obj = null;
            }
            String valueOf = String.valueOf(com_google_android_gms_internal_zzbas.zzpr());
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

    public final ConnectionResult zza(GoogleApi<? extends ApiOptions> googleApi) {
        zzbas zzph = googleApi.zzph();
        zzbo.zzb(this.zzaAB.get(zzph) != null, (Object) "The given API was not part of the availability request.");
        return (ConnectionResult) this.zzaAB.get(zzph);
    }

    public final ArrayMap<zzbas<?>, ConnectionResult> zzpf() {
        return this.zzaAB;
    }
}
