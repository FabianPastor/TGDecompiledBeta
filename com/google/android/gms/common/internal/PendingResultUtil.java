package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class PendingResultUtil
{
  private static final StatusConverter zzun = new zzk();
  
  public static <R extends Result, T> Task<T> toTask(PendingResult<R> paramPendingResult, ResultConverter<R, T> paramResultConverter)
  {
    return toTask(paramPendingResult, paramResultConverter, zzun);
  }
  
  public static <R extends Result, T> Task<T> toTask(PendingResult<R> paramPendingResult, ResultConverter<R, T> paramResultConverter, StatusConverter paramStatusConverter)
  {
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramPendingResult.addStatusListener(new zzl(paramPendingResult, localTaskCompletionSource, paramResultConverter, paramStatusConverter));
    return localTaskCompletionSource.getTask();
  }
  
  public static abstract interface ResultConverter<R extends Result, T>
  {
    public abstract T convert(R paramR);
  }
  
  public static abstract interface StatusConverter
  {
    public abstract ApiException convert(Status paramStatus);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/PendingResultUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */