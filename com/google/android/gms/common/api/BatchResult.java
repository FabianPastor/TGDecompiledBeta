package com.google.android.gms.common.api;

import com.google.android.gms.common.internal.zzbo;
import java.util.concurrent.TimeUnit;

public final class BatchResult
  implements Result
{
  private final Status mStatus;
  private final PendingResult<?>[] zzaAF;
  
  BatchResult(Status paramStatus, PendingResult<?>[] paramArrayOfPendingResult)
  {
    this.mStatus = paramStatus;
    this.zzaAF = paramArrayOfPendingResult;
  }
  
  public final Status getStatus()
  {
    return this.mStatus;
  }
  
  public final <R extends Result> R take(BatchResultToken<R> paramBatchResultToken)
  {
    if (paramBatchResultToken.mId < this.zzaAF.length) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "The result token does not belong to this batch");
      return this.zzaAF[paramBatchResultToken.mId].await(0L, TimeUnit.MILLISECONDS);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/BatchResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */