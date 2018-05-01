package com.google.android.gms.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
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

public class zzaby
{
  public static final Status zzaDu = new Status(8, "The connection to Google Play services was lost");
  private static final zzaaf<?>[] zzaDv = new zzaaf[0];
  private final Map<Api.zzc<?>, Api.zze> zzaBQ;
  final Set<zzaaf<?>> zzaDw = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
  private final zzb zzaDx = new zzb()
  {
    public void zzc(zzaaf<?> paramAnonymouszzaaf)
    {
      zzaby.this.zzaDw.remove(paramAnonymouszzaaf);
      if (paramAnonymouszzaaf.zzvr() != null) {
        zzaby.zza(zzaby.this);
      }
    }
  };
  
  public zzaby(Map<Api.zzc<?>, Api.zze> paramMap)
  {
    this.zzaBQ = paramMap;
  }
  
  private static void zza(zzaaf<?> paramzzaaf, zzf paramzzf, IBinder paramIBinder)
  {
    if (paramzzaaf.isReady())
    {
      paramzzaaf.zza(new zza(paramzzaaf, paramzzf, paramIBinder, null));
      return;
    }
    if ((paramIBinder != null) && (paramIBinder.isBinderAlive()))
    {
      zza localzza = new zza(paramzzaaf, paramzzf, paramIBinder, null);
      paramzzaaf.zza(localzza);
      try
      {
        paramIBinder.linkToDeath(localzza, 0);
        return;
      }
      catch (RemoteException paramIBinder)
      {
        paramzzaaf.cancel();
        paramzzf.remove(paramzzaaf.zzvr().intValue());
        return;
      }
    }
    paramzzaaf.zza(null);
    paramzzaaf.cancel();
    paramzzf.remove(paramzzaaf.zzvr().intValue());
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.append(" mUnconsumedApiCalls.size()=").println(this.zzaDw.size());
  }
  
  public void release()
  {
    zzaaf[] arrayOfzzaaf = (zzaaf[])this.zzaDw.toArray(zzaDv);
    int j = arrayOfzzaaf.length;
    int i = 0;
    if (i < j)
    {
      zzaaf localzzaaf = arrayOfzzaaf[i];
      localzzaaf.zza(null);
      if (localzzaaf.zzvr() == null) {
        if (localzzaaf.zzvF()) {
          this.zzaDw.remove(localzzaaf);
        }
      }
      for (;;)
      {
        i += 1;
        break;
        localzzaaf.zzvH();
        zza(localzzaaf, null, ((Api.zze)this.zzaBQ.get(((zzaad.zza)localzzaaf).zzvg())).zzvi());
        this.zzaDw.remove(localzzaaf);
      }
    }
  }
  
  void zzb(zzaaf<? extends Result> paramzzaaf)
  {
    this.zzaDw.add(paramzzaaf);
    paramzzaaf.zza(this.zzaDx);
  }
  
  public void zzxd()
  {
    zzaaf[] arrayOfzzaaf = (zzaaf[])this.zzaDw.toArray(zzaDv);
    int j = arrayOfzzaaf.length;
    int i = 0;
    while (i < j)
    {
      arrayOfzzaaf[i].zzC(zzaDu);
      i += 1;
    }
  }
  
  private static class zza
    implements IBinder.DeathRecipient, zzaby.zzb
  {
    private final WeakReference<zzf> zzaDA;
    private final WeakReference<IBinder> zzaDB;
    private final WeakReference<zzaaf<?>> zzaDz;
    
    private zza(zzaaf<?> paramzzaaf, zzf paramzzf, IBinder paramIBinder)
    {
      this.zzaDA = new WeakReference(paramzzf);
      this.zzaDz = new WeakReference(paramzzaaf);
      this.zzaDB = new WeakReference(paramIBinder);
    }
    
    private void zzxe()
    {
      Object localObject = (zzaaf)this.zzaDz.get();
      zzf localzzf = (zzf)this.zzaDA.get();
      if ((localzzf != null) && (localObject != null)) {
        localzzf.remove(((zzaaf)localObject).zzvr().intValue());
      }
      localObject = (IBinder)this.zzaDB.get();
      if (localObject != null) {
        ((IBinder)localObject).unlinkToDeath(this, 0);
      }
    }
    
    public void binderDied()
    {
      zzxe();
    }
    
    public void zzc(zzaaf<?> paramzzaaf)
    {
      zzxe();
    }
  }
  
  static abstract interface zzb
  {
    public abstract void zzc(zzaaf<?> paramzzaaf);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaby.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */