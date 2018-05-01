package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbaq<TResult>
  extends zzbam
{
  private final zzbeq<Api.zzb, TResult> zzaBw;
  private final zzbem zzaBx;
  private final TaskCompletionSource<TResult> zzalE;
  
  public zzbaq(int paramInt, zzbeq<Api.zzb, TResult> paramzzbeq, TaskCompletionSource<TResult> paramTaskCompletionSource, zzbem paramzzbem)
  {
    super(paramInt);
    this.zzalE = paramTaskCompletionSource;
    this.zzaBw = paramzzbeq;
    this.zzaBx = paramzzbem;
  }
  
  public final void zza(@NonNull zzbbt paramzzbbt, boolean paramBoolean)
  {
    paramzzbbt.zza(this.zzalE, paramBoolean);
  }
  
  public final void zza(zzbdd<?> paramzzbdd)
    throws DeadObjectException
  {
    try
    {
      this.zzaBw.zza(paramzzbdd.zzpJ(), this.zzalE);
      return;
    }
    catch (DeadObjectException paramzzbdd)
    {
      throw paramzzbdd;
    }
    catch (RemoteException paramzzbdd)
    {
      zzp(zzbam.zzb(paramzzbdd));
    }
  }
  
  public final void zzp(@NonNull Status paramStatus)
  {
    this.zzalE.trySetException(this.zzaBx.zzq(paramStatus));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbaq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */