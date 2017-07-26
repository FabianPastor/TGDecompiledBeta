package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public class zzbby implements Releasable, Result {
    private Status mStatus;
    protected final DataHolder zzaCX;

    protected zzbby(DataHolder dataHolder, Status status) {
        this.mStatus = status;
        this.zzaCX = dataHolder;
    }

    public Status getStatus() {
        return this.mStatus;
    }

    public void release() {
        if (this.zzaCX != null) {
            this.zzaCX.close();
        }
    }
}
