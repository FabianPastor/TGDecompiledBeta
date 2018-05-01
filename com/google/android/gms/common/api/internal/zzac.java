package com.google.android.gms.common.api.internal;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

final class zzac
  implements OnCompleteListener<TResult>
{
  zzac(zzaa paramzzaa, TaskCompletionSource paramTaskCompletionSource) {}
  
  public final void onComplete(Task<TResult> paramTask)
  {
    zzaa.zzb(this.zzgz).remove(this.zzha);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */