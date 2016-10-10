package com.google.android.gms.tasks;

import android.support.annotation.NonNull;

public class TaskCompletionSource<TResult>
{
  private final zzh<TResult> aJH = new zzh();
  
  @NonNull
  public Task<TResult> getTask()
  {
    return this.aJH;
  }
  
  public void setException(@NonNull Exception paramException)
  {
    this.aJH.setException(paramException);
  }
  
  public void setResult(TResult paramTResult)
  {
    this.aJH.setResult(paramTResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/TaskCompletionSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */