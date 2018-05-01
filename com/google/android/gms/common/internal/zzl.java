package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResult.StatusListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.TimeUnit;

final class zzl
  implements PendingResult.StatusListener
{
  zzl(PendingResult paramPendingResult, TaskCompletionSource paramTaskCompletionSource, PendingResultUtil.ResultConverter paramResultConverter, PendingResultUtil.StatusConverter paramStatusConverter) {}
  
  public final void onComplete(Status paramStatus)
  {
    if (paramStatus.isSuccess())
    {
      paramStatus = this.zzuo.await(0L, TimeUnit.MILLISECONDS);
      this.zzup.setResult(this.zzuq.convert(paramStatus));
    }
    for (;;)
    {
      return;
      this.zzup.setException(this.zzur.convert(paramStatus));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */