package com.google.android.gms.internal;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.Wallet.zza;

final class gr extends zza<BooleanResult> {
    gr(gk gkVar, GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    protected final void zza(gt gtVar) {
        gtVar.zza(IsReadyToPayRequest.newBuilder().build(), (zzbay) this);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new BooleanResult(status, false);
    }
}
