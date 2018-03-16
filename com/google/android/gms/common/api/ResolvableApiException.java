package com.google.android.gms.common.api;

public class ResolvableApiException extends ApiException {
    public ResolvableApiException(Status status) {
        super(status);
    }
}
