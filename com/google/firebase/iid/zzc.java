package com.google.firebase.iid;

import android.support.annotation.Nullable;

public class zzc {
    private final FirebaseInstanceId bhn;

    private zzc(FirebaseInstanceId firebaseInstanceId) {
        this.bhn = firebaseInstanceId;
    }

    public static zzc A() {
        return new zzc(FirebaseInstanceId.getInstance());
    }

    public String getId() {
        return this.bhn.getId();
    }

    @Nullable
    public String getToken() {
        return this.bhn.getToken();
    }
}
