package com.google.android.gms.common.api;

import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.internal.zzqe;
import java.util.ArrayList;
import java.util.List;

public final class Batch extends zzqe<BatchResult> {
    private int vo;
    private boolean vp;
    private boolean vq;
    private final PendingResult<?>[] vr;
    private final Object zzakd;

    public static final class Builder {
        private GoogleApiClient kv;
        private List<PendingResult<?>> vt = new ArrayList();

        public Builder(GoogleApiClient googleApiClient) {
            this.kv = googleApiClient;
        }

        public <R extends Result> BatchResultToken<R> add(PendingResult<R> pendingResult) {
            BatchResultToken<R> batchResultToken = new BatchResultToken(this.vt.size());
            this.vt.add(pendingResult);
            return batchResultToken;
        }

        public Batch build() {
            return new Batch(this.vt, this.kv);
        }
    }

    private Batch(List<PendingResult<?>> list, GoogleApiClient googleApiClient) {
        super(googleApiClient);
        this.zzakd = new Object();
        this.vo = list.size();
        this.vr = new PendingResult[this.vo];
        if (list.isEmpty()) {
            zzc(new BatchResult(Status.vY, this.vr));
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            PendingResult pendingResult = (PendingResult) list.get(i);
            this.vr[i] = pendingResult;
            pendingResult.zza(new zza(this) {
                final /* synthetic */ Batch vs;

                {
                    this.vs = r1;
                }

                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void zzv(Status status) {
                    synchronized (this.vs.zzakd) {
                        if (this.vs.isCanceled()) {
                            return;
                        }
                        if (status.isCanceled()) {
                            this.vs.vq = true;
                        } else if (!status.isSuccess()) {
                            this.vs.vp = true;
                        }
                        this.vs.vo = this.vs.vo - 1;
                        if (this.vs.vo == 0) {
                            if (this.vs.vq) {
                                super.cancel();
                            } else {
                                this.vs.zzc(new BatchResult(this.vs.vp ? new Status(13) : Status.vY, this.vs.vr));
                            }
                        }
                    }
                }
            });
        }
    }

    public void cancel() {
        super.cancel();
        for (PendingResult cancel : this.vr) {
            cancel.cancel();
        }
    }

    public BatchResult createFailedResult(Status status) {
        return new BatchResult(status, this.vr);
    }

    public /* synthetic */ Result zzc(Status status) {
        return createFailedResult(status);
    }
}
