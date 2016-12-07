package com.google.firebase.auth;

import android.support.annotation.Nullable;

public class GetTokenResult {
    private String zzahI;

    public GetTokenResult(String str) {
        this.zzahI = str;
    }

    @Nullable
    public String getToken() {
        return this.zzahI;
    }
}
