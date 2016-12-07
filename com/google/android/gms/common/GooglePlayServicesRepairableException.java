package com.google.android.gms.common;

import android.content.Intent;

public class GooglePlayServicesRepairableException extends UserRecoverableException {
    private final int fF;

    GooglePlayServicesRepairableException(int i, String str, Intent intent) {
        super(str, intent);
        this.fF = i;
    }

    public int getConnectionStatusCode() {
        return this.fF;
    }
}
