package com.google.firebase.iid;

import android.support.annotation.Nullable;

@Deprecated
public class zzc {
    private final FirebaseInstanceId zzclk;

    private zzc(FirebaseInstanceId firebaseInstanceId) {
        this.zzclk = firebaseInstanceId;
    }

    public static zzc zzabN() {
        return new zzc(FirebaseInstanceId.getInstance());
    }

    public String getId() {
        return this.zzclk.getId();
    }

    @Nullable
    public String getToken() {
        return this.zzclk.getToken();
    }
}
