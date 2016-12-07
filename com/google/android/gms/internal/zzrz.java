package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzqc.zzb;

public final class zzrz implements zzry {

    private static class zza extends zzrw {
        private final zzb<Status> Dj;

        public zza(zzb<Status> com_google_android_gms_internal_zzqc_zzb_com_google_android_gms_common_api_Status) {
            this.Dj = com_google_android_gms_internal_zzqc_zzb_com_google_android_gms_common_api_Status;
        }

        public void zzgw(int i) throws RemoteException {
            this.Dj.setResult(new Status(i));
        }
    }

    public PendingResult<Status> zzg(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zza(this, googleApiClient) {
            final /* synthetic */ zzrz Di;

            protected void zza(zzsb com_google_android_gms_internal_zzsb) throws RemoteException {
                ((zzsd) com_google_android_gms_internal_zzsb.zzatx()).zza(new zza(this));
            }
        });
    }
}
