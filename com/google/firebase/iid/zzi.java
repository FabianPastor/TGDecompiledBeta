package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public final class zzi {
    private final FirebaseInstanceId zzckq;

    private zzi(FirebaseInstanceId firebaseInstanceId) {
        this.zzckq = firebaseInstanceId;
    }

    public static zzi zzJM() {
        return new zzi(FirebaseInstanceId.getInstance());
    }

    public final String getId() {
        return this.zzckq.getId();
    }

    @Nullable
    public final String getToken() {
        return this.zzckq.getToken();
    }
}
