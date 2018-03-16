package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;

public final class zzo implements CapabilityApi {
    public final PendingResult<GetCapabilityResult> getCapability(GoogleApiClient googleApiClient, String str, int i) {
        boolean z = true;
        if (!(i == 0 || i == 1)) {
            z = false;
        }
        zzbq.checkArgument(z);
        return googleApiClient.zzd(new zzp(this, googleApiClient, str, i));
    }
}
