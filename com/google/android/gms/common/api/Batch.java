package com.google.android.gms.common.api;

import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.internal.zzqq;
import java.util.ArrayList;
import java.util.List;

public final class Batch extends zzqq<BatchResult> {
    private int xp;
    private boolean xq;
    private boolean xr;
    private final PendingResult<?>[] xs;
    private final Object zzako;

    public static final class Builder {
        private GoogleApiClient mD;
        private List<PendingResult<?>> xu = new ArrayList();

        public Builder(GoogleApiClient googleApiClient) {
            this.mD = googleApiClient;
        }

        public <R extends Result> BatchResultToken<R> add(PendingResult<R> pendingResult) {
            BatchResultToken<R> batchResultToken = new BatchResultToken(this.xu.size());
            this.xu.add(pendingResult);
            return batchResultToken;
        }

        public Batch build() {
            return new Batch(this.xu, this.mD);
        }
    }

    private Batch(List<PendingResult<?>> list, GoogleApiClient googleApiClient) {
        super(googleApiClient);
        this.zzako = new Object();
        this.xp = list.size();
        this.xs = new PendingResult[this.xp];
        if (list.isEmpty()) {
            zzc(new BatchResult(Status.xZ, this.xs));
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            PendingResult pendingResult = (PendingResult) list.get(i);
            this.xs[i] = pendingResult;
            pendingResult.zza(new zza(this) {
                final /* synthetic */ Batch xt;

                {
                    this.xt = r1;
                }

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void zzx(Status status) {
                    synchronized (this.xt.zzako) {
                        if (this.xt.isCanceled()) {
                            return;
                        }
                        if (status.isCanceled()) {
                            this.xt.xr = true;
                        } else if (!status.isSuccess()) {
                            this.xt.xq = true;
                        }
                        this.xt.xp = this.xt.xp - 1;
                        if (this.xt.xp == 0) {
                            if (this.xt.xr) {
                                super.cancel();
                            } else {
                                this.xt.zzc(new BatchResult(this.xt.xq ? new Status(13) : Status.xZ, this.xt.xs));
                            }
                        }
                    }
                }
            });
        }
    }

    public void cancel() {
        super.cancel();
        for (PendingResult cancel : this.xs) {
            cancel.cancel();
        }
    }

    public BatchResult createFailedResult(Status status) {
        return new BatchResult(status, this.xs);
    }

    public /* synthetic */ Result zzc(Status status) {
        return createFailedResult(status);
    }
}
