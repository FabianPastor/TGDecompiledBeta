package com.google.android.gms.common;

import android.content.Intent;

public class GooglePlayServicesRepairableException extends UserRecoverableException {
    private final int hM;

    GooglePlayServicesRepairableException(int i, String str, Intent intent) {
        super(str, intent);
        this.hM = i;
    }

    public int getConnectionStatusCode() {
        return this.hM;
    }
}
