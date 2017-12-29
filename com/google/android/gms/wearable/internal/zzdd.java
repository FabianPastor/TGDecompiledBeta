package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class zzdd extends zzbfm implements DataItem {
    public static final Creator<zzdd> CREATOR = new zzde();
    private final Uri mUri;
    private byte[] zzhyw;
    private final Map<String, DataItemAsset> zzlkc;

    zzdd(Uri uri, Bundle bundle, byte[] bArr) {
        this.mUri = uri;
        Map hashMap = new HashMap();
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        for (String str : bundle.keySet()) {
            hashMap.put(str, (DataItemAssetParcelable) bundle.getParcelable(str));
        }
        this.zzlkc = hashMap;
        this.zzhyw = bArr;
    }

    public final byte[] getData() {
        return this.zzhyw;
    }

    public final Uri getUri() {
        return this.mUri;
    }

    public final String toString() {
        boolean isLoggable = Log.isLoggable("DataItem", 3);
        StringBuilder stringBuilder = new StringBuilder("DataItemParcelable[");
        stringBuilder.append("@");
        stringBuilder.append(Integer.toHexString(hashCode()));
        String valueOf = String.valueOf(this.zzhyw == null ? "null" : Integer.valueOf(this.zzhyw.length));
        stringBuilder.append(new StringBuilder(String.valueOf(valueOf).length() + 8).append(",dataSz=").append(valueOf).toString());
        stringBuilder.append(", numAssets=" + this.zzlkc.size());
        valueOf = String.valueOf(this.mUri);
        stringBuilder.append(new StringBuilder(String.valueOf(valueOf).length() + 6).append(", uri=").append(valueOf).toString());
        if (isLoggable) {
            stringBuilder.append("]\n  assets: ");
            for (String valueOf2 : this.zzlkc.keySet()) {
                String valueOf3 = String.valueOf(this.zzlkc.get(valueOf2));
                stringBuilder.append(new StringBuilder((String.valueOf(valueOf2).length() + 7) + String.valueOf(valueOf3).length()).append("\n    ").append(valueOf2).append(": ").append(valueOf3).toString());
            }
            stringBuilder.append("\n  ]");
            return stringBuilder.toString();
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, getUri(), i, false);
        Bundle bundle = new Bundle();
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        for (Entry entry : this.zzlkc.entrySet()) {
            bundle.putParcelable((String) entry.getKey(), new DataItemAssetParcelable((DataItemAsset) entry.getValue()));
        }
        zzbfp.zza(parcel, 4, bundle, false);
        zzbfp.zza(parcel, 5, getData(), false);
        zzbfp.zzai(parcel, zze);
    }
}
