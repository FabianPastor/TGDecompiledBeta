package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public final class zzi {
    private final FirebaseInstanceId zzcku;

    private zzi(FirebaseInstanceId firebaseInstanceId) {
        this.zzcku = firebaseInstanceId;
    }

    public static zzi zzJP() {
        return new zzi(FirebaseInstanceId.getInstance());
    }

    public final String getId() {
        return this.zzcku.getId();
    }

    @Nullable
    public final String getToken() {
        return this.zzcku.getToken();
    }
}
