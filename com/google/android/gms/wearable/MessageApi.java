package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;

@Deprecated
public interface MessageApi {

    @Deprecated
    public interface MessageListener {
        void onMessageReceived(MessageEvent messageEvent);
    }

    @Deprecated
    public interface SendMessageResult extends Result {
        int getRequestId();
    }

    PendingResult<SendMessageResult> sendMessage(GoogleApiClient googleApiClient, String str, String str2, byte[] bArr);
}
