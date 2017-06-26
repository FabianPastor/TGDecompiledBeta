package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import java.io.IOException;
import java.io.OutputStream;

final class zzat implements GetOutputStreamResult {
    private final Status mStatus;
    private final OutputStream zzbSn;

    zzat(Status status, OutputStream outputStream) {
        this.mStatus = (Status) zzbo.zzu(status);
        this.zzbSn = outputStream;
    }

    public final OutputStream getOutputStream() {
        return this.zzbSn;
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    public final void release() {
        if (this.zzbSn != null) {
            try {
                this.zzbSn.close();
            } catch (IOException e) {
            }
        }
    }
}
