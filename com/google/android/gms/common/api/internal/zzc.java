package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzc<T>
  extends zzb
{
  protected final TaskCompletionSource<T> zzdu;
  
  public zzc(int paramInt, TaskCompletionSource<T> paramTaskCompletionSource)
  {
    super(paramInt);
    this.zzdu = paramTaskCompletionSource;
  }
  
  public void zza(Status paramStatus)
  {
    this.zzdu.trySetException(new ApiException(paramStatus));
  }
  
  public final void zza(GoogleApiManager.zza<?> paramzza)
    throws DeadObjectException
  {
    try
    {
      zzb(paramzza);
      return;
    }
    catch (DeadObjectException paramzza)
    {
      zza(zzb.zzb(paramzza));
      throw paramzza;
    }
    catch (RemoteException paramzza)
    {
      for (;;)
      {
        zza(zzb.zzb(paramzza));
      }
    }
    catch (RuntimeException paramzza)
    {
      for (;;)
      {
        zza(paramzza);
      }
    }
  }
  
  public void zza(zzaa paramzzaa, boolean paramBoolean) {}
  
  public void zza(RuntimeException paramRuntimeException)
  {
    this.zzdu.trySetException(paramRuntimeException);
  }
  
  protected abstract void zzb(GoogleApiManager.zza<?> paramzza)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */