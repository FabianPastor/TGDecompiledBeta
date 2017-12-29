package com.google.android.gms.common.api;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.internal.zzh;
import com.google.android.gms.common.internal.zzbq;
import java.util.ArrayList;

public class AvailabilityException extends Exception {
    private final ArrayMap<zzh<?>, ConnectionResult> zzflw;

    public AvailabilityException(ArrayMap<zzh<?>, ConnectionResult> arrayMap) {
        this.zzflw = arrayMap;
    }

    public ConnectionResult getConnectionResult(GoogleApi<? extends ApiOptions> googleApi) {
        zzh zzagn = googleApi.zzagn();
        zzbq.checkArgument(this.zzflw.get(zzagn) != null, "The given API was not part of the availability request.");
        return (ConnectionResult) this.zzflw.get(zzagn);
    }

    public String getMessage() {
        Iterable arrayList = new ArrayList();
        Object obj = 1;
        for (zzh com_google_android_gms_common_api_internal_zzh : this.zzflw.keySet()) {
            ConnectionResult connectionResult = (ConnectionResult) this.zzflw.get(com_google_android_gms_common_api_internal_zzh);
            if (connectionResult.isSuccess()) {
                obj = null;
            }
            String zzagy = com_google_android_gms_common_api_internal_zzh.zzagy();
            String valueOf = String.valueOf(connectionResult);
            arrayList.add(new StringBuilder((String.valueOf(zzagy).length() + 2) + String.valueOf(valueOf).length()).append(zzagy).append(": ").append(valueOf).toString());
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

    public final ArrayMap<zzh<?>, ConnectionResult> zzagj() {
        return this.zzflw;
    }
}
