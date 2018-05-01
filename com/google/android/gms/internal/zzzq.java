package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.common.util.zzs;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public abstract class zzzq
{
  public final int zzanR;
  
  public zzzq(int paramInt)
  {
    this.zzanR = paramInt;
  }
  
  private static Status zza(RemoteException paramRemoteException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((zzs.zzyB()) && ((paramRemoteException instanceof TransactionTooLargeException))) {
      localStringBuilder.append("TransactionTooLargeException: ");
    }
    localStringBuilder.append(paramRemoteException.getLocalizedMessage());
    return new Status(8, localStringBuilder.toString());
  }
  
  public abstract void zza(@NonNull zzaad paramzzaad, boolean paramBoolean);
  
  public abstract void zza(zzaap.zza<?> paramzza)
    throws DeadObjectException;
  
  public abstract void zzy(@NonNull Status paramStatus);
  
  private static abstract class zza
    extends zzzq
  {
    protected final TaskCompletionSource<Void> zzayo;
    
    public zza(int paramInt, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super();
      this.zzayo = paramTaskCompletionSource;
    }
    
    public void zza(@NonNull zzaad paramzzaad, boolean paramBoolean) {}
    
    public final void zza(zzaap.zza<?> paramzza)
      throws DeadObjectException
    {
      try
      {
        zzb(paramzza);
        return;
      }
      catch (DeadObjectException paramzza)
      {
        zzy(zzzq.zzb(paramzza));
        throw paramzza;
      }
      catch (RemoteException paramzza)
      {
        zzy(zzzq.zzb(paramzza));
      }
    }
    
    protected abstract void zzb(zzaap.zza<?> paramzza)
      throws RemoteException;
    
    public void zzy(@NonNull Status paramStatus)
    {
      this.zzayo.trySetException(new zza(paramStatus));
    }
  }
  
  public static class zzb<A extends zzzv.zza<? extends Result, Api.zzb>>
    extends zzzq
  {
    protected final A zzayp;
    
    public zzb(int paramInt, A paramA)
    {
      super();
      this.zzayp = paramA;
    }
    
    public void zza(@NonNull zzaad paramzzaad, boolean paramBoolean)
    {
      paramzzaad.zza(this.zzayp, paramBoolean);
    }
    
    public void zza(zzaap.zza<?> paramzza)
      throws DeadObjectException
    {
      this.zzayp.zzb(paramzza.zzvr());
    }
    
    public void zzy(@NonNull Status paramStatus)
    {
      this.zzayp.zzA(paramStatus);
    }
  }
  
  public static final class zzc
    extends zzzq.zza
  {
    public final zzabe<Api.zzb, ?> zzayq;
    public final zzabr<Api.zzb, ?> zzayr;
    
    public zzc(zzabf paramzzabf, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super(paramTaskCompletionSource);
      this.zzayq = paramzzabf.zzayq;
      this.zzayr = paramzzabf.zzayr;
    }
    
    public void zzb(zzaap.zza<?> paramzza)
      throws RemoteException
    {
      if (this.zzayq.zzwp() != null) {
        paramzza.zzwc().put(this.zzayq.zzwp(), new zzabf(this.zzayq, this.zzayr));
      }
    }
  }
  
  public static final class zzd<TResult>
    extends zzzq
  {
    private final TaskCompletionSource<TResult> zzayo;
    private final zzabn<Api.zzb, TResult> zzays;
    private final zzabk zzayt;
    
    public zzd(int paramInt, zzabn<Api.zzb, TResult> paramzzabn, TaskCompletionSource<TResult> paramTaskCompletionSource, zzabk paramzzabk)
    {
      super();
      this.zzayo = paramTaskCompletionSource;
      this.zzays = paramzzabn;
      this.zzayt = paramzzabk;
    }
    
    public void zza(@NonNull zzaad paramzzaad, boolean paramBoolean)
    {
      paramzzaad.zza(this.zzayo, paramBoolean);
    }
    
    public void zza(zzaap.zza<?> paramzza)
      throws DeadObjectException
    {
      try
      {
        this.zzays.zza(paramzza.zzvr(), this.zzayo);
        return;
      }
      catch (DeadObjectException paramzza)
      {
        throw paramzza;
      }
      catch (RemoteException paramzza)
      {
        zzy(zzzq.zzb(paramzza));
      }
    }
    
    public void zzy(@NonNull Status paramStatus)
    {
      this.zzayo.trySetException(this.zzayt.zzz(paramStatus));
    }
  }
  
  public static final class zze
    extends zzzq.zza
  {
    public final zzaaz.zzb<?> zzayu;
    
    public zze(zzaaz.zzb<?> paramzzb, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super(paramTaskCompletionSource);
      this.zzayu = paramzzb;
    }
    
    public void zzb(zzaap.zza<?> paramzza)
      throws RemoteException
    {
      paramzza = (zzabf)paramzza.zzwc().remove(this.zzayu);
      if (paramzza != null)
      {
        paramzza.zzayq.zzwq();
        return;
      }
      Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
      this.zzayo.trySetException(new zza(Status.zzayj));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */