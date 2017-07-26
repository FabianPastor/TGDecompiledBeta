package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import java.util.concurrent.atomic.AtomicReference;

final class zzbcr implements ConnectionCallbacks {
    private /* synthetic */ zzbcp zzaDN;
    private /* synthetic */ AtomicReference zzaDO;
    private /* synthetic */ zzben zzaDP;

    zzbcr(zzbcp com_google_android_gms_internal_zzbcp, AtomicReference atomicReference, zzben com_google_android_gms_internal_zzben) {
        this.zzaDN = com_google_android_gms_internal_zzbcp;
        this.zzaDO = atomicReference;
        this.zzaDP = com_google_android_gms_internal_zzben;
    }

    public final void onConnected(Bundle bundle) {
        this.zzaDN.zza((GoogleApiClient) this.zzaDO.get(), this.zzaDP, true);
    }

    public final void onConnectionSuspended(int i) {
    }
}
