package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.DataItemAsset;

public final class zzbx implements DataItemAsset {
    private final String zzBP;
    private final String zzIk;

    public zzbx(DataItemAsset dataItemAsset) {
        this.zzIk = dataItemAsset.getId();
        this.zzBP = dataItemAsset.getDataItemKey();
    }

    public final /* bridge */ /* synthetic */ Object freeze() {
        return this;
    }

    public final String getDataItemKey() {
        return this.zzBP;
    }

    public final String getId() {
        return this.zzIk;
    }

    public final boolean isDataValid() {
        return true;
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DataItemAssetEntity[");
        stringBuilder.append("@");
        stringBuilder.append(Integer.toHexString(hashCode()));
        if (this.zzIk == null) {
            stringBuilder.append(",noid");
        } else {
            stringBuilder.append(",");
            stringBuilder.append(this.zzIk);
        }
        stringBuilder.append(", key=");
        stringBuilder.append(this.zzBP);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
