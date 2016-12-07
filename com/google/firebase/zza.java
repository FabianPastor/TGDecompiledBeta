package com.google.firebase;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzabk;

public class zza implements zzabk {
    public Exception zzz(Status status) {
        return status.getStatusCode() == 8 ? new FirebaseException(status.zzuU()) : new FirebaseApiNotAvailableException(status.zzuU());
    }
}
