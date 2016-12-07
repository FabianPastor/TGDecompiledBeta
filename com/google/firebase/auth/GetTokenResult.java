package com.google.firebase.auth;

import android.support.annotation.Nullable;

public class GetTokenResult {
    private String fG;

    public GetTokenResult(String str) {
        this.fG = str;
    }

    @Nullable
    public String getToken() {
        return this.fG;
    }
}
