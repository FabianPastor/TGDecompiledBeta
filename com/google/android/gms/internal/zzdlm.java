package com.google.android.gms.internal;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest.Builder;
import com.google.android.gms.wallet.Wallet.zza;

final class zzdlm
  extends Wallet.zza<BooleanResult>
{
  zzdlm(zzdlh paramzzdlh, GoogleApiClient paramGoogleApiClient)
  {
    super(paramGoogleApiClient);
  }
  
  protected final void zza(zzdlo paramzzdlo)
  {
    paramzzdlo.zza(IsReadyToPayRequest.newBuilder().build(), this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */