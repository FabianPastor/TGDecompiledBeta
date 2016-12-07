package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public class zzc {
    private final FirebaseInstanceId bkz;

    private zzc(FirebaseInstanceId firebaseInstanceId) {
        this.bkz = firebaseInstanceId;
    }

    public static zzc C() {
        return new zzc(FirebaseInstanceId.getInstance());
    }

    public String getId() {
        return this.bkz.getId();
    }

    @Nullable
    public String getToken() {
        return this.bkz.getToken();
    }
}
