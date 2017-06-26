package com.google.android.gms.wearable;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.wearable.Wearable.WearableOptions;
import com.google.android.gms.wearable.Wearable.WearableOptions.Builder;
import com.google.android.gms.wearable.internal.zzfw;

final class zzj extends zza<zzfw, WearableOptions> {
    zzj() {
    }

    public final /* synthetic */ zze zza(Context context, Looper looper, zzq com_google_android_gms_common_internal_zzq, Object obj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        if (((WearableOptions) obj) == null) {
            WearableOptions wearableOptions = new WearableOptions(new Builder());
        }
        return new zzfw(context, looper, connectionCallbacks, onConnectionFailedListener, com_google_android_gms_common_internal_zzq);
    }
}
