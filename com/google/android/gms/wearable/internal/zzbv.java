package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataItem;

public final class zzbv implements DataEvent {
    private int zzamr;
    private DataItem zzbSB;

    public zzbv(DataEvent dataEvent) {
        this.zzamr = dataEvent.getType();
        this.zzbSB = (DataItem) dataEvent.getDataItem().freeze();
    }

    public final /* bridge */ /* synthetic */ Object freeze() {
        return this;
    }

    public final DataItem getDataItem() {
        return this.zzbSB;
    }

    public final int getType() {
        return this.zzamr;
    }

    public final boolean isDataValid() {
        return true;
    }

    public final String toString() {
        String str = getType() == 1 ? "changed" : getType() == 2 ? "deleted" : "unknown";
        String valueOf = String.valueOf(getDataItem());
        return new StringBuilder((String.valueOf(str).length() + 35) + String.valueOf(valueOf).length()).append("DataEventEntity{ type=").append(str).append(", dataitem=").append(valueOf).append(" }").toString();
    }
}
