package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbbe;
import com.google.android.gms.internal.zzbec;
import com.google.android.gms.internal.zzben;

public final class PendingResults
{
  public static PendingResult<Status> canceledPendingResult()
  {
    zzben localzzben = new zzben(Looper.getMainLooper());
    localzzben.cancel();
    return localzzben;
  }
  
  public static <R extends Result> PendingResult<R> canceledPendingResult(R paramR)
  {
    zzbo.zzb(paramR, "Result must not be null");
    if (paramR.getStatus().getStatusCode() == 16) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "Status code must be CommonStatusCodes.CANCELED");
      paramR = new zza(paramR);
      paramR.cancel();
      return paramR;
    }
  }
  
  public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R paramR)
  {
    zzbo.zzb(paramR, "Result must not be null");
    zzc localzzc = new zzc(null);
    localzzc.setResult(paramR);
    return new zzbec(localzzc);
  }
  
  public static PendingResult<Status> immediatePendingResult(Status paramStatus)
  {
    zzbo.zzb(paramStatus, "Result must not be null");
    zzben localzzben = new zzben(Looper.getMainLooper());
    localzzben.setResult(paramStatus);
    return localzzben;
  }
  
  public static <R extends Result> PendingResult<R> zza(R paramR, GoogleApiClient paramGoogleApiClient)
  {
    zzbo.zzb(paramR, "Result must not be null");
    if (!paramR.getStatus().isSuccess()) {}
    for (boolean bool = true;; bool = false)
    {
      zzbo.zzb(bool, "Status code must not be SUCCESS");
      paramGoogleApiClient = new zzb(paramGoogleApiClient, paramR);
      paramGoogleApiClient.setResult(paramR);
      return paramGoogleApiClient;
    }
  }
  
  public static PendingResult<Status> zza(Status paramStatus, GoogleApiClient paramGoogleApiClient)
  {
    zzbo.zzb(paramStatus, "Result must not be null");
    paramGoogleApiClient = new zzben(paramGoogleApiClient);
    paramGoogleApiClient.setResult(paramStatus);
    return paramGoogleApiClient;
  }
  
  public static <R extends Result> OptionalPendingResult<R> zzb(R paramR, GoogleApiClient paramGoogleApiClient)
  {
    zzbo.zzb(paramR, "Result must not be null");
    paramGoogleApiClient = new zzc(paramGoogleApiClient);
    paramGoogleApiClient.setResult(paramR);
    return new zzbec(paramGoogleApiClient);
  }
  
  static final class zza<R extends Result>
    extends zzbbe<R>
  {
    private final R zzaBi;
    
    public zza(R paramR)
    {
      super();
      this.zzaBi = paramR;
    }
    
    protected final R zzb(Status paramStatus)
    {
      if (paramStatus.getStatusCode() != this.zzaBi.getStatus().getStatusCode()) {
        throw new UnsupportedOperationException("Creating failed results is not supported");
      }
      return this.zzaBi;
    }
  }
  
  static final class zzb<R extends Result>
    extends zzbbe<R>
  {
    private final R zzaBj;
    
    public zzb(GoogleApiClient paramGoogleApiClient, R paramR)
    {
      super();
      this.zzaBj = paramR;
    }
    
    protected final R zzb(Status paramStatus)
    {
      return this.zzaBj;
    }
  }
  
  static final class zzc<R extends Result>
    extends zzbbe<R>
  {
    public zzc(GoogleApiClient paramGoogleApiClient)
    {
      super();
    }
    
    protected final R zzb(Status paramStatus)
    {
      throw new UnsupportedOperationException("Creating failed results is not supported");
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/PendingResults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */