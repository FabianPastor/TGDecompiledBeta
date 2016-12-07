package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;

public class zzzy implements ConnectionCallbacks, OnConnectionFailedListener {
    public final Api<?> zzawb;
    private final int zzazb;
    private zzzz zzazc;

    public zzzy(Api<?> api, int i) {
        this.zzawb = api;
        this.zzazb = i;
    }

    private void zzvi() {
        zzac.zzb(this.zzazc, (Object) "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
    }

    public void onConnected(@Nullable Bundle bundle) {
        zzvi();
        this.zzazc.onConnected(bundle);
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzvi();
        this.zzazc.zza(connectionResult, this.zzawb, this.zzazb);
    }

    public void onConnectionSuspended(int i) {
        zzvi();
        this.zzazc.onConnectionSuspended(i);
    }

    public void zza(zzzz com_google_android_gms_internal_zzzz) {
        this.zzazc = com_google_android_gms_internal_zzzz;
    }
}
