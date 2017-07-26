package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.DataItemAsset;

public final class zzbx implements DataItemAsset {
    private final String zzBN;
    private final String zzIi;

    public zzbx(DataItemAsset dataItemAsset) {
        this.zzIi = dataItemAsset.getId();
        this.zzBN = dataItemAsset.getDataItemKey();
    }

    public final /* bridge */ /* synthetic */ Object freeze() {
        return this;
    }

    public final String getDataItemKey() {
        return this.zzBN;
    }

    public final String getId() {
        return this.zzIi;
    }

    public final boolean isDataValid() {
        return true;
    }

    public final String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DataItemAssetEntity[");
        stringBuilder.append("@");
        stringBuilder.append(Integer.toHexString(hashCode()));
        if (this.zzIi == null) {
            stringBuilder.append(",noid");
        } else {
            stringBuilder.append(",");
            stringBuilder.append(this.zzIi);
        }
        stringBuilder.append(", key=");
        stringBuilder.append(this.zzBN);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
