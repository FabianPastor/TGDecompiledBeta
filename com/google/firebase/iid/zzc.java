package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public class zzc {
    private final FirebaseInstanceId zzclg;

    private zzc(FirebaseInstanceId firebaseInstanceId) {
        this.zzclg = firebaseInstanceId;
    }

    public static zzc zzabL() {
        return new zzc(FirebaseInstanceId.getInstance());
    }

    public String getId() {
        return this.zzclg.getId();
    }

    @Nullable
    public String getToken() {
        return this.zzclg.getToken();
    }
}
