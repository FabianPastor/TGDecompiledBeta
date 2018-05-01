package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder;

final class zzai
  extends zzaf
{
  private final BaseImplementation.ResultHolder<BooleanResult> zzgk;
  
  public zzai(BaseImplementation.ResultHolder<BooleanResult> paramResultHolder)
  {
    this.zzgk = paramResultHolder;
  }
  
  public final void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle)
  {
    this.zzgk.setResult(new BooleanResult(paramStatus, paramBoolean));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/wallet/zzai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */