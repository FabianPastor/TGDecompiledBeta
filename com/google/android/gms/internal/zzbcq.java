package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import java.util.concurrent.atomic.AtomicReference;

final class zzbcq implements ConnectionCallbacks {
    private /* synthetic */ zzbco zzaDN;
    private /* synthetic */ AtomicReference zzaDO;
    private /* synthetic */ zzbem zzaDP;

    zzbcq(zzbco com_google_android_gms_internal_zzbco, AtomicReference atomicReference, zzbem com_google_android_gms_internal_zzbem) {
        this.zzaDN = com_google_android_gms_internal_zzbco;
        this.zzaDO = atomicReference;
        this.zzaDP = com_google_android_gms_internal_zzbem;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaDN.zza((GoogleApiClient) this.zzaDO.get(), this.zzaDP, true);
    }

    public final void onConnectionSuspended(int i) {
    }
}
