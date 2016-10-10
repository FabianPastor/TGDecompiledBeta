package com.google.android.gms.tasks;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzra;
import com.google.android.gms.internal.zzrb;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

final class zzh<TResult>
  extends Task<TResult>
{
  private final zzg<TResult> aJJ = new zzg();
  private boolean aJK;
  private TResult aJL;
  private Exception aJM;
  private final Object zzakd = new Object();
  
  private void zzclh()
  {
    zzac.zza(this.aJK, "Task is not yet complete");
  }
  
  private void zzcli()
  {
    if (!this.aJK) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "Task is already complete");
      return;
    }
  }
  
  private void zzclj()
  {
    synchronized (this.zzakd)
    {
      if (!this.aJK) {
        return;
      }
      this.aJJ.zza(this);
      return;
    }
  }
  
  @NonNull
  public Task<TResult> addOnCompleteListener(@NonNull Activity paramActivity, @NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    paramOnCompleteListener = new zzc(TaskExecutors.MAIN_THREAD, paramOnCompleteListener);
    this.aJJ.zza(paramOnCompleteListener);
    zza.zzv(paramActivity).zzb(paramOnCompleteListener);
    zzclj();
    return this;
  }
  
  @NonNull
  public Task<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    return addOnCompleteListener(TaskExecutors.MAIN_THREAD, paramOnCompleteListener);
  }
  
  @NonNull
  public Task<TResult> addOnCompleteListener(@NonNull Executor paramExecutor, @NonNull OnCompleteListener<TResult> paramOnCompleteListener)
  {
    this.aJJ.zza(new zzc(paramExecutor, paramOnCompleteListener));
    zzclj();
    return this;
  }
  
  @NonNull
  public Task<TResult> addOnFailureListener(@NonNull Activity paramActivity, @NonNull OnFailureListener paramOnFailureListener)
  {
    paramOnFailureListener = new zzd(TaskExecutors.MAIN_THREAD, paramOnFailureListener);
    this.aJJ.zza(paramOnFailureListener);
    zza.zzv(paramActivity).zzb(paramOnFailureListener);
    zzclj();
    return this;
  }
  
  @NonNull
  public Task<TResult> addOnFailureListener(@NonNull OnFailureListener paramOnFailureListener)
  {
    return addOnFailureListener(TaskExecutors.MAIN_THREAD, paramOnFailureListener);
  }
  
  @NonNull
  public Task<TResult> addOnFailureListener(@NonNull Executor paramExecutor, @NonNull OnFailureListener paramOnFailureListener)
  {
    this.aJJ.zza(new zzd(paramExecutor, paramOnFailureListener));
    zzclj();
    return this;
  }
  
  @NonNull
  public Task<TResult> addOnSuccessListener(@NonNull Activity paramActivity, @NonNull OnSuccessListener<? super TResult> paramOnSuccessListener)
  {
    paramOnSuccessListener = new zze(TaskExecutors.MAIN_THREAD, paramOnSuccessListener);
    this.aJJ.zza(paramOnSuccessListener);
    zza.zzv(paramActivity).zzb(paramOnSuccessListener);
    zzclj();
    return this;
  }
  
  @NonNull
  public Task<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> paramOnSuccessListener)
  {
    return addOnSuccessListener(TaskExecutors.MAIN_THREAD, paramOnSuccessListener);
  }
  
  @NonNull
  public Task<TResult> addOnSuccessListener(@NonNull Executor paramExecutor, @NonNull OnSuccessListener<? super TResult> paramOnSuccessListener)
  {
    this.aJJ.zza(new zze(paramExecutor, paramOnSuccessListener));
    zzclj();
    return this;
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> paramContinuation)
  {
    return continueWith(TaskExecutors.MAIN_THREAD, paramContinuation);
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, TContinuationResult> paramContinuation)
  {
    zzh localzzh = new zzh();
    this.aJJ.zza(new zza(paramExecutor, paramContinuation, localzzh));
    zzclj();
    return localzzh;
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> paramContinuation)
  {
    return continueWithTask(TaskExecutors.MAIN_THREAD, paramContinuation);
  }
  
  @NonNull
  public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor paramExecutor, @NonNull Continuation<TResult, Task<TContinuationResult>> paramContinuation)
  {
    zzh localzzh = new zzh();
    this.aJJ.zza(new zzb(paramExecutor, paramContinuation, localzzh));
    zzclj();
    return localzzh;
  }
  
  @Nullable
  public Exception getException()
  {
    synchronized (this.zzakd)
    {
      Exception localException = this.aJM;
      return localException;
    }
  }
  
  public TResult getResult()
  {
    synchronized (this.zzakd)
    {
      zzclh();
      if (this.aJM != null) {
        throw new RuntimeExecutionException(this.aJM);
      }
    }
    Object localObject3 = this.aJL;
    return (TResult)localObject3;
  }
  
  public <X extends Throwable> TResult getResult(@NonNull Class<X> paramClass)
    throws Throwable
  {
    synchronized (this.zzakd)
    {
      zzclh();
      if (paramClass.isInstance(this.aJM)) {
        throw ((Throwable)paramClass.cast(this.aJM));
      }
    }
    if (this.aJM != null) {
      throw new RuntimeExecutionException(this.aJM);
    }
    paramClass = this.aJL;
    return paramClass;
  }
  
  public boolean isComplete()
  {
    synchronized (this.zzakd)
    {
      boolean bool = this.aJK;
      return bool;
    }
  }
  
  public boolean isSuccessful()
  {
    for (;;)
    {
      synchronized (this.zzakd)
      {
        if ((this.aJK) && (this.aJM == null))
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
  
  public void setException(@NonNull Exception paramException)
  {
    zzac.zzb(paramException, "Exception must not be null");
    synchronized (this.zzakd)
    {
      zzcli();
      this.aJK = true;
      this.aJM = paramException;
      this.aJJ.zza(this);
      return;
    }
  }
  
  public void setResult(TResult paramTResult)
  {
    synchronized (this.zzakd)
    {
      zzcli();
      this.aJK = true;
      this.aJL = paramTResult;
      this.aJJ.zza(this);
      return;
    }
  }
  
  private static class zza
    extends zzra
  {
    private final List<WeakReference<zzf<?>>> mListeners = new ArrayList();
    
    private zza(zzrb paramzzrb)
    {
      super();
      this.yY.zza("TaskOnStopCallback", this);
    }
    
    public static zza zzv(Activity paramActivity)
    {
      zzrb localzzrb = zzs(paramActivity);
      zza localzza = (zza)localzzrb.zza("TaskOnStopCallback", zza.class);
      paramActivity = localzza;
      if (localzza == null) {
        paramActivity = new zza(localzzrb);
      }
      return paramActivity;
    }
    
    @MainThread
    public void onStop()
    {
      synchronized (this.mListeners)
      {
        Iterator localIterator = this.mListeners.iterator();
        while (localIterator.hasNext())
        {
          zzf localzzf = (zzf)((WeakReference)localIterator.next()).get();
          if (localzzf != null) {
            localzzf.cancel();
          }
        }
      }
      this.mListeners.clear();
    }
    
    public <T> void zzb(zzf<T> paramzzf)
    {
      synchronized (this.mListeners)
      {
        this.mListeners.add(new WeakReference(paramzzf));
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/tasks/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */