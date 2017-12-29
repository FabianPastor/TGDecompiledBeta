package com.google.android.gms.common.api;

public abstract class ResultTransform<R extends Result, S extends Result> {
    public Status onFailure(Status status) {
        return status;
    }

    public abstract PendingResult<S> onSuccess(R r);
}
