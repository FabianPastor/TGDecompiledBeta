package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

final class zzbcl implements ConnectionCallbacks, OnConnectionFailedListener {
    private /* synthetic */ zzbcc zzaDp;

    private zzbcl(zzbcc com_google_android_gms_internal_zzbcc) {
        this.zzaDp = com_google_android_gms_internal_zzbcc;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaDp.zzaDh.zza(new zzbcj(this.zzaDp));
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
