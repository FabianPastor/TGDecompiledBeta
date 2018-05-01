package com.google.android.gms.internal.wallet;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.Wallet.zzb;

final class zzz
  extends Wallet.zzb
{
  zzz(zzw paramzzw, GoogleApiClient paramGoogleApiClient, FullWalletRequest paramFullWalletRequest, int paramInt)
  {
    super(paramGoogleApiClient);
  }
  
  protected final void zza(zzad paramzzad)
  {
    paramzzad.zza(this.zzge, this.val$requestCode);
    setResult(Status.RESULT_SUCCESS);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */