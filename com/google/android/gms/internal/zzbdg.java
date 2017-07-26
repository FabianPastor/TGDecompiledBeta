package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;

final class zzbdg implements Runnable {
    private /* synthetic */ zzbdd zzaEv;
    private /* synthetic */ ConnectionResult zzaEw;

    zzbdg(zzbdd com_google_android_gms_internal_zzbdd, ConnectionResult connectionResult) {
        this.zzaEv = com_google_android_gms_internal_zzbdd;
        this.zzaEw = connectionResult;
    }

    public final void run() {
        this.zzaEv.onConnectionFailed(this.zzaEw);
    }
}
