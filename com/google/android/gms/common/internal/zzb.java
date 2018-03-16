package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;

public final class zzb {
    public static ApiException zzy(Status status) {
        return status.hasResolution() ? new ResolvableApiException(status) : new ApiException(status);
    }
}
