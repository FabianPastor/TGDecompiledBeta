package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.measurement.AppMeasurement.zzb;

final class zzchz implements Runnable {
    private /* synthetic */ boolean zzbtN;
    private /* synthetic */ zzb zzbtO;
    private /* synthetic */ zzcib zzbtP;
    private /* synthetic */ zzchy zzbtQ;

    zzchz(zzchy com_google_android_gms_internal_zzchy, boolean z, zzb com_google_android_gms_measurement_AppMeasurement_zzb, zzcib com_google_android_gms_internal_zzcib) {
        this.zzbtQ = com_google_android_gms_internal_zzchy;
        this.zzbtN = z;
        this.zzbtO = com_google_android_gms_measurement_AppMeasurement_zzb;
        this.zzbtP = com_google_android_gms_internal_zzcib;
    }

    public final void run() {
        if (this.zzbtN && this.zzbtQ.zzbtE != null) {
            this.zzbtQ.zza(this.zzbtQ.zzbtE);
        }
        Object obj = (this.zzbtO != null && this.zzbtO.zzbol == this.zzbtP.zzbol && zzcjk.zzR(this.zzbtO.zzbok, this.zzbtP.zzbok) && zzcjk.zzR(this.zzbtO.zzboj, this.zzbtP.zzboj)) ? null : 1;
        if (obj != null) {
            Bundle bundle = new Bundle();
            zzchy.zza(this.zzbtP, bundle);
            if (this.zzbtO != null) {
                if (this.zzbtO.zzboj != null) {
                    bundle.putString("_pn", this.zzbtO.zzboj);
                }
                bundle.putString("_pc", this.zzbtO.zzbok);
                bundle.putLong("_pi", this.zzbtO.zzbol);
            }
            this.zzbtQ.zzwt().zzd("auto", "_vs", bundle);
        }
        this.zzbtQ.zzbtE = this.zzbtP;
        this.zzbtQ.zzww().zza(this.zzbtP);
    }
}
