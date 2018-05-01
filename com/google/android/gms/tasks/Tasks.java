package com.google.android.gms.tasks;

import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class Tasks
{
  public static <TResult> TResult await(Task<TResult> paramTask)
    throws ExecutionException, InterruptedException
  {
    Preconditions.checkNotMainThread();
    Preconditions.checkNotNull(paramTask, "Task must not be null");
    if (paramTask.isComplete()) {}
    for (paramTask = zzb(paramTask);; paramTask = zzb(paramTask))
    {
      return paramTask;
      zza localzza = new zza(null);
      zza(paramTask, localzza);
      localzza.await();
    }
  }
  
  public static <TResult> TResult await(Task<TResult> paramTask, long paramLong, TimeUnit paramTimeUnit)
    throws ExecutionException, InterruptedException, TimeoutException
  {
    Preconditions.checkNotMainThread();
    Preconditions.checkNotNull(paramTask, "Task must not be null");
    Preconditions.checkNotNull(paramTimeUnit, "TimeUnit must not be null");
    if (paramTask.isComplete()) {}
    for (paramTask = zzb(paramTask);; paramTask = zzb(paramTask))
    {
      return paramTask;
      zza localzza = new zza(null);
      zza(paramTask, localzza);
      if (!localzza.await(paramLong, paramTimeUnit)) {
        throw new TimeoutException("Timed out waiting for Task");
      }
    }
  }
  
  public static <TResult> Task<TResult> call(Executor paramExecutor, Callable<TResult> paramCallable)
  {
    Preconditions.checkNotNull(paramExecutor, "Executor must not be null");
    Preconditions.checkNotNull(paramCallable, "Callback must not be null");
    zzu localzzu = new zzu();
    paramExecutor.execute(new zzv(localzzu, paramCallable));
    return localzzu;
  }
  
  public static <TResult> Task<TResult> forException(Exception paramException)
  {
    zzu localzzu = new zzu();
    localzzu.setException(paramException);
    return localzzu;
  }
  
  public static <TResult> Task<TResult> forResult(TResult paramTResult)
  {
    zzu localzzu = new zzu();
    localzzu.setResult(paramTResult);
    return localzzu;
  }
  
  private static void zza(Task<?> paramTask, zzb paramzzb)
  {
    paramTask.addOnSuccessListener(TaskExecutors.zzagd, paramzzb);
    paramTask.addOnFailureListener(TaskExecutors.zzagd, paramzzb);
    paramTask.addOnCanceledListener(TaskExecutors.zzagd, paramzzb);
  }
  
  private static <TResult> TResult zzb(Task<TResult> paramTask)
    throws ExecutionException
  {
    if (paramTask.isSuccessful()) {
      return (TResult)paramTask.getResult();
    }
    if (paramTask.isCanceled()) {
      throw new CancellationException("Task is already canceled");
    }
    throw new ExecutionException(paramTask.getException());
  }
  
  private static final class zza
    implements Tasks.zzb
  {
    private final CountDownLatch zzfd = new CountDownLatch(1);
    
    public final void await()
      throws InterruptedException
    {
      this.zzfd.await();
    }
    
    public final boolean await(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return this.zzfd.await(paramLong, paramTimeUnit);
    }
    
    public final void onCanceled()
    {
      this.zzfd.countDown();
    }
    
    public final void onFailure(Exception paramException)
    {
      this.zzfd.countDown();
    }
    
    public final void onSuccess(Object paramObject)
    {
      this.zzfd.countDown();
    }
  }
  
  static abstract interface zzb
    extends OnCanceledListener, OnFailureListener, OnSuccessListener<Object>
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/Tasks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */