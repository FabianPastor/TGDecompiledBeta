package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public abstract class zzqj
{
  public final int nV;
  
  public zzqj(int paramInt)
  {
    this.nV = paramInt;
  }
  
  public abstract void zza(@NonNull zzqv paramzzqv, boolean paramBoolean);
  
  public abstract void zza(zzrh.zza<?> paramzza)
    throws DeadObjectException;
  
  public abstract void zzy(@NonNull Status paramStatus);
  
  private static abstract class zza
    extends zzqj
  {
    protected final TaskCompletionSource<Void> yg;
    
    public zza(int paramInt, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super();
      this.yg = paramTaskCompletionSource;
    }
    
    private void zza(RemoteException paramRemoteException)
    {
      zzy(new Status(8, paramRemoteException.getLocalizedMessage(), null));
    }
    
    public void zza(@NonNull zzqv paramzzqv, boolean paramBoolean) {}
    
    public final void zza(zzrh.zza<?> paramzza)
      throws DeadObjectException
    {
      try
      {
        zzb(paramzza);
        return;
      }
      catch (DeadObjectException paramzza)
      {
        zza(paramzza);
        throw paramzza;
      }
      catch (RemoteException paramzza)
      {
        zza(paramzza);
      }
    }
    
    protected abstract void zzb(zzrh.zza<?> paramzza)
      throws RemoteException;
    
    public void zzy(@NonNull Status paramStatus)
    {
      this.yg.trySetException(new zza(paramStatus));
    }
  }
  
  public static class zzb<A extends zzqo.zza<? extends Result, Api.zzb>>
    extends zzqj
  {
    protected final A yh;
    
    public zzb(int paramInt, A paramA)
    {
      super();
      this.yh = paramA;
    }
    
    public void zza(@NonNull zzqv paramzzqv, boolean paramBoolean)
    {
      paramzzqv.zza(this.yh, paramBoolean);
    }
    
    public void zza(zzrh.zza<?> paramzza)
      throws DeadObjectException
    {
      this.yh.zzb(paramzza.getClient());
    }
    
    public void zzy(@NonNull Status paramStatus)
    {
      this.yh.zzaa(paramStatus);
    }
  }
  
  public static final class zzc
    extends zzqj.zza
  {
    public final zzrw<Api.zzb> yi;
    public final zzsh<Api.zzb> yj;
    
    public zzc(zzrx paramzzrx, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super(paramTaskCompletionSource);
      this.yi = paramzzrx.yi;
      this.yj = paramzzrx.yj;
    }
    
    public void zzb(zzrh.zza<?> paramzza)
      throws DeadObjectException
    {
      this.yi.zza(paramzza.getClient(), this.yg);
      if (this.yi.zzatz() != null) {
        paramzza.zzatn().put(this.yi.zzatz(), new zzrx(this.yi, this.yj));
      }
    }
  }
  
  public static final class zzd<TResult>
    extends zzqj
  {
    private static final Status ym = new Status(8, "Connection to Google Play services was lost while executing the API call.");
    private final TaskCompletionSource<TResult> yg;
    private final zzse<Api.zzb, TResult> yk;
    private final zzsb yl;
    
    public zzd(int paramInt, zzse<Api.zzb, TResult> paramzzse, TaskCompletionSource<TResult> paramTaskCompletionSource, zzsb paramzzsb)
    {
      super();
      this.yg = paramTaskCompletionSource;
      this.yk = paramzzse;
      this.yl = paramzzsb;
    }
    
    public void zza(@NonNull zzqv paramzzqv, boolean paramBoolean)
    {
      paramzzqv.zza(this.yg, paramBoolean);
    }
    
    public void zza(zzrh.zza<?> paramzza)
      throws DeadObjectException
    {
      try
      {
        this.yk.zzb(paramzza.getClient(), this.yg);
        return;
      }
      catch (DeadObjectException paramzza)
      {
        throw paramzza;
      }
      catch (RemoteException paramzza)
      {
        zzy(ym);
      }
    }
    
    public void zzy(@NonNull Status paramStatus)
    {
      this.yg.trySetException(this.yl.zzz(paramStatus));
    }
  }
  
  public static final class zze
    extends zzqj.zza
  {
    public final zzrr.zzb<?> yn;
    
    public zze(zzrr.zzb<?> paramzzb, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super(paramTaskCompletionSource);
      this.yn = paramzzb;
    }
    
    public void zzb(zzrh.zza<?> paramzza)
      throws DeadObjectException
    {
      zzrx localzzrx = (zzrx)paramzza.zzatn().remove(this.yn);
      if (localzzrx != null)
      {
        localzzrx.yj.zzc(paramzza.getClient(), this.yg);
        localzzrx.yi.zzaua();
        return;
      }
      Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
      this.yg.trySetException(new zza(Status.yb));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzqj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */