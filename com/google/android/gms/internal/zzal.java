package com.google.android.gms.internal;

import java.net.URI;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public final class zzal extends HttpEntityEnclosingRequestBase {
    public zzal(String str) {
        setURI(URI.create(str));
    }

    public final String getMethod() {
        return "PATCH";
    }
}
