package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.DataItemAsset;

@KeepName
public class DataItemAssetParcelable extends zzbfm implements ReflectedParcelable, DataItemAsset {
    public static final Creator<DataItemAssetParcelable> CREATOR = new zzda();
    private final String zzbhb;
    private final String zzbuz;

    public DataItemAssetParcelable(DataItemAsset dataItemAsset) {
        this.zzbuz = (String) zzbq.checkNotNull(dataItemAsset.getId());
        this.zzbhb = (String) zzbq.checkNotNull(dataItemAsset.getDataItemKey());
    }

    DataItemAssetParcelable(String str, String str2) {
        this.zzbuz = str;
        this.zzbhb = str2;
    }

    public String getDataItemKey() {
        return this.zzbhb;
    }

    public String getId() {
        return this.zzbuz;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DataItemAssetParcelable[");
        stringBuilder.append("@");
        stringBuilder.append(Integer.toHexString(hashCode()));
        if (this.zzbuz == null) {
            stringBuilder.append(",noid");
        } else {
            stringBuilder.append(",");
            stringBuilder.append(this.zzbuz);
        }
        stringBuilder.append(", key=");
        stringBuilder.append(this.zzbhb);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getId(), false);
        zzbfp.zza(parcel, 3, getDataItemKey(), false);
        zzbfp.zzai(parcel, zze);
    }
}
