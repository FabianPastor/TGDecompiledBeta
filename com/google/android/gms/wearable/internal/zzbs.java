package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataItem;

public final class zzbs implements DataItemResult {
    private final Status mStatus;
    private final DataItem zzbSB;

    public zzbs(Status status, DataItem dataItem) {
        this.mStatus = status;
        this.zzbSB = dataItem;
    }

    public final DataItem getDataItem() {
        return this.zzbSB;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
