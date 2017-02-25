package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public class zzc {
    private final FirebaseInstanceId zzcla;

    private zzc(FirebaseInstanceId firebaseInstanceId) {
        this.zzcla = firebaseInstanceId;
    }

    public static zzc zzabK() {
        return new zzc(FirebaseInstanceId.getInstance());
    }

    public String getId() {
        return this.zzcla.getId();
    }

    @Nullable
    public String getToken() {
        return this.zzcla.getToken();
    }
}
