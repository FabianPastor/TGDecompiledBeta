package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import java.io.IOException;
import java.io.OutputStream;

final class zzat implements GetOutputStreamResult {
    private final Status mStatus;
    private final OutputStream zzbSp;

    zzat(Status status, OutputStream outputStream) {
        this.mStatus = (Status) zzbo.zzu(status);
        this.zzbSp = outputStream;
    }

    public final OutputStream getOutputStream() {
        return this.zzbSp;
    }

    public final Status getStatus() {
        return this.mStatus;
    }

    public final void release() {
        if (this.zzbSp != null) {
            try {
                this.zzbSp.close();
            } catch (IOException e) {
            }
        }
    }
}
