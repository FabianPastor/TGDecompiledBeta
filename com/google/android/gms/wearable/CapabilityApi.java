package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;

@Deprecated
public interface CapabilityApi {

    @Deprecated
    public interface CapabilityListener {
        void onCapabilityChanged(CapabilityInfo capabilityInfo);
    }

    @Deprecated
    public interface GetCapabilityResult extends Result {
        CapabilityInfo getCapability();
    }

    PendingResult<GetCapabilityResult> getCapability(GoogleApiClient googleApiClient, String str, int i);
}
