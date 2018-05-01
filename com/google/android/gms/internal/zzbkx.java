package com.google.android.gms.internal;

import android.annotation.SuppressLint;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest.Builder;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.NotifyTransactionStatusRequest;
import com.google.android.gms.wallet.Payments;
import com.google.android.gms.wallet.Wallet.zza;
import com.google.android.gms.wallet.Wallet.zzb;

@SuppressLint({"MissingRemoteException"})
public class zzbkx
  implements Payments
{
  public void changeMaskedWallet(GoogleApiClient paramGoogleApiClient, final String paramString1, final String paramString2, final int paramInt)
  {
    paramGoogleApiClient.zza(new Wallet.zzb(paramGoogleApiClient)
    {
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zzf(paramString1, paramString2, paramInt);
        zzb(Status.zzazx);
      }
    });
  }
  
  public void checkForPreAuthorization(GoogleApiClient paramGoogleApiClient, final int paramInt)
  {
    paramGoogleApiClient.zza(new Wallet.zzb(paramGoogleApiClient)
    {
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zzoY(paramInt);
        zzb(Status.zzazx);
      }
    });
  }
  
  public void isNewUser(GoogleApiClient paramGoogleApiClient, final int paramInt)
  {
    paramGoogleApiClient.zza(new Wallet.zzb(paramGoogleApiClient)
    {
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zzoZ(paramInt);
        zzb(Status.zzazx);
      }
    });
  }
  
  public PendingResult<BooleanResult> isReadyToPay(GoogleApiClient paramGoogleApiClient)
  {
    paramGoogleApiClient.zza(new Wallet.zza(paramGoogleApiClient)
    {
      protected BooleanResult zzM(Status paramAnonymousStatus)
      {
        return new BooleanResult(paramAnonymousStatus, false);
      }
      
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zza(IsReadyToPayRequest.newBuilder().build(), this);
      }
    });
  }
  
  public PendingResult<BooleanResult> isReadyToPay(GoogleApiClient paramGoogleApiClient, final IsReadyToPayRequest paramIsReadyToPayRequest)
  {
    paramGoogleApiClient.zza(new Wallet.zza(paramGoogleApiClient)
    {
      protected BooleanResult zzM(Status paramAnonymousStatus)
      {
        return new BooleanResult(paramAnonymousStatus, false);
      }
      
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zza(paramIsReadyToPayRequest, this);
      }
    });
  }
  
  public void loadFullWallet(GoogleApiClient paramGoogleApiClient, final FullWalletRequest paramFullWalletRequest, final int paramInt)
  {
    paramGoogleApiClient.zza(new Wallet.zzb(paramGoogleApiClient)
    {
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zza(paramFullWalletRequest, paramInt);
        zzb(Status.zzazx);
      }
    });
  }
  
  public void loadMaskedWallet(GoogleApiClient paramGoogleApiClient, final MaskedWalletRequest paramMaskedWalletRequest, final int paramInt)
  {
    paramGoogleApiClient.zza(new Wallet.zzb(paramGoogleApiClient)
    {
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zza(paramMaskedWalletRequest, paramInt);
        zzb(Status.zzazx);
      }
    });
  }
  
  public void notifyTransactionStatus(GoogleApiClient paramGoogleApiClient, final NotifyTransactionStatusRequest paramNotifyTransactionStatusRequest)
  {
    paramGoogleApiClient.zza(new Wallet.zzb(paramGoogleApiClient)
    {
      protected void zza(zzbky paramAnonymouszzbky)
      {
        paramAnonymouszzbky.zza(paramNotifyTransactionStatusRequest);
        zzb(Status.zzazx);
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbkx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */