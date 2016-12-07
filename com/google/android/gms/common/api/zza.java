package com.google.android.gms.common.api;

import android.support.annotation.NonNull;

public class zza extends Exception {
    protected final Status hv;

    public zza(@NonNull Status status) {
        super(status.getStatusMessage());
        this.hv = status;
    }
}
