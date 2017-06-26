package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.ChannelApi.OpenChannelResult;

public final class zzac implements ChannelApi {
    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, ChannelListener channelListener) {
        zzbo.zzb((Object) googleApiClient, (Object) "client is null");
        zzbo.zzb((Object) channelListener, (Object) "listener is null");
        return zzb.zza(googleApiClient, new zzae(new IntentFilter[]{zzez.zzgl(ChannelApi.ACTION_CHANNEL_EVENT)}), channelListener);
    }

    public final PendingResult<OpenChannelResult> openChannel(GoogleApiClient googleApiClient, String str, String str2) {
        zzbo.zzb((Object) googleApiClient, (Object) "client is null");
        zzbo.zzb((Object) str, (Object) "nodeId is null");
        zzbo.zzb((Object) str2, (Object) "path is null");
        return googleApiClient.zzd(new zzad(this, googleApiClient, str, str2));
    }

    public final PendingResult<Status> removeListener(GoogleApiClient googleApiClient, ChannelListener channelListener) {
        zzbo.zzb((Object) googleApiClient, (Object) "client is null");
        zzbo.zzb((Object) channelListener, (Object) "listener is null");
        return googleApiClient.zzd(new zzag(googleApiClient, channelListener, null));
    }
}
