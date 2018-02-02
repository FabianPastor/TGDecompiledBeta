package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.data.zzc;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;

public final class zzdf extends zzc implements DataItem {
    private final int zzhwi;

    public zzdf(DataHolder dataHolder, int i, int i2) {
        super(dataHolder, i);
        this.zzhwi = i2;
    }

    public final Map<String, DataItemAsset> getAssets() {
        Map<String, DataItemAsset> hashMap = new HashMap(this.zzhwi);
        for (int i = 0; i < this.zzhwi; i++) {
            zzdb com_google_android_gms_wearable_internal_zzdb = new zzdb(this.zzfqt, this.zzfvx + i);
            if (com_google_android_gms_wearable_internal_zzdb.getDataItemKey() != null) {
                hashMap.put(com_google_android_gms_wearable_internal_zzdb.getDataItemKey(), com_google_android_gms_wearable_internal_zzdb);
            }
        }
        return hashMap;
    }

    public final byte[] getData() {
        return getByteArray(DataSchemeDataSource.SCHEME_DATA);
    }

    public final Uri getUri() {
        return Uri.parse(getString("path"));
    }

    public final String toString() {
        boolean isLoggable = Log.isLoggable("DataItem", 3);
        byte[] data = getData();
        Map assets = getAssets();
        StringBuilder stringBuilder = new StringBuilder("DataItemRef{ ");
        String valueOf = String.valueOf(getUri());
        stringBuilder.append(new StringBuilder(String.valueOf(valueOf).length() + 4).append("uri=").append(valueOf).toString());
        String valueOf2 = String.valueOf(data == null ? "null" : Integer.valueOf(data.length));
        stringBuilder.append(new StringBuilder(String.valueOf(valueOf2).length() + 9).append(", dataSz=").append(valueOf2).toString());
        stringBuilder.append(", numAssets=" + assets.size());
        if (isLoggable && !assets.isEmpty()) {
            stringBuilder.append(", assets=[");
            valueOf2 = TtmlNode.ANONYMOUS_REGION_ID;
            String str = valueOf2;
            for (Entry entry : assets.entrySet()) {
                String str2 = (String) entry.getKey();
                valueOf2 = ((DataItemAsset) entry.getValue()).getId();
                stringBuilder.append(new StringBuilder(((String.valueOf(str).length() + 2) + String.valueOf(str2).length()) + String.valueOf(valueOf2).length()).append(str).append(str2).append(": ").append(valueOf2).toString());
                str = ", ";
            }
            stringBuilder.append("]");
        }
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }
}
