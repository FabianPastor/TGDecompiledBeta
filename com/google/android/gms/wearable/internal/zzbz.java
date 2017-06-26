package com.google.android.gms.wearable.internal;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzc;
import com.google.android.gms.wearable.DataItemAsset;

public final class zzbz extends zzc implements DataItemAsset {
    public zzbz(DataHolder dataHolder, int i) {
        super(dataHolder, i);
    }

    public final /* synthetic */ Object freeze() {
        return new zzbx(this);
    }

    public final String getDataItemKey() {
        return getString("asset_key");
    }

    public final String getId() {
        return getString("asset_id");
    }
}
