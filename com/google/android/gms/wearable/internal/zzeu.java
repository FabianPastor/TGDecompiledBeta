package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;

public final class zzeu implements MessageApi {
    public final PendingResult<SendMessageResult> sendMessage(GoogleApiClient googleApiClient, String str, String str2, byte[] bArr) {
        return googleApiClient.zzd(new zzev(this, googleApiClient, str, str2, bArr));
    }
}
