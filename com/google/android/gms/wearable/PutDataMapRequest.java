package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.internal.adp;
import com.google.android.gms.internal.hc;
import com.google.android.gms.internal.hd;

public class PutDataMapRequest {
    private final DataMap zzbRf = new DataMap();
    private final PutDataRequest zzbRg;

    private PutDataMapRequest(PutDataRequest putDataRequest, DataMap dataMap) {
        this.zzbRg = putDataRequest;
        if (dataMap != null) {
            this.zzbRf.putAll(dataMap);
        }
    }

    public static PutDataMapRequest create(String str) {
        return new PutDataMapRequest(PutDataRequest.create(str), null);
    }

    public static PutDataMapRequest createFromDataMapItem(DataMapItem dataMapItem) {
        return new PutDataMapRequest(PutDataRequest.zzt(dataMapItem.getUri()), dataMapItem.getDataMap());
    }

    public static PutDataMapRequest createWithAutoAppendedId(String str) {
        return new PutDataMapRequest(PutDataRequest.createWithAutoAppendedId(str), null);
    }

    public PutDataRequest asPutDataRequest() {
        hd zza = hc.zza(this.zzbRf);
        this.zzbRg.setData(adp.zzc(zza.zzbTF));
        int size = zza.zzbTG.size();
        int i = 0;
        while (i < size) {
            String num = Integer.toString(i);
            Asset asset = (Asset) zza.zzbTG.get(i);
            String valueOf;
            if (num == null) {
                valueOf = String.valueOf(asset);
                throw new IllegalStateException(new StringBuilder(String.valueOf(valueOf).length() + 26).append("asset key cannot be null: ").append(valueOf).toString());
            } else if (asset == null) {
                String str = "asset cannot be null: key=";
                valueOf = String.valueOf(num);
                throw new IllegalStateException(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            } else {
                if (Log.isLoggable(DataMap.TAG, 3)) {
                    String str2 = DataMap.TAG;
                    String valueOf2 = String.valueOf(asset);
                    Log.d(str2, new StringBuilder((String.valueOf(num).length() + 33) + String.valueOf(valueOf2).length()).append("asPutDataRequest: adding asset: ").append(num).append(" ").append(valueOf2).toString());
                }
                this.zzbRg.putAsset(num, asset);
                i++;
            }
        }
        return this.zzbRg;
    }

    public DataMap getDataMap() {
        return this.zzbRf;
    }

    public Uri getUri() {
        return this.zzbRg.getUri();
    }

    public boolean isUrgent() {
        return this.zzbRg.isUrgent();
    }

    public PutDataMapRequest setUrgent() {
        this.zzbRg.setUrgent();
        return this;
    }
}
