package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import java.util.List;

public final class zzee implements GetConnectedNodesResult {
    private final Status mStatus;
    private final List<Node> zzbSW;

    public zzee(Status status, List<Node> list) {
        this.mStatus = status;
        this.zzbSW = list;
    }

    public final List<Node> getNodes() {
        return this.zzbSW;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
