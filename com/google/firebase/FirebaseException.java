package com.google.firebase;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzaa;

public class FirebaseException extends Exception {
    @Deprecated
    protected FirebaseException() {
    }

    public FirebaseException(@NonNull String str) {
        super(zzaa.zzh(str, "Detail message must not be empty"));
    }

    public FirebaseException(@NonNull String str, Throwable th) {
        super(zzaa.zzh(str, "Detail message must not be empty"), th);
    }
}
