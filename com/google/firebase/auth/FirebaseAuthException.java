package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.FirebaseException;

public class FirebaseAuthException extends FirebaseException {
    private final String zzbXP;

    public FirebaseAuthException(@NonNull String str, @NonNull String str2) {
        super(str2);
        this.zzbXP = zzac.zzdr(str);
    }

    @NonNull
    public String getErrorCode() {
        return this.zzbXP;
    }
}
