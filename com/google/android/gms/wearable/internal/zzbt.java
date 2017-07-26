package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi.DeleteDataItemsResult;

public final class zzbt implements DeleteDataItemsResult {
    private final Status mStatus;
    private final int zzbSC;

    public zzbt(Status status, int i) {
        this.mStatus = status;
        this.zzbSC = i;
    }

    public final int getNumDeleted() {
        return this.zzbSC;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
