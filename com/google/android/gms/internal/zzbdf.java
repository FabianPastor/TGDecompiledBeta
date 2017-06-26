package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;

final class zzbdf implements Runnable {
    private /* synthetic */ zzbdc zzaEv;
    private /* synthetic */ ConnectionResult zzaEw;

    zzbdf(zzbdc com_google_android_gms_internal_zzbdc, ConnectionResult connectionResult) {
        this.zzaEv = com_google_android_gms_internal_zzbdc;
        this.zzaEw = connectionResult;
    }

    public final void run() {
        this.zzaEv.onConnectionFailed(this.zzaEw);
    }
}
