package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzcq<A extends Api.zzb, L>
{
  private final zzci<L> zzfus;
  
  public final void zzajp()
  {
    this.zzfus.clear();
  }
  
  protected abstract void zzb(A paramA, TaskCompletionSource<Void> paramTaskCompletionSource)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzcq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */