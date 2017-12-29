package com.google.android.gms.wearable;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzg;
import com.google.android.gms.wearable.internal.zzcy;

public class DataEventBuffer extends zzg<Object> implements Result {
    private final Status mStatus;

    public DataEventBuffer(DataHolder dataHolder) {
        super(dataHolder);
        this.mStatus = new Status(dataHolder.getStatusCode());
    }

    public Status getStatus() {
        return this.mStatus;
    }

    protected final String zzaka() {
        return "path";
    }

    protected final /* synthetic */ Object zzl(int i, int i2) {
        return new zzcy(this.zzfqt, i, i2);
    }
}
