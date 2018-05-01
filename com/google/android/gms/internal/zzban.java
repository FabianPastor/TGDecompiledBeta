package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

abstract class zzban
  extends zzbam
{
  protected final TaskCompletionSource<Void> zzalE;
  
  public zzban(int paramInt, TaskCompletionSource<Void> paramTaskCompletionSource)
  {
    super(paramInt);
    this.zzalE = paramTaskCompletionSource;
  }
  
  public void zza(@NonNull zzbbt paramzzbbt, boolean paramBoolean) {}
  
  public final void zza(zzbdd<?> paramzzbdd)
    throws DeadObjectException
  {
    try
    {
      zzb(paramzzbdd);
      return;
    }
    catch (DeadObjectException paramzzbdd)
    {
      zzp(zzbam.zzb(paramzzbdd));
      throw paramzzbdd;
    }
    catch (RemoteException paramzzbdd)
    {
      zzp(zzbam.zzb(paramzzbdd));
    }
  }
  
  protected abstract void zzb(zzbdd<?> paramzzbdd)
    throws RemoteException;
  
  public void zzp(@NonNull Status paramStatus)
  {
    this.zzalE.trySetException(new ApiException(paramStatus));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzban.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */