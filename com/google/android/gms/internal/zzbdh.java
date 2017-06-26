package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import java.util.Collections;

final class zzbdh implements Runnable {
    private /* synthetic */ ConnectionResult zzaEw;
    private /* synthetic */ zzbdg zzaEy;

    zzbdh(zzbdg com_google_android_gms_internal_zzbdg, ConnectionResult connectionResult) {
        this.zzaEy = com_google_android_gms_internal_zzbdg;
        this.zzaEw = connectionResult;
    }

    public final void run() {
        if (this.zzaEw.isSuccess()) {
            this.zzaEy.zzaEx = true;
            if (this.zzaEy.zzaCy.zzmv()) {
                this.zzaEy.zzqz();
                return;
            } else {
                this.zzaEy.zzaCy.zza(null, Collections.emptySet());
                return;
            }
        }
        ((zzbdc) this.zzaEy.zzaEm.zzaCB.get(this.zzaEy.zzaAK)).onConnectionFailed(this.zzaEw);
    }
}
