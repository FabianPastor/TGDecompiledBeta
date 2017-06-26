package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.google.android.gms.wearable.NodeApi.GetLocalNodeResult;
import com.google.android.gms.wearable.NodeApi.NodeListener;

public final class zzdz implements NodeApi {
    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, NodeListener nodeListener) {
        return zzb.zza(googleApiClient, new zzec(new IntentFilter[]{zzez.zzgl("com.google.android.gms.wearable.NODE_CHANGED")}), nodeListener);
    }

    public final PendingResult<GetConnectedNodesResult> getConnectedNodes(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzeb(this, googleApiClient));
    }

    public final PendingResult<GetLocalNodeResult> getLocalNode(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzea(this, googleApiClient));
    }

    public final PendingResult<Status> removeListener(GoogleApiClient googleApiClient, NodeListener nodeListener) {
        return googleApiClient.zzd(new zzed(this, googleApiClient, nodeListener));
    }
}
