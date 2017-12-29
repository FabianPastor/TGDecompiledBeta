package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

final class zzax implements ConnectionCallbacks, OnConnectionFailedListener {
    private /* synthetic */ zzao zzfrl;

    private zzax(zzao com_google_android_gms_common_api_internal_zzao) {
        this.zzfrl = com_google_android_gms_common_api_internal_zzao;
    }

    public final void onConnected(Bundle bundle) {
        this.zzfrl.zzfrd.zza(new zzav(this.zzfrl));
    }

    public final void onConnectionFailed(ConnectionResult connectionResult) {
        this.zzfrl.zzfps.lock();
        try {
            if (this.zzfrl.zzd(connectionResult)) {
                this.zzfrl.zzaif();
                this.zzfrl.zzaid();
            } else {
                this.zzfrl.zze(connectionResult);
            }
            this.zzfrl.zzfps.unlock();
        } catch (Throwable th) {
            this.zzfrl.zzfps.unlock();
        }
    }

    public final void onConnectionSuspended(int i) {
    }
}
