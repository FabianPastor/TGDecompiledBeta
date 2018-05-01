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
public final class gl
  implements Payments
{
  public final void changeMaskedWallet(GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, int paramInt)
  {
    paramGoogleApiClient.zzd(new gp(this, paramGoogleApiClient, paramString1, paramString2, paramInt));
  }
  
  public final void checkForPreAuthorization(GoogleApiClient paramGoogleApiClient, int paramInt)
  {
    paramGoogleApiClient.zzd(new gm(this, paramGoogleApiClient, paramInt));
  }
  
  public final void isNewUser(GoogleApiClient paramGoogleApiClient, int paramInt)
  {
    paramGoogleApiClient.zzd(new gr(this, paramGoogleApiClient, paramInt));
  }
  
  public final PendingResult<BooleanResult> isReadyToPay(GoogleApiClient paramGoogleApiClient)
  {
    return paramGoogleApiClient.zzd(new gs(this, paramGoogleApiClient));
  }
  
  public final PendingResult<BooleanResult> isReadyToPay(GoogleApiClient paramGoogleApiClient, IsReadyToPayRequest paramIsReadyToPayRequest)
  {
    return paramGoogleApiClient.zzd(new gt(this, paramGoogleApiClient, paramIsReadyToPayRequest));
  }
  
  public final void loadFullWallet(GoogleApiClient paramGoogleApiClient, FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    paramGoogleApiClient.zzd(new go(this, paramGoogleApiClient, paramFullWalletRequest, paramInt));
  }
  
  public final void loadMaskedWallet(GoogleApiClient paramGoogleApiClient, MaskedWalletRequest paramMaskedWalletRequest, int paramInt)
  {
    paramGoogleApiClient.zzd(new gn(this, paramGoogleApiClient, paramMaskedWalletRequest, paramInt));
  }
  
  public final void notifyTransactionStatus(GoogleApiClient paramGoogleApiClient, NotifyTransactionStatusRequest paramNotifyTransactionStatusRequest)
  {
    paramGoogleApiClient.zzd(new gq(this, paramGoogleApiClient, paramNotifyTransactionStatusRequest));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */