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
import com.google.android.gms.common.util.zzt;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Map;

public abstract class zzzx
{
  public final int zzakD;
  
  public zzzx(int paramInt)
  {
    this.zzakD = paramInt;
  }
  
  private static Status zza(RemoteException paramRemoteException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((zzt.zzzh()) && ((paramRemoteException instanceof TransactionTooLargeException))) {
      localStringBuilder.append("TransactionTooLargeException: ");
    }
    localStringBuilder.append(paramRemoteException.getLocalizedMessage());
    return new Status(8, localStringBuilder.toString());
  }
  
  public abstract void zza(@NonNull zzaal paramzzaal, boolean paramBoolean);
  
  public abstract void zza(zzaax.zza<?> paramzza)
    throws DeadObjectException;
  
  public abstract void zzz(@NonNull Status paramStatus);
  
  private static abstract class zza
    extends zzzx
  {
    protected final TaskCompletionSource<Void> zzazE;
    
    public zza(int paramInt, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super();
      this.zzazE = paramTaskCompletionSource;
    }
    
    public void zza(@NonNull zzaal paramzzaal, boolean paramBoolean) {}
    
    public final void zza(zzaax.zza<?> paramzza)
      throws DeadObjectException
    {
      try
      {
        zzb(paramzza);
        return;
      }
      catch (DeadObjectException paramzza)
      {
        zzz(zzzx.zzb(paramzza));
        throw paramzza;
      }
      catch (RemoteException paramzza)
      {
        zzz(zzzx.zzb(paramzza));
      }
    }
    
    protected abstract void zzb(zzaax.zza<?> paramzza)
      throws RemoteException;
    
    public void zzz(@NonNull Status paramStatus)
    {
      this.zzazE.trySetException(new zza(paramStatus));
    }
  }
  
  public static class zzb<A extends zzaad.zza<? extends Result, Api.zzb>>
    extends zzzx
  {
    protected final A zzazF;
    
    public zzb(int paramInt, A paramA)
    {
      super();
      this.zzazF = paramA;
    }
    
    public void zza(@NonNull zzaal paramzzaal, boolean paramBoolean)
    {
      paramzzaal.zza(this.zzazF, paramBoolean);
    }
    
    public void zza(zzaax.zza<?> paramzza)
      throws DeadObjectException
    {
      this.zzazF.zzb(paramzza.zzvU());
    }
    
    public void zzz(@NonNull Status paramStatus)
    {
      this.zzazF.zzB(paramStatus);
    }
  }
  
  public static final class zzc
    extends zzzx.zza
  {
    public final zzabm<Api.zzb, ?> zzazG;
    public final zzabz<Api.zzb, ?> zzazH;
    
    public zzc(zzabn paramzzabn, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super(paramTaskCompletionSource);
      this.zzazG = paramzzabn.zzazG;
      this.zzazH = paramzzabn.zzazH;
    }
    
    public void zzb(zzaax.zza<?> paramzza)
      throws RemoteException
    {
      if (this.zzazG.zzwW() != null) {
        paramzza.zzwI().put(this.zzazG.zzwW(), new zzabn(this.zzazG, this.zzazH));
      }
    }
  }
  
  public static final class zzd<TResult>
    extends zzzx
  {
    private final TaskCompletionSource<TResult> zzazE;
    private final zzabv<Api.zzb, TResult> zzazI;
    private final zzabs zzazJ;
    
    public zzd(int paramInt, zzabv<Api.zzb, TResult> paramzzabv, TaskCompletionSource<TResult> paramTaskCompletionSource, zzabs paramzzabs)
    {
      super();
      this.zzazE = paramTaskCompletionSource;
      this.zzazI = paramzzabv;
      this.zzazJ = paramzzabs;
    }
    
    public void zza(@NonNull zzaal paramzzaal, boolean paramBoolean)
    {
      paramzzaal.zza(this.zzazE, paramBoolean);
    }
    
    public void zza(zzaax.zza<?> paramzza)
      throws DeadObjectException
    {
      try
      {
        this.zzazI.zza(paramzza.zzvU(), this.zzazE);
        return;
      }
      catch (DeadObjectException paramzza)
      {
        throw paramzza;
      }
      catch (RemoteException paramzza)
      {
        zzz(zzzx.zzb(paramzza));
      }
    }
    
    public void zzz(@NonNull Status paramStatus)
    {
      this.zzazE.trySetException(this.zzazJ.zzA(paramStatus));
    }
  }
  
  public static final class zze
    extends zzzx.zza
  {
    public final zzabh.zzb<?> zzazK;
    
    public zze(zzabh.zzb<?> paramzzb, TaskCompletionSource<Void> paramTaskCompletionSource)
    {
      super(paramTaskCompletionSource);
      this.zzazK = paramzzb;
    }
    
    public void zzb(zzaax.zza<?> paramzza)
      throws RemoteException
    {
      paramzza = (zzabn)paramzza.zzwI().remove(this.zzazK);
      if (paramzza != null)
      {
        paramzza.zzazG.zzwX();
        return;
      }
      Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
      this.zzazE.trySetException(new zza(Status.zzazz));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzzx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */