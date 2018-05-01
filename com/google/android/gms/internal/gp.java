package com.google.android.gms.internal;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.Wallet.zzb;

final class gp
  extends Wallet.zzb
{
  gp(gl paramgl, GoogleApiClient paramGoogleApiClient, String paramString1, String paramString2, int paramInt)
  {
    super(paramGoogleApiClient);
  }
  
  protected final void zza(gu paramgu)
  {
    paramgu.zzc(this.zzbQB, this.zzbQC, this.val$requestCode);
    setResult(Status.zzaBm);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/gp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */