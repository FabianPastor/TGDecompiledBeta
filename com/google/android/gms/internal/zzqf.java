package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;

public class zzqf implements ConnectionCallbacks, OnConnectionFailedListener {
    public final Api<?> tv;
    private final int wT;
    private zzqg wU;

    public zzqf(Api<?> api, int i) {
        this.tv = api;
        this.wT = i;
    }

    private void zzaqx() {
        zzac.zzb(this.wU, (Object) "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
    }

    public void onConnected(@Nullable Bundle bundle) {
        zzaqx();
        this.wU.onConnected(bundle);
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzaqx();
        this.wU.zza(connectionResult, this.tv, this.wT);
    }

    public void onConnectionSuspended(int i) {
        zzaqx();
        this.wU.onConnectionSuspended(i);
    }

    public void zza(zzqg com_google_android_gms_internal_zzqg) {
        this.wU = com_google_android_gms_internal_zzqg;
    }
}
