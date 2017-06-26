package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi.DeleteDataItemsResult;

public final class zzbt implements DeleteDataItemsResult {
    private final Status mStatus;
    private final int zzbSA;

    public zzbt(Status status, int i) {
        this.mStatus = status;
        this.zzbSA = i;
    }

    public final int getNumDeleted() {
        return this.zzbSA;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
