package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.TimeUnit;

public final class BatchResult implements Result {
    private final Status zzair;
    private final PendingResult<?>[] zzayP;

    BatchResult(Status status, PendingResult<?>[] pendingResultArr) {
        this.zzair = status;
        this.zzayP = pendingResultArr;
    }

    public Status getStatus() {
        return this.zzair;
    }

    public <R extends Result> R take(BatchResultToken<R> batchResultToken) {
        zzac.zzb(batchResultToken.mId < this.zzayP.length, (Object) "The result token does not belong to this batch");
        return this.zzayP[batchResultToken.mId].await(0, TimeUnit.MILLISECONDS);
    }
}
