package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class gq extends zzb {
    private /* synthetic */ NotifyTransactionStatusRequest zzbQD;

    gq(gl glVar, GoogleApiClient googleApiClient, NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        this.zzbQD = notifyTransactionStatusRequest;
        super(googleApiClient);
    }

    protected final void zza(gu guVar) {
        guVar.zza(this.zzbQD);
        setResult(Status.zzaBm);
    }
}
