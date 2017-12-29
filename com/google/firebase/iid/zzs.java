package com.google.firebase.iid;

public final class zzs extends Exception {
    private final int errorCode;

    public zzs(int i, String str) {
        super(str);
        this.errorCode = i;
    }

    public final int getErrorCode() {
        return this.errorCode;
    }
}
