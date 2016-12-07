package com.google.firebase.auth;

import android.support.annotation.Nullable;

public class GetTokenResult {
    private String hN;

    public GetTokenResult(String str) {
        this.hN = str;
    }

    @Nullable
    public String getToken() {
        return this.hN;
    }
}
