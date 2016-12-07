package com.google.android.gms.internal;

import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public class zzsc extends zzqq<Status> {
    @Deprecated
    public zzsc(Looper looper) {
        super(looper);
    }

    public zzsc(GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    protected Status zzb(Status status) {
        return status;
    }

    protected /* synthetic */ Result zzc(Status status) {
        return zzb(status);
    }
}
