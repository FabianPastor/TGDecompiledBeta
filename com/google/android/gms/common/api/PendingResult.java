package com.google.android.gms.common.api;

import java.util.concurrent.TimeUnit;

public abstract class PendingResult<R extends Result>
{
  public void addStatusListener(StatusListener paramStatusListener)
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract R await();
  
  public abstract R await(long paramLong, TimeUnit paramTimeUnit);
  
  public abstract void cancel();
  
  public abstract boolean isCanceled();
  
  public abstract void setResultCallback(ResultCallback<? super R> paramResultCallback);
  
  public Integer zzo()
  {
    throw new UnsupportedOperationException();
  }
  
  public static abstract interface StatusListener
  {
    public abstract void onComplete(Status paramStatus);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/PendingResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */