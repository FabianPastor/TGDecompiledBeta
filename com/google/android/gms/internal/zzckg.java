package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.stats.zza;
import com.google.android.gms.common.zzf;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class zzckg
  extends zzcjl
{
  private final zzcku zzjic;
  private zzche zzjid;
  private volatile Boolean zzjie;
  private final zzcgs zzjif;
  private final zzclk zzjig;
  private final List<Runnable> zzjih = new ArrayList();
  private final zzcgs zzjii;
  
  protected zzckg(zzcim paramzzcim)
  {
    super(paramzzcim);
    this.zzjig = new zzclk(paramzzcim.zzws());
    this.zzjic = new zzcku(this);
    this.zzjif = new zzckh(this, paramzzcim);
    this.zzjii = new zzckm(this, paramzzcim);
  }
  
  private final void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzve();
    if (this.zzjid != null)
    {
      this.zzjid = null;
      zzawy().zzazj().zzj("Disconnected from device MeasurementService", paramComponentName);
      zzve();
      zzyc();
    }
  }
  
  private final void zzbat()
  {
    zzve();
    zzawy().zzazj().zzj("Processing queued up service tasks", Integer.valueOf(this.zzjih.size()));
    Iterator localIterator = this.zzjih.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      try
      {
        localRunnable.run();
      }
      catch (Throwable localThrowable)
      {
        zzawy().zzazd().zzj("Task exception while flushing queue", localThrowable);
      }
    }
    this.zzjih.clear();
    this.zzjii.cancel();
  }
  
  private final zzcgi zzbr(boolean paramBoolean)
  {
    zzchh localzzchh = zzawn();
    if (paramBoolean) {}
    for (String str = zzawy().zzazk();; str = null) {
      return localzzchh.zzjg(str);
    }
  }
  
  private final void zzj(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzve();
    if (isConnected())
    {
      paramRunnable.run();
      return;
    }
    if (this.zzjih.size() >= 1000L)
    {
      zzawy().zzazd().log("Discarding data. Max runnable queue size reached");
      return;
    }
    this.zzjih.add(paramRunnable);
    this.zzjii.zzs(60000L);
    zzyc();
  }
  
  private final void zzxr()
  {
    zzve();
    this.zzjig.start();
    this.zzjif.zzs(((Long)zzchc.zzjbj.get()).longValue());
  }
  
  private final void zzxs()
  {
    zzve();
    if (!isConnected()) {
      return;
    }
    zzawy().zzazj().log("Inactivity, disconnecting from the service");
    disconnect();
  }
  
  public final void disconnect()
  {
    zzve();
    zzxf();
    try
    {
      zza.zzamc();
      getContext().unbindService(this.zzjic);
      this.zzjid = null;
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      for (;;) {}
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
  
  public final boolean isConnected()
  {
    zzve();
    zzxf();
    return this.zzjid != null;
  }
  
  protected final void resetAnalyticsData()
  {
    zzve();
    zzxf();
    zzcgi localzzcgi = zzbr(false);
    zzawr().resetAnalyticsData();
    zzj(new zzcki(this, localzzcgi));
  }
  
  protected final void zza(zzche paramzzche)
  {
    zzve();
    zzbq.checkNotNull(paramzzche);
    this.zzjid = paramzzche;
    zzxr();
    zzbat();
  }
  
  final void zza(zzche paramzzche, zzbfm paramzzbfm, zzcgi paramzzcgi)
  {
    zzve();
    zzxf();
    int j = 0;
    int i = 100;
    ArrayList localArrayList;
    Object localObject;
    if ((j < 1001) && (i == 100))
    {
      localArrayList = new ArrayList();
      localObject = zzawr().zzeb(100);
      if (localObject == null) {
        break label295;
      }
      localArrayList.addAll((Collection)localObject);
    }
    label295:
    for (i = ((List)localObject).size();; i = 0)
    {
      if ((paramzzbfm != null) && (i < 100)) {
        localArrayList.add(paramzzbfm);
      }
      localArrayList = (ArrayList)localArrayList;
      int m = localArrayList.size();
      int k = 0;
      while (k < m)
      {
        localObject = localArrayList.get(k);
        k += 1;
        localObject = (zzbfm)localObject;
        if ((localObject instanceof zzcha)) {
          try
          {
            paramzzche.zza((zzcha)localObject, paramzzcgi);
          }
          catch (RemoteException localRemoteException1)
          {
            zzawy().zzazd().zzj("Failed to send event to the service", localRemoteException1);
          }
        } else if ((localRemoteException1 instanceof zzcln)) {
          try
          {
            paramzzche.zza((zzcln)localRemoteException1, paramzzcgi);
          }
          catch (RemoteException localRemoteException2)
          {
            zzawy().zzazd().zzj("Failed to send attribute to the service", localRemoteException2);
          }
        } else if ((localRemoteException2 instanceof zzcgl)) {
          try
          {
            paramzzche.zza((zzcgl)localRemoteException2, paramzzcgi);
          }
          catch (RemoteException localRemoteException3)
          {
            zzawy().zzazd().zzj("Failed to send conditional property to the service", localRemoteException3);
          }
        } else {
          zzawy().zzazd().log("Discarding data. Unrecognized parcel type.");
        }
      }
      j += 1;
      break;
      return;
    }
  }
  
  protected final void zza(AppMeasurement.zzb paramzzb)
  {
    zzve();
    zzxf();
    zzj(new zzckl(this, paramzzb));
  }
  
  public final void zza(AtomicReference<String> paramAtomicReference)
  {
    zzve();
    zzxf();
    zzj(new zzckj(this, paramAtomicReference, zzbr(false)));
  }
  
  protected final void zza(AtomicReference<List<zzcgl>> paramAtomicReference, String paramString1, String paramString2, String paramString3)
  {
    zzve();
    zzxf();
    zzj(new zzckq(this, paramAtomicReference, paramString1, paramString2, paramString3, zzbr(false)));
  }
  
  protected final void zza(AtomicReference<List<zzcln>> paramAtomicReference, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    zzve();
    zzxf();
    zzj(new zzckr(this, paramAtomicReference, paramString1, paramString2, paramString3, paramBoolean, zzbr(false)));
  }
  
  protected final void zza(AtomicReference<List<zzcln>> paramAtomicReference, boolean paramBoolean)
  {
    zzve();
    zzxf();
    zzj(new zzckt(this, paramAtomicReference, zzbr(false), paramBoolean));
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  protected final void zzb(zzcln paramzzcln)
  {
    zzve();
    zzxf();
    if (zzawr().zza(paramzzcln)) {}
    for (boolean bool = true;; bool = false)
    {
      zzj(new zzcks(this, bool, paramzzcln, zzbr(true)));
      return;
    }
  }
  
  protected final void zzbaq()
  {
    zzve();
    zzxf();
    zzj(new zzckn(this, zzbr(true)));
  }
  
  protected final void zzbar()
  {
    zzve();
    zzxf();
    zzj(new zzckk(this, zzbr(true)));
  }
  
  final Boolean zzbas()
  {
    return this.zzjie;
  }
  
  protected final void zzc(zzcha paramzzcha, String paramString)
  {
    zzbq.checkNotNull(paramzzcha);
    zzve();
    zzxf();
    if (zzawr().zza(paramzzcha)) {}
    for (boolean bool = true;; bool = false)
    {
      zzj(new zzcko(this, true, bool, paramzzcha, zzbr(true), paramString));
      return;
    }
  }
  
  protected final void zzf(zzcgl paramzzcgl)
  {
    zzbq.checkNotNull(paramzzcgl);
    zzve();
    zzxf();
    if (zzawr().zzc(paramzzcgl)) {}
    for (boolean bool = true;; bool = false)
    {
      zzj(new zzckp(this, true, bool, new zzcgl(paramzzcgl), zzbr(true), paramzzcgl));
      return;
    }
  }
  
  final void zzyc()
  {
    int j = 1;
    zzve();
    zzxf();
    if (isConnected()) {
      return;
    }
    boolean bool2;
    if (this.zzjie == null)
    {
      zzve();
      zzxf();
      localObject = zzawz().zzazo();
      if ((localObject != null) && (((Boolean)localObject).booleanValue()))
      {
        bool2 = true;
        this.zzjie = Boolean.valueOf(bool2);
      }
    }
    else
    {
      if (!this.zzjie.booleanValue()) {
        break label414;
      }
      this.zzjic.zzbau();
      return;
    }
    boolean bool1;
    if (zzawn().zzazb() == 1)
    {
      i = 1;
      bool1 = true;
    }
    for (;;)
    {
      bool2 = bool1;
      if (i == 0) {
        break;
      }
      zzawz().zzbm(bool1);
      bool2 = bool1;
      break;
      zzawy().zzazj().log("Checking service availability");
      localObject = zzawu();
      i = zzf.zzafy().isGooglePlayServicesAvailable(((zzcjk)localObject).getContext());
      switch (i)
      {
      default: 
        zzawy().zzazf().zzj("Unexpected service status", Integer.valueOf(i));
        i = 0;
        bool1 = false;
        break;
      case 0: 
        zzawy().zzazj().log("Service available");
        i = 1;
        bool1 = true;
        break;
      case 1: 
        zzawy().zzazj().log("Service missing");
        i = 1;
        bool1 = false;
        break;
      case 18: 
        zzawy().zzazf().log("Service updating");
        i = 1;
        bool1 = true;
        break;
      case 2: 
        zzawy().zzazi().log("Service container out of date");
        localObject = zzawu();
        zzf.zzafy();
        if (zzf.zzcf(((zzcjk)localObject).getContext()) < 11400)
        {
          i = 1;
          bool1 = false;
        }
        else
        {
          localObject = zzawz().zzazo();
          if ((localObject == null) || (((Boolean)localObject).booleanValue())) {}
          for (bool1 = true;; bool1 = false)
          {
            i = 0;
            break;
          }
        }
        break;
      case 3: 
        zzawy().zzazf().log("Service disabled");
        i = 0;
        bool1 = false;
        break;
      case 9: 
        zzawy().zzazf().log("Service invalid");
        i = 0;
        bool1 = false;
      }
    }
    label414:
    Object localObject = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
    if ((localObject != null) && (((List)localObject).size() > 0)) {}
    for (int i = j; i != 0; i = 0)
    {
      localObject = new Intent("com.google.android.gms.measurement.START");
      ((Intent)localObject).setComponent(new ComponentName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"));
      this.zzjic.zzn((Intent)localObject);
      return;
    }
    zzawy().zzazd().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzckg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */