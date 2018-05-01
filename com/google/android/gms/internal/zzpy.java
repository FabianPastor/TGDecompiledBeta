package com.google.android.gms.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zza;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseException;
import java.util.Map;

public abstract class zzpy
{
  public final int lN;
  public final int wf;
  
  public zzpy(int paramInt1, int paramInt2)
  {
    this.wf = paramInt1;
    this.lN = paramInt2;
  }
  
  public boolean cancel()
  {
    return true;
  }
  
  public void zza(SparseArray<zzrq> paramSparseArray) {}
  
  public abstract void zzb(Api.zzb paramzzb)
    throws DeadObjectException;
  
  public abstract void zzx(@NonNull Status paramStatus);
  
  private static abstract class zza
    extends zzpy
  {
    protected final SparseArray<Map<zzrd.zzb<?>, zzri>> wg;
    protected final TaskCompletionSource<Void> wh;
    
    public zza(int paramInt1, int paramInt2, TaskCompletionSource<Void> paramTaskCompletionSource, SparseArray<Map<zzrd.zzb<?>, zzri>> paramSparseArray)
    {
      super(paramInt2);
      this.wg = paramSparseArray;
      this.wh = paramTaskCompletionSource;
    }
    
    private void zza(RemoteException paramRemoteException)
    {
      zzx(new Status(8, paramRemoteException.getLocalizedMessage(), null));
    }
    
    public boolean cancel()
    {
      this.wh.setException(new zza(Status.wc));
      return true;
    }
    
    public void zza(SparseArray<zzrq> paramSparseArray) {}
    
    protected abstract void zza(Api.zzb paramzzb)
      throws RemoteException;
    
    public final void zzb(Api.zzb paramzzb)
      throws DeadObjectException
    {
      try
      {
        zza(paramzzb);
        return;
      }
      catch (DeadObjectException paramzzb)
      {
        zza(paramzzb);
        throw paramzzb;
      }
      catch (RemoteException paramzzb)
      {
        zza(paramzzb);
      }
    }
    
    public void zzx(@NonNull Status paramStatus)
    {
      this.wh.setException(new zza(paramStatus));
    }
  }
  
  public static class zzb<A extends zzqc.zza<? extends Result, Api.zzb>>
    extends zzpy
  {
    protected final A wi;
    
    public zzb(int paramInt1, int paramInt2, A paramA)
    {
      super(paramInt2);
      this.wi = paramA;
    }
    
    public boolean cancel()
    {
      return this.wi.zzaqq();
    }
    
    public void zza(SparseArray<zzrq> paramSparseArray)
    {
      paramSparseArray = (zzrq)paramSparseArray.get(this.wf);
      if (paramSparseArray != null) {
        paramSparseArray.zzb(this.wi);
      }
    }
    
    public void zzb(Api.zzb paramzzb)
      throws DeadObjectException
    {
      this.wi.zzb(paramzzb);
    }
    
    public void zzx(@NonNull Status paramStatus)
    {
      this.wi.zzz(paramStatus);
    }
  }
  
  public static final class zzc
    extends zzpy.zza
  {
    public final zzrh<Api.zzb> wj;
    public final zzrr<Api.zzb> wk;
    
    public zzc(int paramInt, zzri paramzzri, TaskCompletionSource<Void> paramTaskCompletionSource, SparseArray<Map<zzrd.zzb<?>, zzri>> paramSparseArray)
    {
      super(3, paramTaskCompletionSource, paramSparseArray);
      this.wj = paramzzri.wj;
      this.wk = paramzzri.wk;
    }
    
    public void zza(Api.zzb paramzzb)
      throws DeadObjectException
    {
      this.wj.zza(paramzzb, this.wh);
      Object localObject = (Map)this.wg.get(this.wf);
      paramzzb = (Api.zzb)localObject;
      if (localObject == null)
      {
        paramzzb = new ArrayMap(1);
        this.wg.put(this.wf, paramzzb);
      }
      localObject = String.valueOf(this.wj.zzasr());
      Log.d("reg", String.valueOf(localObject).length() + 12 + "registered: " + (String)localObject);
      if (this.wj.zzasr() != null) {
        paramzzb.put(this.wj.zzasr(), new zzri(this.wj, this.wk));
      }
    }
  }
  
  public static final class zzd<TResult>
    extends zzpy
  {
    private static final Status wm = new Status(8, "Connection to Google Play services was lost while executing the API call.");
    private final TaskCompletionSource<TResult> wh;
    private final zzro<Api.zzb, TResult> wl;
    
    public zzd(int paramInt1, int paramInt2, zzro<Api.zzb, TResult> paramzzro, TaskCompletionSource<TResult> paramTaskCompletionSource)
    {
      super(paramInt2);
      this.wh = paramTaskCompletionSource;
      this.wl = paramzzro;
    }
    
    public void zzb(Api.zzb paramzzb)
      throws DeadObjectException
    {
      try
      {
        this.wl.zzb(paramzzb, this.wh);
        return;
      }
      catch (DeadObjectException paramzzb)
      {
        zzx(wm);
        throw paramzzb;
      }
      catch (RemoteException paramzzb)
      {
        zzx(wm);
      }
    }
    
    public void zzx(@NonNull Status paramStatus)
    {
      if (paramStatus.getStatusCode() == 8)
      {
        this.wh.setException(new FirebaseException(paramStatus.getStatusMessage()));
        return;
      }
      this.wh.setException(new FirebaseApiNotAvailableException(paramStatus.getStatusMessage()));
    }
  }
  
  public static final class zze
    extends zzpy.zza
  {
    public final zzrr<Api.zzb> wn;
    
    public zze(int paramInt, zzrr<Api.zzb> paramzzrr, TaskCompletionSource<Void> paramTaskCompletionSource, SparseArray<Map<zzrd.zzb<?>, zzri>> paramSparseArray)
    {
      super(4, paramTaskCompletionSource, paramSparseArray);
      this.wn = paramzzrr;
    }
    
    public void zza(Api.zzb paramzzb)
      throws DeadObjectException
    {
      Map localMap = (Map)this.wg.get(this.wf);
      if ((localMap != null) && (this.wn.zzasr() != null))
      {
        localMap.remove(this.wn.zzasr());
        this.wn.zzc(paramzzb, this.wh);
        return;
      }
      Log.wtf("UnregisterListenerTask", "Received call to unregister a listener without a matching registration call.", new Exception());
      this.wh.setException(new zza(Status.wa));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzpy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */