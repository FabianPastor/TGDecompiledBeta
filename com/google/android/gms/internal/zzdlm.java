package com.google.android.gms.internal;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzn;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.Wallet.zza;

final class zzdlm extends zza<BooleanResult> {
    zzdlm(zzdlh com_google_android_gms_internal_zzdlh, GoogleApiClient googleApiClient) {
        super(googleApiClient);
    }

    protected final void zza(zzdlo com_google_android_gms_internal_zzdlo) {
        com_google_android_gms_internal_zzdlo.zza(IsReadyToPayRequest.newBuilder().build(), (zzn) this);
    }

    protected final /* synthetic */ Result zzb(Status status) {
        return new BooleanResult(status, false);
    }
}
