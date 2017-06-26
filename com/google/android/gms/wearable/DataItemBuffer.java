package com.google.android.gms.wearable;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzg;
import com.google.android.gms.wearable.internal.zzcd;

public class DataItemBuffer extends zzg<DataItem> implements Result {
    private final Status mStatus;

    public DataItemBuffer(DataHolder dataHolder) {
        super(dataHolder);
        this.mStatus = new Status(dataHolder.getStatusCode());
    }

    public Status getStatus() {
        return this.mStatus;
    }

    protected final /* synthetic */ Object zzi(int i, int i2) {
        return new zzcd(this.zzaCX, i, i2);
    }

    protected final String zzqS() {
        return "path";
    }
}
