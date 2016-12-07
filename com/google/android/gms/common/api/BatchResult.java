package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.TimeUnit;

public final class BatchResult implements Result {
    private final Status fp;
    private final PendingResult<?>[] vr;

    BatchResult(Status status, PendingResult<?>[] pendingResultArr) {
        this.fp = status;
        this.vr = pendingResultArr;
    }

    public Status getStatus() {
        return this.fp;
    }

    public <R extends Result> R take(BatchResultToken<R> batchResultToken) {
        zzac.zzb(batchResultToken.mId < this.vr.length, (Object) "The result token does not belong to this batch");
        return this.vr[batchResultToken.mId].await(0, TimeUnit.MILLISECONDS);
    }
}
