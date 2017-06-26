package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class gp extends zzb {
    private /* synthetic */ NotifyTransactionStatusRequest zzbQB;

    gp(gk gkVar, GoogleApiClient googleApiClient, NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        this.zzbQB = notifyTransactionStatusRequest;
        super(googleApiClient);
    }

    protected final void zza(gt gtVar) {
        gtVar.zza(this.zzbQB);
        setResult(Status.zzaBm);
    }
}
