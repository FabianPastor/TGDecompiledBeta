package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class zzdlk extends zzb {
    private /* synthetic */ int val$requestCode;
    private /* synthetic */ FullWalletRequest zzlfm;

    zzdlk(zzdlh com_google_android_gms_internal_zzdlh, GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i) {
        this.zzlfm = fullWalletRequest;
        this.val$requestCode = i;
        super(googleApiClient);
    }

    protected final void zza(zzdlo com_google_android_gms_internal_zzdlo) {
        com_google_android_gms_internal_zzdlo.zza(this.zzlfm, this.val$requestCode);
        setResult(Status.zzfni);
    }
}
