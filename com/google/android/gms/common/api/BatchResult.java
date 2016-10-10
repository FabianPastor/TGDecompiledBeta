package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzac;
import java.util.concurrent.TimeUnit;

public final class BatchResult
  implements Result
{
  private final Status fp;
  private final PendingResult<?>[] vr;
  
  BatchResult(Status paramStatus, PendingResult<?>[] paramArrayOfPendingResult)
  {
    this.fp = paramStatus;
    this.vr = paramArrayOfPendingResult;
  }
  
  public Status getStatus()
  {
    return this.fp;
  }
  
  public <R extends Result> R take(BatchResultToken<R> paramBatchResultToken)
  {
    if (paramBatchResultToken.mId < this.vr.length) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "The result token does not belong to this batch");
      return this.vr[paramBatchResultToken.mId].await(0L, TimeUnit.MILLISECONDS);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/BatchResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */