package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public abstract class zzqk implements Releasable, Result {
    protected final Status fp;
    protected final DataHolder xi;

    protected zzqk(DataHolder dataHolder, Status status) {
        this.fp = status;
        this.xi = dataHolder;
    }

    public Status getStatus() {
        return this.fp;
    }

    public void release() {
        if (this.xi != null) {
            this.xi.close();
        }
    }
}
