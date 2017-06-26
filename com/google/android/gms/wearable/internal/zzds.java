package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;

public final class zzds implements MessageApi {
    private static PendingResult<Status> zza(GoogleApiClient googleApiClient, MessageListener messageListener, IntentFilter[] intentFilterArr) {
        return googleApiClient.zzd(new zzdv(googleApiClient, messageListener, googleApiClient.zzp(messageListener), intentFilterArr, null));
    }

    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, MessageListener messageListener) {
        return zza(googleApiClient, messageListener, new IntentFilter[]{zzez.zzgl(MessageApi.ACTION_MESSAGE_RECEIVED)});
    }

    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, MessageListener messageListener, Uri uri, int i) {
        zzbo.zzb(uri != null, (Object) "uri must not be null");
        boolean z = i == 0 || i == 1;
        zzbo.zzb(z, (Object) "invalid filter type");
        return zza(googleApiClient, messageListener, new IntentFilter[]{zzez.zza(MessageApi.ACTION_MESSAGE_RECEIVED, uri, i)});
    }

    public final PendingResult<Status> removeListener(GoogleApiClient googleApiClient, MessageListener messageListener) {
        return googleApiClient.zzd(new zzdu(this, googleApiClient, messageListener));
    }

    public final PendingResult<SendMessageResult> sendMessage(GoogleApiClient googleApiClient, String str, String str2, byte[] bArr) {
        return googleApiClient.zzd(new zzdt(this, googleApiClient, str, str2, bArr));
    }
}
