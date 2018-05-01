package com.google.android.gms.common.api;

import com.google.android.gms.internal.zzbbe;
import java.util.ArrayList;
import java.util.List;

public final class Batch
  extends zzbbe<BatchResult>
{
  private final Object mLock = new Object();
  private int zzaAC;
  private boolean zzaAD;
  private boolean zzaAE;
  private final PendingResult<?>[] zzaAF;
  
  private Batch(List<PendingResult<?>> paramList, GoogleApiClient paramGoogleApiClient)
  {
    super(paramGoogleApiClient);
    this.zzaAC = paramList.size();
    this.zzaAF = new PendingResult[this.zzaAC];
    if (paramList.isEmpty()) {
      setResult(new BatchResult(Status.zzaBm, this.zzaAF));
    }
    for (;;)
    {
      return;
      int i = 0;
      while (i < paramList.size())
      {
        paramGoogleApiClient = (PendingResult)paramList.get(i);
        this.zzaAF[i] = paramGoogleApiClient;
        paramGoogleApiClient.zza(new zzb(this));
        i += 1;
      }
    }
  }
  
  public final void cancel()
  {
    super.cancel();
    PendingResult[] arrayOfPendingResult = this.zzaAF;
    int j = arrayOfPendingResult.length;
    int i = 0;
    while (i < j)
    {
      arrayOfPendingResult[i].cancel();
      i += 1;
    }
  }
  
  public final BatchResult createFailedResult(Status paramStatus)
  {
    return new BatchResult(paramStatus, this.zzaAF);
  }
  
  public static final class Builder
  {
    private List<PendingResult<?>> zzaAH = new ArrayList();
    private GoogleApiClient zzapu;
    
    public Builder(GoogleApiClient paramGoogleApiClient)
    {
      this.zzapu = paramGoogleApiClient;
    }
    
    public final <R extends Result> BatchResultToken<R> add(PendingResult<R> paramPendingResult)
    {
      BatchResultToken localBatchResultToken = new BatchResultToken(this.zzaAH.size());
      this.zzaAH.add(paramPendingResult);
      return localBatchResultToken;
    }
    
    public final Batch build()
    {
      return new Batch(this.zzaAH, this.zzapu, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Batch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */