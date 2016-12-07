package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

abstract class zzaca<R extends Result> extends com.google.android.gms.internal.zzzv.zza<R, zzacb> {

    static abstract class zza extends zzaca<Status> {
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

    public zzaca(GoogleApiClient googleApiClient) {
        super(zzabx.API, googleApiClient);
    }
}
