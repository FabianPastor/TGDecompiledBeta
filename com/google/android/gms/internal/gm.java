package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class gm extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ MaskedWalletRequest zzbQx;

    gm(gk gkVar, GoogleApiClient googleApiClient, MaskedWalletRequest maskedWalletRequest, int i) {
        this.zzbQx = maskedWalletRequest;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gt gtVar) {
        gtVar.zza(this.zzbQx, this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
