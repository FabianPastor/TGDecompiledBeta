package com.google.android.gms.common.api;

import com.google.android.gms.common.api.PendingResult.zza;

final class zzb implements zza {
    private /* synthetic */ Batch zzaAG;

    zzb(Batch batch) {
        this.zzaAG = batch;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzo(Status status) {
        synchronized (this.zzaAG.mLock) {
            if (this.zzaAG.isCanceled()) {
                return;
            }
            if (status.isCanceled()) {
                this.zzaAG.zzaAE = true;
            } else if (!status.isSuccess()) {
                this.zzaAG.zzaAD = true;
            }
            this.zzaAG.zzaAC = this.zzaAG.zzaAC - 1;
            if (this.zzaAG.zzaAC == 0) {
                if (this.zzaAG.zzaAE) {
                    super.cancel();
                } else {
                    this.zzaAG.setResult(new BatchResult(this.zzaAG.zzaAD ? new Status(13) : Status.zzaBm, this.zzaAG.zzaAF));
                }
            }
        }
    }
}
