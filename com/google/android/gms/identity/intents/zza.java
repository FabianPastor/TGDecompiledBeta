package com.google.android.gms.identity.intents;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.identity.intents.Address.AddressOptions;
import com.google.android.gms.internal.zzcbe;

final class zza extends com.google.android.gms.common.api.Api.zza<zzcbe, AddressOptions> {
    zza() {
    }

    public final /* synthetic */ zze zza(Context context, Looper looper, zzq com_google_android_gms_common_internal_zzq, Object obj, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
        AddressOptions addressOptions = (AddressOptions) obj;
        zzbo.zzb(context instanceof Activity, (Object) "An Activity must be used for Address APIs");
        if (addressOptions == null) {
            addressOptions = new AddressOptions();
        }
        return new zzcbe((Activity) context, looper, com_google_android_gms_common_internal_zzq, addressOptions.theme, connectionCallbacks, onConnectionFailedListener);
    }
}
