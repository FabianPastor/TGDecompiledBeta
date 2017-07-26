package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import java.util.List;

public final class zzee implements GetConnectedNodesResult {
    private final Status mStatus;
    private final List<Node> zzbSY;

    public zzee(Status status, List<Node> list) {
        this.mStatus = status;
        this.zzbSY = list;
    }

    public final List<Node> getNodes() {
        return this.zzbSY;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
