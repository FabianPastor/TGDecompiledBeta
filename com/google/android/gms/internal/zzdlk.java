package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BasePendingResult;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class zzdlk
  extends Wallet.zzb
{
  zzdlk(zzdlh paramzzdlh, GoogleApiClient paramGoogleApiClient, FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    super(paramGoogleApiClient);
  }
  
  protected final void zza(zzdlo paramzzdlo)
  {
    paramzzdlo.zza(this.zzlfm, this.val$requestCode);
    setResult(Status.zzfni);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */