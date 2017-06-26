package com.google.firebase.auth;

import android.support.annotation.Nullable;

public class GetTokenResult {
    private String zzakv;

    public GetTokenResult(String str) {
        this.zzakv = str;
    }

    @Nullable
    public String getToken() {
        return this.zzakv;
    }
}
