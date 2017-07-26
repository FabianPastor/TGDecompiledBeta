package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import java.io.IOException;
import java.io.InputStream;

final class zzas implements GetInputStreamResult {
    private final Status mStatus;
    private final InputStream zzbSo;

    zzas(Status status, InputStream inputStream) {
        this.mStatus = (Status) zzbo.zzu(status);
        this.zzbSo = inputStream;
    }

    public final InputStream getInputStream() {
        return this.zzbSo;
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    public final void release() {
        if (this.zzbSo != null) {
            try {
                this.zzbSo.close();
            } catch (IOException e) {
            }
        }
    }
}
