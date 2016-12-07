package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzaa;
import com.google.firebase.FirebaseException;

public class FirebaseAuthException extends FirebaseException {
    private final String aXe;

    public FirebaseAuthException(@NonNull String str, @NonNull String str2) {
        super(str2);
        this.aXe = zzaa.zzib(str);
    }

    @NonNull
    public String getErrorCode() {
        return this.aXe;
    }
}
