package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.ConnectionResult;
import java.util.Collections;

final class zzbv implements Runnable {
    private /* synthetic */ ConnectionResult zzfts;
    private /* synthetic */ zzbu zzftv;

    zzbv(zzbu com_google_android_gms_common_api_internal_zzbu, ConnectionResult connectionResult) {
        this.zzftv = com_google_android_gms_common_api_internal_zzbu;
        this.zzfts = connectionResult;
    }

    public final void run() {
        if (this.zzfts.isSuccess()) {
            this.zzftv.zzftu = true;
            if (this.zzftv.zzfpv.zzaay()) {
                this.zzftv.zzajg();
                return;
            } else {
                this.zzftv.zzfpv.zza(null, Collections.emptySet());
                return;
            }
        }
        ((zzbo) this.zzftv.zzfti.zzfpy.get(this.zzftv.zzfmf)).onConnectionFailed(this.zzfts);
    }
}
