package com.google.android.gms.common.api;

public abstract class ResultTransform<R extends Result, S extends Result>
{
  public Status onFailure(Status paramStatus)
  {
    return paramStatus;
  }
  
  public abstract PendingResult<S> onSuccess(R paramR);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/ResultTransform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */