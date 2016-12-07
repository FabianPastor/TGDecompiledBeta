package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.TimeUnit;

public final class BatchResult implements Result {
    private final Status zzahq;
    private final PendingResult<?>[] zzaxC;

    BatchResult(Status status, PendingResult<?>[] pendingResultArr) {
        this.zzahq = status;
        this.zzaxC = pendingResultArr;
    }

    public Status getStatus() {
        return this.zzahq;
    }

    public <R extends Result> R take(BatchResultToken<R> batchResultToken) {
        zzac.zzb(batchResultToken.mId < this.zzaxC.length, (Object) "The result token does not belong to this batch");
        return this.zzaxC[batchResultToken.mId].await(0, TimeUnit.MILLISECONDS);
    }
}
