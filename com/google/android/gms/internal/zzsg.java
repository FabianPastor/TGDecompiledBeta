package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api.zzc;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zze;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class zzsg
{
  private static final zzqq<?>[] BE = new zzqq[0];
  public static final Status ym = new Status(8, "The connection to Google Play services was lost");
  private final Map<Api.zzc<?>, Api.zze> Aj;
  final Set<zzqq<?>> BF = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zzb BG = new zzb()
  {
    public void zzc(zzqq<?> paramAnonymouszzqq)
    {
      zzsg.this.BF.remove(paramAnonymouszzqq);
      if ((paramAnonymouszzqq.zzarh() != null) && (zzsg.zza(zzsg.this) != null)) {
        zzsg.zza(zzsg.this).remove(paramAnonymouszzqq.zzarh().intValue());
      }
    }
  };
  
  public zzsg(Map<Api.zzc<?>, Api.zze> paramMap)
  {
    this.Aj = paramMap;
  }
  
  private static void zza(zzqq<?> paramzzqq, zze paramzze, IBinder paramIBinder)
  {
    if (paramzzqq.isReady())
    {
      paramzzqq.zza(new zza(paramzzqq, paramzze, paramIBinder, null));
      return;
    }
    if ((paramIBinder != null) && (paramIBinder.isBinderAlive()))
    {
      zza localzza = new zza(paramzzqq, paramzze, paramIBinder, null);
      paramzzqq.zza(localzza);
      try
      {
        paramIBinder.linkToDeath(localzza, 0);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        paramzzqq.cancel();
        paramzze.remove(paramzzqq.zzarh().intValue());
        return;
      }
    }
    paramzzqq.zza(null);
    paramzzqq.cancel();
    paramzze.remove(paramzzqq.zzarh().intValue());
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.append(" mUnconsumedApiCalls.size()=").println(this.BF.size());
  }
  
  public void release()
  {
    zzqq[] arrayOfzzqq = (zzqq[])this.BF.toArray(BE);
    int j = arrayOfzzqq.length;
    int i = 0;
    if (i < j)
    {
      zzqq localzzqq = arrayOfzzqq[i];
      localzzqq.zza(null);
      if (localzzqq.zzarh() == null) {
        if (localzzqq.zzars()) {
          this.BF.remove(localzzqq);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        localzzqq.zzaru();
        zza(localzzqq, null, ((Api.zze)this.Aj.get(((zzqo.zza)localzzqq).zzaqv())).zzaqy());
        this.BF.remove(localzzqq);
      }
    }
  }
  
  public void zzauf()
  {
    zzqq[] arrayOfzzqq = (zzqq[])this.BF.toArray(BE);
    int j = arrayOfzzqq.length;
    int i = 0;
    while (i < j)
    {
      arrayOfzzqq[i].zzab(ym);
      i += 1;
    }
  }
  
  void zzb(zzqq<? extends Result> paramzzqq)
  {
    this.BF.add(paramzzqq);
    paramzzqq.zza(this.BG);
  }
  
  private static class zza
    implements IBinder.DeathRecipient, zzsg.zzb
  {
    private final WeakReference<zzqq<?>> BI;
    private final WeakReference<zze> BJ;
    private final WeakReference<IBinder> BK;
    
    private zza(zzqq<?> paramzzqq, zze paramzze, IBinder paramIBinder)
    {
      this.BJ = new WeakReference(paramzze);
      this.BI = new WeakReference(paramzzqq);
      this.BK = new WeakReference(paramIBinder);
    }
    
    private void zzaug()
    {
      Object localObject = (zzqq)this.BI.get();
      zze localzze = (zze)this.BJ.get();
      if ((localzze != null) && (localObject != null)) {
        localzze.remove(((zzqq)localObject).zzarh().intValue());
      }
      localObject = (IBinder)this.BK.get();
      if (localObject != null) {
        ((IBinder)localObject).unlinkToDeath(this, 0);
      }
    }
    
    public void binderDied()
    {
      zzaug();
    }
    
    public void zzc(zzqq<?> paramzzqq)
    {
      zzaug();
    }
  }
  
  static abstract interface zzb
  {
    public abstract void zzc(zzqq<?> paramzzqq);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */