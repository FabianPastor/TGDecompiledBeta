package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Payments;

@SuppressLint({"MissingRemoteException"})
public final class zzdlh implements Payments {
    public final PendingResult<BooleanResult> isReadyToPay(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new zzdlm(this, googleApiClient));
    }

    public final void loadFullWallet(GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i) {
        googleApiClient.zzd(new zzdlk(this, googleApiClient, fullWalletRequest, i));
    }
}
