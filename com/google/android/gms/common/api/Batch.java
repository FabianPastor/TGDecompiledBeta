package com.google.android.gms.common.api;

import com.google.android.gms.internal.zzqe;
import java.util.ArrayList;
import java.util.List;

public final class Batch
  extends zzqe<BatchResult>
{
  private int vo;
  private boolean vp;
  private boolean vq;
  private final PendingResult<?>[] vr;
  private final Object zzakd = new Object();
  
  private Batch(List<PendingResult<?>> paramList, GoogleApiClient paramGoogleApiClient)
  {
    super(paramGoogleApiClient);
    this.vo = paramList.size();
    this.vr = new PendingResult[this.vo];
    if (paramList.isEmpty()) {
      zzc(new BatchResult(Status.vY, this.vr));
    }
    for (;;)
    {
      return;
      int i = 0;
      while (i < paramList.size())
      {
        paramGoogleApiClient = (PendingResult)paramList.get(i);
        this.vr[i] = paramGoogleApiClient;
        paramGoogleApiClient.zza(new PendingResult.zza()
        {
          public void zzv(Status paramAnonymousStatus)
          {
            for (;;)
            {
              synchronized (Batch.zza(Batch.this))
              {
                if (Batch.this.isCanceled()) {
                  return;
                }
                if (paramAnonymousStatus.isCanceled())
                {
                  Batch.zza(Batch.this, true);
                  Batch.zzb(Batch.this);
                  if (Batch.zzc(Batch.this) == 0)
                  {
                    if (!Batch.zzd(Batch.this)) {
                      break;
                    }
                    Batch.zze(Batch.this);
                  }
                  return;
                }
              }
              if (!paramAnonymousStatus.isSuccess()) {
                Batch.zzb(Batch.this, true);
              }
            }
            if (Batch.zzf(Batch.this)) {}
            for (paramAnonymousStatus = new Status(13);; paramAnonymousStatus = Status.vY)
            {
              Batch.this.zzc(new BatchResult(paramAnonymousStatus, Batch.zzg(Batch.this)));
              break;
            }
          }
        });
        i += 1;
      }
    }
  }
  
  public void cancel()
  {
    super.cancel();
    PendingResult[] arrayOfPendingResult = this.vr;
    int j = arrayOfPendingResult.length;
    int i = 0;
    while (i < j)
    {
      arrayOfPendingResult[i].cancel();
      i += 1;
    }
  }
  
  public BatchResult createFailedResult(Status paramStatus)
  {
    return new BatchResult(paramStatus, this.vr);
  }
  
  public static final class Builder
  {
    private GoogleApiClient kv;
    private List<PendingResult<?>> vt = new ArrayList();
    
    public Builder(GoogleApiClient paramGoogleApiClient)
    {
      this.kv = paramGoogleApiClient;
    }
    
    public <R extends Result> BatchResultToken<R> add(PendingResult<R> paramPendingResult)
    {
      BatchResultToken localBatchResultToken = new BatchResultToken(this.vt.size());
      this.vt.add(paramPendingResult);
      return localBatchResultToken;
    }
    
    public Batch build()
    {
      return new Batch(this.vt, this.kv, null);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/Batch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */