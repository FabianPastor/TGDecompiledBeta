package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class TaskApiCall<A extends Api.AnyClient, ResultT>
{
  private final Feature[] zzlz;
  private final boolean zzma;
  
  protected abstract void doExecute(A paramA, TaskCompletionSource<ResultT> paramTaskCompletionSource)
    throws RemoteException;
  
  public boolean shouldAutoResolveMissingFeatures()
  {
    return this.zzma;
  }
  
  public final Feature[] zzca()
  {
    return this.zzlz;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/TaskApiCall.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */