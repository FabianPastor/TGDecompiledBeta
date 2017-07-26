package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.Wallet.zzb;

final class gr extends zzb {
    private /* synthetic */ int val$requestCode;

    gr(gl glVar, GoogleApiClient googleApiClient, int i) {
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(gu guVar) {
        guVar.zzbQ(this.val$requestCode);
        setResult(Status.zzaBm);
    }
}
