package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzqe;
import com.google.android.gms.internal.zzrg;
import com.google.android.gms.internal.zzrm;

public final class PendingResults
{
  public static PendingResult<Status> canceledPendingResult()
  {
    zzrm localzzrm = new zzrm(Looper.getMainLooper());
    localzzrm.cancel();
    return localzzrm;
  }
  
  public static <R extends Result> PendingResult<R> canceledPendingResult(R paramR)
  {
    zzac.zzb(paramR, "Result must not be null");
    if (paramR.getStatus().getStatusCode() == 16) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "Status code must be CommonStatusCodes.CANCELED");
      paramR = new zza(paramR);
      paramR.cancel();
      return paramR;
    }
  }
  
  public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R paramR)
  {
    zzac.zzb(paramR, "Result must not be null");
    zzc localzzc = new zzc(null);
    localzzc.zzc(paramR);
    return new zzrg(localzzc);
  }
  
  public static PendingResult<Status> immediatePendingResult(Status paramStatus)
  {
    zzac.zzb(paramStatus, "Result must not be null");
    zzrm localzzrm = new zzrm(Looper.getMainLooper());
    localzzrm.zzc(paramStatus);
    return localzzrm;
  }
  
  public static <R extends Result> PendingResult<R> zza(R paramR, GoogleApiClient paramGoogleApiClient)
  {
    zzac.zzb(paramR, "Result must not be null");
    if (!paramR.getStatus().isSuccess()) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzb(bool, "Status code must not be SUCCESS");
      paramGoogleApiClient = new zzb(paramGoogleApiClient, paramR);
      paramGoogleApiClient.zzc(paramR);
      return paramGoogleApiClient;
    }
  }
  
  public static PendingResult<Status> zza(Status paramStatus, GoogleApiClient paramGoogleApiClient)
  {
    zzac.zzb(paramStatus, "Result must not be null");
    paramGoogleApiClient = new zzrm(paramGoogleApiClient);
    paramGoogleApiClient.zzc(paramStatus);
    return paramGoogleApiClient;
  }
  
  public static <R extends Result> OptionalPendingResult<R> zzb(R paramR, GoogleApiClient paramGoogleApiClient)
  {
    zzac.zzb(paramR, "Result must not be null");
    paramGoogleApiClient = new zzc(paramGoogleApiClient);
    paramGoogleApiClient.zzc(paramR);
    return new zzrg(paramGoogleApiClient);
  }
  
  private static final class zza<R extends Result>
    extends zzqe<R>
  {
    private final R vT;
    
    public zza(R paramR)
    {
      super();
      this.vT = paramR;
    }
    
    protected R zzc(Status paramStatus)
    {
      if (paramStatus.getStatusCode() != this.vT.getStatus().getStatusCode()) {
        throw new UnsupportedOperationException("Creating failed results is not supported");
      }
      return this.vT;
    }
  }
  
  private static final class zzb<R extends Result>
    extends zzqe<R>
  {
    private final R vU;
    
    public zzb(GoogleApiClient paramGoogleApiClient, R paramR)
    {
      super();
      this.vU = paramR;
    }
    
    protected R zzc(Status paramStatus)
    {
      return this.vU;
    }
  }
  
  private static final class zzc<R extends Result>
    extends zzqe<R>
  {
    public zzc(GoogleApiClient paramGoogleApiClient)
    {
      super();
    }
    
    protected R zzc(Status paramStatus)
    {
      throw new UnsupportedOperationException("Creating failed results is not supported");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/PendingResults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */