package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzzz;
import java.util.ArrayList;

public class zzb extends Exception {
    private final ArrayMap<zzzz<?>, ConnectionResult> zzayL;

    public zzb(ArrayMap<zzzz<?>, ConnectionResult> arrayMap) {
        this.zzayL = arrayMap;
    }

    public String getMessage() {
        Iterable arrayList = new ArrayList();
        Object obj = 1;
        for (zzzz com_google_android_gms_internal_zzzz : this.zzayL.keySet()) {
            ConnectionResult connectionResult = (ConnectionResult) this.zzayL.get(com_google_android_gms_internal_zzzz);
            if (connectionResult.isSuccess()) {
                obj = null;
            }
            String valueOf = String.valueOf(com_google_android_gms_internal_zzzz.zzvw());
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

    public ConnectionResult zza(zzc<? extends ApiOptions> com_google_android_gms_common_api_zzc__extends_com_google_android_gms_common_api_Api_ApiOptions) {
        zzzz apiKey = com_google_android_gms_common_api_zzc__extends_com_google_android_gms_common_api_Api_ApiOptions.getApiKey();
        zzac.zzb(this.zzayL.get(apiKey) != null, (Object) "The given API was not part of the availability request.");
        return (ConnectionResult) this.zzayL.get(apiKey);
    }

    public ArrayMap<zzzz<?>, ConnectionResult> zzvj() {
        return this.zzayL;
    }
}
