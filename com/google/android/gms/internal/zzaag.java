package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.internal.zzac;

public class zzaag implements ConnectionCallbacks, OnConnectionFailedListener {
    private final boolean zzaAu;
    private zzaah zzaAv;
    public final Api<?> zzaxf;

    public zzaag(Api<?> api, boolean z) {
        this.zzaxf = api;
        this.zzaAu = z;
    }

    private void zzvL() {
        zzac.zzb(this.zzaAv, (Object) "Callbacks must be attached to a ClientConnectionHelper instance before connecting the client.");
    }

    public void onConnected(@Nullable Bundle bundle) {
        zzvL();
        this.zzaAv.onConnected(bundle);
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        zzvL();
        this.zzaAv.zza(connectionResult, this.zzaxf, this.zzaAu);
    }

    public void onConnectionSuspended(int i) {
        zzvL();
        this.zzaAv.onConnectionSuspended(i);
    }

    public void zza(zzaah com_google_android_gms_internal_zzaah) {
        this.zzaAv = com_google_android_gms_internal_zzaah;
    }
}
