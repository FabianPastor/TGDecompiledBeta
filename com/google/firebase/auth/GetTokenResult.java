package com.google.firebase.auth;

import android.support.annotation.Nullable;

public class GetTokenResult {
    private String zzaiJ;

    public GetTokenResult(String str) {
        this.zzaiJ = str;
    }

    @Nullable
    public String getToken() {
        return this.zzaiJ;
    }
}
