package com.google.android.gms.internal;

import android.os.DeadObjectException;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.tasks.TaskCompletionSource;

public abstract class zzrr<A extends Api.zzb>
{
  public zzrd.zzb<?> zzasr()
  {
    return null;
  }
  
  protected abstract void zzc(A paramA, TaskCompletionSource<Void> paramTaskCompletionSource)
    throws DeadObjectException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */