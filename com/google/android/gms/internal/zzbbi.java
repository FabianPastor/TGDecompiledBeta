package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzbo;

public final class zzbbi implements ConnectionCallbacks, OnConnectionFailedListener {
    private final boolean zzaCj;
    private zzbbj zzaCk;
    public final Api<?> zzayW;

    public zzbbi(Api<?> api, boolean z) {
        this.zzayW = api;
        this.zzaCj = z;
    }

    private final void zzpD() {
        zzbo.zzb(this.zzaCk, (Object) "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
    }

    public final void onConnected(@Nullable Bundle bundle) {
        zzpD();
        this.zzaCk.onConnected(bundle);
    }

    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzpD();
        this.zzaCk.zza(connectionResult, this.zzayW, this.zzaCj);
    }

    public final void onConnectionSuspended(int i) {
        zzpD();
        this.zzaCk.onConnectionSuspended(i);
    }

    public final void zza(zzbbj com_google_android_gms_internal_zzbbj) {
        this.zzaCk = com_google_android_gms_internal_zzbbj;
    }
}
