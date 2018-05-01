package com.google.android.gms.tasks;

import com.google.android.gms.common.internal.Preconditions;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import javax.annotation.concurrent.GuardedBy;

final class zzu<TResult>
  extends Task<TResult>
{
  private final Object mLock = new Object();
  private final zzr<TResult> zzage = new zzr();
  @GuardedBy("mLock")
  private boolean zzagf;
  @GuardedBy("mLock")
  private TResult zzagg;
  @GuardedBy("mLock")
  private Exception zzagh;
  private volatile boolean zzfi;
  
  @GuardedBy("mLock")
  private final void zzdq()
  {
    Preconditions.checkState(this.zzagf, "Task is not yet complete");
  }
  
  @GuardedBy("mLock")
  private final void zzdr()
  {
    if (!this.zzagf) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool, "Task is already complete");
      return;
    }
  }
  
  @GuardedBy("mLock")
  private final void zzds()
  {
    if (this.zzfi) {
      throw new CancellationException("Task is already canceled.");
    }
  }
  
  private final void zzdt()
  {
    synchronized (this.mLock)
    {
      if (!this.zzagf) {
        return;
      }
      this.zzage.zza(this);
    }
  }
  
  public final Task<TResult> addOnCanceledListener(Executor paramExecutor, OnCanceledListener paramOnCanceledListener)
  {
    this.zzage.zza(new zzg(paramExecutor, paramOnCanceledListener));
    zzdt();
    return this;
  }
  
  public final Task<TResult> addOnCompleteListener(OnCompleteListener<TResult> paramOnCompleteListener)
  {
    return addOnCompleteListener(TaskExecutors.MAIN_THREAD, paramOnCompleteListener);
  }
  
  public final Task<TResult> addOnCompleteListener(Executor paramExecutor, OnCompleteListener<TResult> paramOnCompleteListener)
  {
    this.zzage.zza(new zzi(paramExecutor, paramOnCompleteListener));
    zzdt();
    return this;
  }
  
  public final Task<TResult> addOnFailureListener(Executor paramExecutor, OnFailureListener paramOnFailureListener)
  {
    this.zzage.zza(new zzk(paramExecutor, paramOnFailureListener));
    zzdt();
    return this;
  }
  
  public final Task<TResult> addOnSuccessListener(Executor paramExecutor, OnSuccessListener<? super TResult> paramOnSuccessListener)
  {
    this.zzage.zza(new zzm(paramExecutor, paramOnSuccessListener));
    zzdt();
    return this;
  }
  
  public final Exception getException()
  {
    synchronized (this.mLock)
    {
      Exception localException = this.zzagh;
      return localException;
    }
  }
  
  public final TResult getResult()
  {
    synchronized (this.mLock)
    {
      zzdq();
      zzds();
      if (this.zzagh != null)
      {
        RuntimeExecutionException localRuntimeExecutionException = new com/google/android/gms/tasks/RuntimeExecutionException;
        localRuntimeExecutionException.<init>(this.zzagh);
        throw localRuntimeExecutionException;
      }
    }
    Object localObject3 = this.zzagg;
    return (TResult)localObject3;
  }
  
  public final boolean isCanceled()
  {
    return this.zzfi;
  }
  
  public final boolean isComplete()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.zzagf;
      return bool;
    }
  }
  
  public final boolean isSuccessful()
  {
    synchronized (this.mLock)
    {
      if ((this.zzagf) && (!this.zzfi) && (this.zzagh == null))
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public final void setException(Exception paramException)
  {
    Preconditions.checkNotNull(paramException, "Exception must not be null");
    synchronized (this.mLock)
    {
      zzdr();
      this.zzagf = true;
      this.zzagh = paramException;
      this.zzage.zza(this);
      return;
    }
  }
  
  public final void setResult(TResult paramTResult)
  {
    synchronized (this.mLock)
    {
      zzdr();
      this.zzagf = true;
      this.zzagg = paramTResult;
      this.zzage.zza(this);
      return;
    }
  }
  
  public final boolean trySetException(Exception paramException)
  {
    boolean bool = true;
    Preconditions.checkNotNull(paramException, "Exception must not be null");
    synchronized (this.mLock)
    {
      if (this.zzagf)
      {
        bool = false;
        return bool;
      }
      this.zzagf = true;
      this.zzagh = paramException;
      this.zzage.zza(this);
    }
  }
  
  public final boolean trySetResult(TResult paramTResult)
  {
    boolean bool = true;
    synchronized (this.mLock)
    {
      if (this.zzagf)
      {
        bool = false;
        return bool;
      }
      this.zzagf = true;
      this.zzagg = paramTResult;
      this.zzage.zza(this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */