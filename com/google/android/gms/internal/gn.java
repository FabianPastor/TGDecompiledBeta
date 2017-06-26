package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class gn extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ FullWalletRequest zzbQy;

    gn(gk gkVar, GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i) {
        this.zzbQy = fullWalletRequest;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gt gtVar) {
        gtVar.zza(this.zzbQy, this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
