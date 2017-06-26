package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;
import com.google.android.gms.wearable.CapabilityInfo;

public final class zzy implements GetCapabilityResult {
    private final Status mStatus;
    private final CapabilityInfo zzbSa;

    public zzy(Status status, CapabilityInfo capabilityInfo) {
        this.mStatus = status;
        this.zzbSa = capabilityInfo;
    }

    public final CapabilityInfo getCapability() {
        return this.zzbSa;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
