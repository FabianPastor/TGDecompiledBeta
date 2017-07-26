package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class go extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ FullWalletRequest zzbQA;

    go(gl glVar, GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i) {
        this.zzbQA = fullWalletRequest;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gu guVar) {
        guVar.zza(this.zzbQA, this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
