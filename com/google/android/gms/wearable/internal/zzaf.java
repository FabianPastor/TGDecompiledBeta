package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi.OpenChannelResult;

final class zzaf implements OpenChannelResult {
    private final Status mStatus;
    private final Channel zzbSd;

    zzaf(Status status, Channel channel) {
        this.mStatus = (Status) zzbo.zzu(status);
        this.zzbSd = channel;
    }

    public final Channel getChannel() {
        return this.zzbSd;
    }

    public final Status getStatus() {
        return this.mStatus;
    }
}
