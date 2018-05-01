package com.google.android.gms.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzbee<A extends Api.zzb, L>
{
  private final zzbdw<L> zzaEU;
  
  protected zzbee(zzbdw<L> paramzzbdw)
  {
    this.zzaEU = paramzzbdw;
  }
  
  protected abstract void zzb(A paramA, TaskCompletionSource<Void> paramTaskCompletionSource)
    throws RemoteException;
  
  public final zzbdy<L> zzqG()
  {
    return this.zzaEU.zzqG();
  }
  
  public final void zzqH()
  {
    this.zzaEU.clear();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbee.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */