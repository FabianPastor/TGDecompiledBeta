package com.google.firebase;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzbel;

public final class zzb implements zzbel {
    public final Exception zzq(Status status) {
        return status.getStatusCode() == 8 ? new FirebaseException(status.zzpq()) : new FirebaseApiNotAvailableException(status.zzpq());
    }
}
