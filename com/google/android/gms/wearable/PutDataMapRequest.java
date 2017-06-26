package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.internal.adg;
import com.google.android.gms.internal.hb;
import com.google.android.gms.internal.hc;

public class PutDataMapRequest {
    private final DataMap zzbRd = new DataMap();
    private final PutDataRequest zzbRe;

    private PutDataMapRequest(PutDataRequest putDataRequest, DataMap dataMap) {
        this.zzbRe = putDataRequest;
        if (dataMap != null) {
            this.zzbRd.putAll(dataMap);
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
        hc zza = hb.zza(this.zzbRd);
        this.zzbRe.setData(adg.zzc(zza.zzbTD));
        int size = zza.zzbTE.size();
        int i = 0;
        while (i < size) {
            String num = Integer.toString(i);
            Asset asset = (Asset) zza.zzbTE.get(i);
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
                this.zzbRe.putAsset(num, asset);
                i++;
            }
        }
        return this.zzbRe;
    }

    public DataMap getDataMap() {
        return this.zzbRd;
    }

    public Uri getUri() {
        return this.zzbRe.getUri();
    }

    public boolean isUrgent() {
        return this.zzbRe.isUrgent();
    }

    public PutDataMapRequest setUrgent() {
        this.zzbRe.setUrgent();
        return this;
    }
}
