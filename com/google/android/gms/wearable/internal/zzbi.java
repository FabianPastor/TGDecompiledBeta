package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataApi.DeleteDataItemsResult;
import com.google.android.gms.wearable.DataApi.GetFdForAssetResult;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.PutDataRequest;

public final class zzbi implements DataApi {
    private static PendingResult<Status> zza(GoogleApiClient googleApiClient, DataListener dataListener, IntentFilter[] intentFilterArr) {
        return zzb.zza(googleApiClient, new zzbq(intentFilterArr), dataListener);
    }

    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, DataListener dataListener) {
        return zza(googleApiClient, dataListener, new IntentFilter[]{zzez.zzgl(DataApi.ACTION_DATA_CHANGED)});
    }

    public final PendingResult<Status> addListener(GoogleApiClient googleApiClient, DataListener dataListener, Uri uri, int i) {
        zzbo.zzb(uri != null, (Object) "uri must not be null");
        boolean z = i == 0 || i == 1;
        zzbo.zzb(z, (Object) "invalid filter type");
        return zza(googleApiClient, dataListener, new IntentFilter[]{zzez.zza(DataApi.ACTION_DATA_CHANGED, uri, i)});
    }

    public final PendingResult<DeleteDataItemsResult> deleteDataItems(GoogleApiClient googleApiClient, Uri uri) {
        return deleteDataItems(googleApiClient, uri, 0);
    }

    public final PendingResult<DeleteDataItemsResult> deleteDataItems(GoogleApiClient googleApiClient, Uri uri, int i) {
        boolean z = false;
        zzbo.zzb(uri != null, (Object) "uri must not be null");
        if (i == 0 || i == 1) {
            z = true;
        }
        zzbo.zzb(z, (Object) "invalid filter type");
        return googleApiClient.zzd(new zzbn(this, googleApiClient, uri, i));
    }

    public final PendingResult<DataItemResult> getDataItem(GoogleApiClient googleApiClient, Uri uri) {
        return googleApiClient.zzd(new zzbk(this, googleApiClient, uri));
    }

    public final PendingResult<DataItemBuffer> getDataItems(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzbl(this, googleApiClient));
    }

    public final PendingResult<DataItemBuffer> getDataItems(GoogleApiClient googleApiClient, Uri uri) {
        return getDataItems(googleApiClient, uri, 0);
    }

    public final PendingResult<DataItemBuffer> getDataItems(GoogleApiClient googleApiClient, Uri uri, int i) {
        boolean z = false;
        zzbo.zzb(uri != null, (Object) "uri must not be null");
        if (i == 0 || i == 1) {
            z = true;
        }
        zzbo.zzb(z, (Object) "invalid filter type");
        return googleApiClient.zzd(new zzbm(this, googleApiClient, uri, i));
    }

    public final PendingResult<GetFdForAssetResult> getFdForAsset(GoogleApiClient googleApiClient, Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("asset is null");
        } else if (asset.getDigest() == null) {
            throw new IllegalArgumentException("invalid asset");
        } else if (asset.getData() == null) {
            return googleApiClient.zzd(new zzbo(this, googleApiClient, asset));
        } else {
            throw new IllegalArgumentException("invalid asset");
        }
    }

    public final PendingResult<GetFdForAssetResult> getFdForAsset(GoogleApiClient googleApiClient, DataItemAsset dataItemAsset) {
        return googleApiClient.zzd(new zzbp(this, googleApiClient, dataItemAsset));
    }

    public final PendingResult<DataItemResult> putDataItem(GoogleApiClient googleApiClient, PutDataRequest putDataRequest) {
        return googleApiClient.zzd(new zzbj(this, googleApiClient, putDataRequest));
    }

    public final PendingResult<Status> removeListener(GoogleApiClient googleApiClient, DataListener dataListener) {
        return googleApiClient.zzd(new zzbr(this, googleApiClient, dataListener));
    }
}
