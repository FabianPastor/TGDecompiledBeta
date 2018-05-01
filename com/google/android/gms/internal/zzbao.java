package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzbao<A extends zzbay<? extends Result, Api.zzb>>
  extends zzbam
{
  private A zzaBt;
  
  public zzbao(int paramInt, A paramA)
  {
    super(paramInt);
    this.zzaBt = paramA;
  }
  
  public final void zza(@NonNull zzbbt paramzzbbt, boolean paramBoolean)
  {
    paramzzbbt.zza(this.zzaBt, paramBoolean);
  }
  
  public final void zza(zzbdd<?> paramzzbdd)
    throws DeadObjectException
  {
    this.zzaBt.zzb(paramzzbdd.zzpJ());
  }
  
  public final void zzp(@NonNull Status paramStatus)
  {
    this.zzaBt.zzr(paramStatus);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbao.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */