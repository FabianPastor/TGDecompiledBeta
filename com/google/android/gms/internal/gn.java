package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class gn extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ MaskedWalletRequest zzbQz;

    gn(gl glVar, GoogleApiClient googleApiClient, MaskedWalletRequest maskedWalletRequest, int i) {
        this.zzbQz = maskedWalletRequest;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gu guVar) {
        guVar.zza(this.zzbQz, this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
