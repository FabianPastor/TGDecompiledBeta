package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

final class zzbcm implements ConnectionCallbacks, OnConnectionFailedListener {
    private /* synthetic */ zzbcd zzaDp;

    private zzbcm(zzbcd com_google_android_gms_internal_zzbcd) {
        this.zzaDp = com_google_android_gms_internal_zzbcd;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaDp.zzaDh.zza(new zzbck(this.zzaDp));
    }

    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.zzaDp.zzaCv.lock();
        try {
            if (this.zzaDp.zzd(connectionResult)) {
                this.zzaDp.zzpZ();
                this.zzaDp.zzpX();
            } else {
                this.zzaDp.zze(connectionResult);
            }
            this.zzaDp.zzaCv.unlock();
        } catch (Throwable th) {
            this.zzaDp.zzaCv.unlock();
        }
    }

    public final void onConnectionSuspended(int i) {
    }
}
