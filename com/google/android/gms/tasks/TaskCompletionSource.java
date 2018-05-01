package com.google.android.gms.tasks;

public class TaskCompletionSource<TResult>
{
  private final zzu<TResult> zzafh = new zzu();
  
  public Task<TResult> getTask()
  {
    return this.zzafh;
  }
  
  public void setException(Exception paramException)
  {
    this.zzafh.setException(paramException);
  }
  
  public void setResult(TResult paramTResult)
  {
    this.zzafh.setResult(paramTResult);
  }
  
  public boolean trySetException(Exception paramException)
  {
    return this.zzafh.trySetException(paramException);
  }
  
  public boolean trySetResult(TResult paramTResult)
  {
    return this.zzafh.trySetResult(paramTResult);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/TaskCompletionSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */