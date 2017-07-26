package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.Payments;

@SuppressLint({"MissingRemoteException"})
public final class gl implements Payments {
    public final void changeMaskedWallet(GoogleApiClient googleApiClient, String str, String str2, int i) {
        googleApiClient.zzd(new gp(this, googleApiClient, str, str2, i));
    }

    public final void checkForPreAuthorization(GoogleApiClient googleApiClient, int i) {
        googleApiClient.zzd(new gm(this, googleApiClient, i));
    }

    public final void isNewUser(GoogleApiClient googleApiClient, int i) {
        googleApiClient.zzd(new gr(this, googleApiClient, i));
    }

    public final PendingResult<BooleanResult> isReadyToPay(GoogleApiClient googleApiClient) {
        return googleApiClient.zzd(new gs(this, googleApiClient));
    }

    public final PendingResult<BooleanResult> isReadyToPay(GoogleApiClient googleApiClient, IsReadyToPayRequest isReadyToPayRequest) {
        return googleApiClient.zzd(new gt(this, googleApiClient, isReadyToPayRequest));
    }

    public final void loadFullWallet(GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i) {
        googleApiClient.zzd(new go(this, googleApiClient, fullWalletRequest, i));
    }

    public final void loadMaskedWallet(GoogleApiClient googleApiClient, MaskedWalletRequest maskedWalletRequest, int i) {
        googleApiClient.zzd(new gn(this, googleApiClient, maskedWalletRequest, i));
    }

    public final void notifyTransactionStatus(GoogleApiClient googleApiClient, NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        googleApiClient.zzd(new gq(this, googleApiClient, notifyTransactionStatusRequest));
    }
}
