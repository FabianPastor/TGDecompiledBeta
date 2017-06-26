package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.util.List;

public interface NodeApi {

    @Deprecated
    public interface NodeListener {
        @Deprecated
        void onPeerConnected(Node node);

        @Deprecated
        void onPeerDisconnected(Node node);
    }

    public interface GetConnectedNodesResult extends Result {
        List<Node> getNodes();
    }

    public interface GetLocalNodeResult extends Result {
        Node getNode();
    }

    @Deprecated
    PendingResult<Status> addListener(GoogleApiClient googleApiClient, NodeListener nodeListener);

    PendingResult<GetConnectedNodesResult> getConnectedNodes(GoogleApiClient googleApiClient);

    PendingResult<GetLocalNodeResult> getLocalNode(GoogleApiClient googleApiClient);

    @Deprecated
    PendingResult<Status> removeListener(GoogleApiClient googleApiClient, NodeListener nodeListener);
}
