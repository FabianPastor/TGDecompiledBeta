package com.google.android.gms.internal;

import android.support.annotation.MainThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiActivity;

final class zzbbb implements Runnable {
    private final zzbba zzaBR;
    final /* synthetic */ zzbaz zzaBS;

    zzbbb(zzbaz com_google_android_gms_internal_zzbaz, zzbba com_google_android_gms_internal_zzbba) {
        this.zzaBS = com_google_android_gms_internal_zzbaz;
        this.zzaBR = com_google_android_gms_internal_zzbba;
    }

    @MainThread
    public final void run() {
        if (this.zzaBS.mStarted) {
            ConnectionResult zzpz = this.zzaBR.zzpz();
            if (zzpz.hasResolution()) {
                this.zzaBS.zzaEG.startActivityForResult(GoogleApiActivity.zza(this.zzaBS.getActivity(), zzpz.getResolution(), this.zzaBR.zzpy(), false), 1);
            } else if (this.zzaBS.zzaBd.isUserResolvableError(zzpz.getErrorCode())) {
                this.zzaBS.zzaBd.zza(this.zzaBS.getActivity(), this.zzaBS.zzaEG, zzpz.getErrorCode(), 2, this.zzaBS);
            } else if (zzpz.getErrorCode() == 18) {
                GoogleApiAvailability.zza(this.zzaBS.getActivity().getApplicationContext(), new zzbbc(this, GoogleApiAvailability.zza(this.zzaBS.getActivity(), this.zzaBS)));
            } else {
                this.zzaBS.zza(zzpz, this.zzaBR.zzpy());
            }
        }
    }
}
