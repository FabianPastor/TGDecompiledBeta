package com.google.android.gms.internal;

import android.os.Looper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzben extends zzbbe<Status> {
    @Deprecated
    public zzben(Looper looper) {
        super(looper);
    }

    public zzben(GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return status;
    }
}
