package com.google.firebase;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.internal.zzabs;

public class zza implements zzabs {
    public Exception zzA(Status status) {
        return status.getStatusCode() == 8 ? new FirebaseException(status.zzvv()) : new FirebaseApiNotAvailableException(status.zzvv());
    }
}
