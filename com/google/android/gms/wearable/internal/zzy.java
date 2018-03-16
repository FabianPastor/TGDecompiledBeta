package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult;
import com.google.android.gms.wearable.CapabilityInfo;

public final class zzy implements GetCapabilityResult {
    private final Status mStatus;
    private final CapabilityInfo zzliq;

    public zzy(Status status, CapabilityInfo capabilityInfo) {
        this.mStatus = status;
        this.zzliq = capabilityInfo;
    }

    public final CapabilityInfo getCapability() {
        return this.zzliq;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
