package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;

final class zzbcr implements OnConnectionFailedListener {
    private /* synthetic */ zzbem zzaDP;

    zzbcr(zzbco com_google_android_gms_internal_zzbco, zzbem com_google_android_gms_internal_zzbem) {
        this.zzaDP = com_google_android_gms_internal_zzbem;
    }

    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.zzaDP.setResult(new Status(8));
    }
}
