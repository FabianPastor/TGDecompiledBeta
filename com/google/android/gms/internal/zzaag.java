package com.google.android.gms.internal;

import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;

public abstract class zzaag implements Releasable, Result {
    protected final Status zzahq;
    protected final DataHolder zzazI;

    protected zzaag(DataHolder dataHolder, Status status) {
        this.zzahq = status;
        this.zzazI = dataHolder;
    }

    public Status getStatus() {
        return this.zzahq;
    }

    public void release() {
        if (this.zzazI != null) {
            this.zzazI.close();
        }
    }
}
