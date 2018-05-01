package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;

public final class zzd<A extends BaseImplementation.ApiMethodImpl<? extends Result, Api.AnyClient>>
  extends zzb
{
  private final A zzdv;
  
  public zzd(int paramInt, A paramA)
  {
    super(paramInt);
    this.zzdv = paramA;
  }
  
  public final void zza(Status paramStatus)
  {
    this.zzdv.setFailedResult(paramStatus);
  }
  
  public final void zza(GoogleApiManager.zza<?> paramzza)
    throws DeadObjectException
  {
    try
    {
      this.zzdv.run(paramzza.zzae());
      return;
    }
    catch (RuntimeException paramzza)
    {
      for (;;)
      {
        zza(paramzza);
      }
    }
  }
  
  public final void zza(zzaa paramzzaa, boolean paramBoolean)
  {
    paramzzaa.zza(this.zzdv, paramBoolean);
  }
  
  public final void zza(RuntimeException paramRuntimeException)
  {
    String str = paramRuntimeException.getClass().getSimpleName();
    paramRuntimeException = paramRuntimeException.getLocalizedMessage();
    paramRuntimeException = new Status(10, String.valueOf(str).length() + 2 + String.valueOf(paramRuntimeException).length() + str + ": " + paramRuntimeException);
    this.zzdv.setFailedResult(paramRuntimeException);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */