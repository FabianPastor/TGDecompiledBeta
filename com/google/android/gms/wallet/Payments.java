package com.google.android.gms.wallet;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;

public interface Payments {
    @Deprecated
    PendingResult<BooleanResult> isReadyToPay(GoogleApiClient googleApiClient);

    void loadFullWallet(GoogleApiClient googleApiClient, FullWalletRequest fullWalletRequest, int i);
}
