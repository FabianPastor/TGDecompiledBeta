package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetLocalNodeResult;

public final class zzef implements GetLocalNodeResult {
    private final Status mStatus;
    private final Node zzbSZ;

    public zzef(Status status, Node node) {
        this.mStatus = status;
        this.zzbSZ = node;
    }

    public final Node getNode() {
        return this.zzbSZ;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
