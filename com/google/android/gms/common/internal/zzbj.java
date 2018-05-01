package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public final class zzbj
{
  private static final zzbp zzgbf = new zzbk();
  
  public static <R extends Result, T> Task<T> zza(PendingResult<R> paramPendingResult, zzbo<R, T> paramzzbo)
  {
    zzbp localzzbp = zzgbf;
    TaskCompletionSource localTaskCompletionSource = new TaskCompletionSource();
    paramPendingResult.zza(new zzbl(paramPendingResult, localTaskCompletionSource, paramzzbo, localzzbp));
    return localTaskCompletionSource.getTask();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */