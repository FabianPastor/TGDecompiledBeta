package com.stripe.android.net;

import java.util.List;
import java.util.Map;

public class StripeResponse {
    private String mResponseBody;
    private int mResponseCode;
    private Map<String, List<String>> mResponseHeaders;

    public StripeResponse(int responseCode, String responseBody, Map<String, List<String>> responseHeaders) {
        this.mResponseCode = responseCode;
        this.mResponseBody = responseBody;
        this.mResponseHeaders = responseHeaders;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public String getResponseBody() {
        return this.mResponseBody;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.mResponseHeaders;
    }
}
