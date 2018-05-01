package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class RegisterListenerMethod<A extends Api.AnyClient, L>
{
  private final ListenerHolder<L> zzls;
  
  public void clearListener()
  {
    this.zzls.clear();
  }
  
  protected abstract void registerListener(A paramA, TaskCompletionSource<Void> paramTaskCompletionSource)
    throws RemoteException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/RegisterListenerMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */