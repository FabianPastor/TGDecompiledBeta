package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.TimeUnit;

final class zzbl
  implements PendingResult.zza
{
  zzbl(PendingResult paramPendingResult, TaskCompletionSource paramTaskCompletionSource, zzbo paramzzbo, zzbp paramzzbp) {}
  
  public final void zzr(Status paramStatus)
  {
    if (paramStatus.isSuccess())
    {
      paramStatus = this.zzgbg.await(0L, TimeUnit.MILLISECONDS);
      this.zzgbh.setResult(this.zzgbi.zzb(paramStatus));
      return;
    }
    this.zzgbh.setException(this.zzgbj.zzz(paramStatus));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzbl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */