package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;

public interface ge extends IInterface {
    void zza(Bundle bundle, gi giVar) throws RemoteException;

    void zza(FullWalletRequest fullWalletRequest, Bundle bundle, gi giVar) throws RemoteException;

    void zza(IsReadyToPayRequest isReadyToPayRequest, Bundle bundle, gi giVar) throws RemoteException;

    void zza(MaskedWalletRequest maskedWalletRequest, Bundle bundle, gi giVar) throws RemoteException;

    void zza(NotifyTransactionStatusRequest notifyTransactionStatusRequest, Bundle bundle) throws RemoteException;

    void zza(String str, String str2, Bundle bundle, gi giVar) throws RemoteException;

    void zzb(Bundle bundle, gi giVar) throws RemoteException;
}
