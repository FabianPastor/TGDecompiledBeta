package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzaa;

public class zzqr implements ConnectionCallbacks, OnConnectionFailedListener {
    public final Api<?> vS;
    private final int yU;
    private zzqs yV;

    public zzqr(Api<?> api, int i) {
        this.vS = api;
        this.yU = i;
    }

    private void zzary() {
        zzaa.zzb(this.yV, (Object) "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
    }

    public void onConnected(@Nullable Bundle bundle) {
        zzary();
        this.yV.onConnected(bundle);
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzary();
        this.yV.zza(connectionResult, this.vS, this.yU);
    }

    public void onConnectionSuspended(int i) {
        zzary();
        this.yV.onConnectionSuspended(i);
    }

    public void zza(zzqs com_google_android_gms_internal_zzqs) {
        this.yV = com_google_android_gms_internal_zzqs;
    }
}
