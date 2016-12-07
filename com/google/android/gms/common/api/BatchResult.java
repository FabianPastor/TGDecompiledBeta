package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzaa;
import java.util.concurrent.TimeUnit;

public final class BatchResult implements Result {
    private final Status hv;
    private final PendingResult<?>[] xs;

    BatchResult(Status status, PendingResult<?>[] pendingResultArr) {
        this.hv = status;
        this.xs = pendingResultArr;
    }

    public Status getStatus() {
        return this.hv;
    }

    public <R extends Result> R take(BatchResultToken<R> batchResultToken) {
        zzaa.zzb(batchResultToken.mId < this.xs.length, (Object) "The result token does not belong to this batch");
        return this.xs[batchResultToken.mId].await(0, TimeUnit.MILLISECONDS);
    }
}
