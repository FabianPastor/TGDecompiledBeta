package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzc;
import com.google.android.gms.wearable.DataItem;

public final class zzcy extends zzc {
    private final int zzhwi;

    public zzcy(DataHolder dataHolder, int i, int i2) {
        super(dataHolder, i);
        this.zzhwi = i2;
    }

    public final DataItem getDataItem() {
        return new zzdf(this.zzfqt, this.zzfvx, this.zzhwi);
    }

    public final int getType() {
        return getInteger("event_type");
    }

    public final String toString() {
        String str = getType() == 1 ? "changed" : getType() == 2 ? "deleted" : "unknown";
        String valueOf = String.valueOf(getDataItem());
        return new StringBuilder((String.valueOf(str).length() + 32) + String.valueOf(valueOf).length()).append("DataEventRef{ type=").append(str).append(", dataitem=").append(valueOf).append(" }").toString();
    }
}
