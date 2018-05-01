package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Payments;

@SuppressLint({"MissingRemoteException"})
public final class zzdlh
  implements Payments
{
  public final PendingResult<BooleanResult> isReadyToPay(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.zzd(new zzdlm(this, paramGoogleApiClient));
  }
  
  public final void loadFullWallet(GoogleApiClient paramGoogleApiClient, FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    paramGoogleApiClient.zzd(new zzdlk(this, paramGoogleApiClient, paramFullWalletRequest, paramInt));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */