package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzn;

final class zzdlt
  extends zzdlq
{
  private final zzn<BooleanResult> zzgbw;
  
  public zzdlt(zzn<BooleanResult> paramzzn)
  {
    this.zzgbw = paramzzn;
  }
  
  public final void zza(Status paramStatus, boolean paramBoolean, Bundle paramBundle)
  {
    this.zzgbw.setResult(new BooleanResult(paramStatus, paramBoolean));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzdlt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */