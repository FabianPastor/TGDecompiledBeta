package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzbq;

public final class zzt implements ConnectionCallbacks, OnConnectionFailedListener {
    public final Api<?> zzfin;
    private final boolean zzfpg;
    private zzu zzfph;

    public zzt(Api<?> api, boolean z) {
        this.zzfin = api;
        this.zzfpg = z;
    }

    private final void zzahj() {
        zzbq.checkNotNull(this.zzfph, "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
    }

    public final void onConnected(Bundle bundle) {
        zzahj();
        this.zzfph.onConnected(bundle);
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        zzahj();
        this.zzfph.zza(connectionResult, this.zzfin, this.zzfpg);
    }

    public final void onConnectionSuspended(int i) {
        zzahj();
        this.zzfph.onConnectionSuspended(i);
    }

    public final void zza(zzu com_google_android_gms_common_api_internal_zzu) {
        this.zzfph = com_google_android_gms_common_api_internal_zzu;
    }
}
