package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

abstract class zzsa<R extends Result> extends com.google.android.gms.internal.zzqc.zza<R, zzsb> {

    static abstract class zza extends zzsa<Status> {
        public zza(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        public Status zzb(Status status) {
            return status;
        }

        public /* synthetic */ Result zzc(Status status) {
            return zzb(status);
        }
    }

    public zzsa(GoogleApiClient googleApiClient) {
        super(zzrx.API, googleApiClient);
    }
}
