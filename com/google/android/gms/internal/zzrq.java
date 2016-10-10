package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzf;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class zzrq
{
  private static final zzqe<?>[] zt = new zzqe[0];
  private final Api.zze vC;
  private final Map<Api.zzc<?>, Api.zze> xW;
  final Set<zzqe<?>> zu = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zzb zv = new zzb()
  {
    public void zzc(zzqe<?> paramAnonymouszzqe)
    {
      zzrq.this.zu.remove(paramAnonymouszzqe);
      if ((paramAnonymouszzqe.zzaqf() != null) && (zzrq.zza(zzrq.this) != null)) {
        zzrq.zza(zzrq.this).remove(paramAnonymouszzqe.zzaqf().intValue());
      }
      if ((zzrq.zzb(zzrq.this) != null) && (zzrq.this.zu.isEmpty())) {
        zzrq.zzb(zzrq.this).zzask();
      }
    }
  };
  private zzc zw = null;
  
  public zzrq(Api.zze paramzze)
  {
    this.xW = null;
    this.vC = paramzze;
  }
  
  public zzrq(Map<Api.zzc<?>, Api.zze> paramMap)
  {
    this.xW = paramMap;
    this.vC = null;
  }
  
  private static void zza(zzqe<?> paramzzqe, zzf paramzzf, IBinder paramIBinder)
  {
    if (paramzzqe.isReady())
    {
      paramzzqe.zza(new zza(paramzzqe, paramzzf, paramIBinder, null));
      return;
    }
    if ((paramIBinder != null) && (paramIBinder.isBinderAlive()))
    {
      zza localzza = new zza(paramzzqe, paramzzf, paramIBinder, null);
      paramzzqe.zza(localzza);
      try
      {
        paramIBinder.linkToDeath(localzza, 0);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        paramzzqe.cancel();
        paramzzf.remove(paramzzqe.zzaqf().intValue());
        return;
      }
    }
    paramzzqe.zza(null);
    paramzzqe.cancel();
    paramzzf.remove(paramzzqe.zzaqf().intValue());
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.append(" mUnconsumedApiCalls.size()=").println(this.zu.size());
  }
  
  public void release()
  {
    zzqe[] arrayOfzzqe = (zzqe[])this.zu.toArray(zt);
    int j = arrayOfzzqe.length;
    int i = 0;
    while (i < j)
    {
      zzqe localzzqe = arrayOfzzqe[i];
      localzzqe.zza(null);
      if (localzzqe.zzaqf() == null)
      {
        if (localzzqe.zzaqq()) {
          this.zu.remove(localzzqe);
        }
        i += 1;
      }
      else
      {
        localzzqe.zzaqs();
        IBinder localIBinder;
        if (this.vC != null) {
          localIBinder = this.vC.zzaps();
        }
        for (;;)
        {
          zza(localzzqe, null, localIBinder);
          this.zu.remove(localzzqe);
          break;
          if (this.xW != null)
          {
            localIBinder = ((Api.zze)this.xW.get(((zzqc.zza)localzzqe).zzapp())).zzaps();
          }
          else
          {
            Log.wtf("UnconsumedApiCalls", "Could not get service broker binder", new Exception());
            localIBinder = null;
          }
        }
      }
    }
  }
  
  public void zza(zzc paramzzc)
  {
    if (this.zu.isEmpty()) {
      paramzzc.zzask();
    }
    this.zw = paramzzc;
  }
  
  public void zzasw()
  {
    zzqe[] arrayOfzzqe = (zzqe[])this.zu.toArray(zt);
    int j = arrayOfzzqe.length;
    int i = 0;
    while (i < j)
    {
      arrayOfzzqe[i].zzaa(new Status(8, "The connection to Google Play services was lost"));
      i += 1;
    }
  }
  
  public boolean zzasx()
  {
    zzqe[] arrayOfzzqe = (zzqe[])this.zu.toArray(zt);
    int j = arrayOfzzqe.length;
    int i = 0;
    while (i < j)
    {
      if (!arrayOfzzqe[i].isReady()) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  void zzb(zzqe<? extends Result> paramzzqe)
  {
    this.zu.add(paramzzqe);
    paramzzqe.zza(this.zv);
  }
  
  private static class zza
    implements IBinder.DeathRecipient, zzrq.zzb
  {
    private final WeakReference<IBinder> zA;
    private final WeakReference<zzqe<?>> zy;
    private final WeakReference<zzf> zz;
    
    private zza(zzqe<?> paramzzqe, zzf paramzzf, IBinder paramIBinder)
    {
      this.zz = new WeakReference(paramzzf);
      this.zy = new WeakReference(paramzzqe);
      this.zA = new WeakReference(paramIBinder);
    }
    
    private void zzasd()
    {
      Object localObject = (zzqe)this.zy.get();
      zzf localzzf = (zzf)this.zz.get();
      if ((localzzf != null) && (localObject != null)) {
        localzzf.remove(((zzqe)localObject).zzaqf().intValue());
      }
      localObject = (IBinder)this.zA.get();
      if (localObject != null) {
        ((IBinder)localObject).unlinkToDeath(this, 0);
      }
    }
    
    public void binderDied()
    {
      zzasd();
    }
    
    public void zzc(zzqe<?> paramzzqe)
    {
      zzasd();
    }
  }
  
  static abstract interface zzb
  {
    public abstract void zzc(zzqe<?> paramzzqe);
  }
  
  static abstract interface zzc
  {
    public abstract void zzask();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */