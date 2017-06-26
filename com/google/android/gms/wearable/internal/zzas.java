package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import java.io.IOException;
import java.io.InputStream;

final class zzas implements GetInputStreamResult {
    private final Status mStatus;
    private final InputStream zzbSm;

    zzas(Status status, InputStream inputStream) {
        this.mStatus = (Status) zzbo.zzu(status);
        this.zzbSm = inputStream;
    }

    public final InputStream getInputStream() {
        return this.zzbSm;
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    public final void release() {
        if (this.zzbSm != null) {
            try {
                this.zzbSm.close();
            } catch (IOException e) {
            }
        }
    }
}
