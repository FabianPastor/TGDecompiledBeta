package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public abstract class zzaao implements Releasable, Result {
    protected final DataHolder zzaBi;
    protected final Status zzair;

    protected zzaao(DataHolder dataHolder, Status status) {
        this.zzair = status;
        this.zzaBi = dataHolder;
    }

    public Status getStatus() {
        return this.zzair;
    }

    public void release() {
        if (this.zzaBi != null) {
            this.zzaBi.close();
        }
    }
}
