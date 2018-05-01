package com.google.android.gms.internal;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest.Builder;
import com.google.android.gms.wallet.Wallet.zza;

final class gs
  extends Wallet.zza<BooleanResult>
{
  gs(gl paramgl, GoogleApiClient paramGoogleApiClient)
  {
    super(paramGoogleApiClient);
  }
  
  protected final void zza(gu paramgu)
  {
    paramgu.zza(IsReadyToPayRequest.newBuilder().build(), this);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */