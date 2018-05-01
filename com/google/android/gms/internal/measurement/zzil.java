package com.google.android.gms.internal.measurement;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.stats.ConnectionTracker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class zzil
  extends zzhk
{
  private final zziz zzapr;
  private zzey zzaps;
  private volatile Boolean zzapt;
  private final zzem zzapu;
  private final zzjp zzapv;
  private final List<Runnable> zzapw = new ArrayList();
  private final zzem zzapx;
  
  protected zzil(zzgl paramzzgl)
  {
    super(paramzzgl);
    this.zzapv = new zzjp(paramzzgl.zzbt());
    this.zzapr = new zziz(this);
    this.zzapu = new zzim(this, paramzzgl);
    this.zzapx = new zzir(this, paramzzgl);
  }
  
  private final void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzab();
    if (this.zzaps != null)
    {
      this.zzaps = null;
      zzgg().zzir().zzg("Disconnected from device MeasurementService", paramComponentName);
      zzab();
      zzdf();
    }
  }
  
  private final void zzcu()
  {
    zzab();
    this.zzapv.start();
    this.zzapu.zzh(((Long)zzew.zzahr.get()).longValue());
  }
  
  private final void zzcv()
  {
    zzab();
    if (!isConnected()) {}
    for (;;)
    {
      return;
      zzgg().zzir().log("Inactivity, disconnecting from the service");
      disconnect();
    }
  }
  
  private final void zzf(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzab();
    if (isConnected()) {
      paramRunnable.run();
    }
    for (;;)
    {
      return;
      if (this.zzapw.size() >= 1000L)
      {
        zzgg().zzil().log("Discarding data. Max runnable queue size reached");
      }
      else
      {
        this.zzapw.add(paramRunnable);
        this.zzapx.zzh(60000L);
        zzdf();
      }
    }
  }
  
  private final void zzko()
  {
    zzab();
    zzgg().zzir().zzg("Processing queued up service tasks", Integer.valueOf(this.zzapw.size()));
    Iterator localIterator = this.zzapw.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      try
      {
        localRunnable.run();
      }
      catch (Throwable localThrowable)
      {
        zzgg().zzil().zzg("Task exception while flushing queue", localThrowable);
      }
    }
    this.zzapw.clear();
    this.zzapx.cancel();
  }
  
  private final zzec zzl(boolean paramBoolean)
  {
    zzfb localzzfb = zzfv();
    if (paramBoolean) {}
    for (String str = zzgg().zzit();; str = null) {
      return localzzfb.zzbd(str);
    }
  }
  
  public final void disconnect()
  {
    zzab();
    zzch();
    try
    {
      ConnectionTracker.getInstance().unbindService(getContext(), this.zzapr);
      this.zzaps = null;
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
    zzab();
    zzch();
    if (this.zzaps != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected final void resetAnalyticsData()
  {
    zzab();
    zzch();
    zzec localzzec = zzl(false);
    zzfz().resetAnalyticsData();
    zzf(new zzin(this, localzzec));
  }
  
  protected final void zza(zzey paramzzey)
  {
    zzab();
    Preconditions.checkNotNull(paramzzey);
    this.zzaps = paramzzey;
    zzcu();
    zzko();
  }
  
  final void zza(zzey paramzzey, AbstractSafeParcelable paramAbstractSafeParcelable, zzec paramzzec)
  {
    zzab();
    zzch();
    int i = 0;
    int j = 100;
    ArrayList localArrayList;
    Object localObject;
    if ((i < 1001) && (j == 100))
    {
      localArrayList = new ArrayList();
      localObject = zzfz().zzp(100);
      if (localObject == null) {
        break label289;
      }
      localArrayList.addAll((Collection)localObject);
    }
    label289:
    for (j = ((List)localObject).size();; j = 0)
    {
      if ((paramAbstractSafeParcelable != null) && (j < 100)) {
        localArrayList.add(paramAbstractSafeParcelable);
      }
      localArrayList = (ArrayList)localArrayList;
      int k = localArrayList.size();
      int m = 0;
      while (m < k)
      {
        localObject = localArrayList.get(m);
        m++;
        localObject = (AbstractSafeParcelable)localObject;
        if ((localObject instanceof zzeu)) {
          try
          {
            paramzzey.zza((zzeu)localObject, paramzzec);
          }
          catch (RemoteException localRemoteException1)
          {
            zzgg().zzil().zzg("Failed to send event to the service", localRemoteException1);
          }
        } else if ((localRemoteException1 instanceof zzjs)) {
          try
          {
            paramzzey.zza((zzjs)localRemoteException1, paramzzec);
          }
          catch (RemoteException localRemoteException2)
          {
            zzgg().zzil().zzg("Failed to send attribute to the service", localRemoteException2);
          }
        } else if ((localRemoteException2 instanceof zzef)) {
          try
          {
            paramzzey.zza((zzef)localRemoteException2, paramzzec);
          }
          catch (RemoteException localRemoteException3)
          {
            zzgg().zzil().zzg("Failed to send conditional property to the service", localRemoteException3);
          }
        } else {
          zzgg().zzil().log("Discarding data. Unrecognized parcel type.");
        }
      }
      i++;
      break;
      return;
    }
  }
  
  protected final void zza(zzig paramzzig)
  {
    zzab();
    zzch();
    zzf(new zziq(this, paramzzig));
  }
  
  public final void zza(AtomicReference<String> paramAtomicReference)
  {
    zzab();
    zzch();
    zzf(new zzio(this, paramAtomicReference, zzl(false)));
  }
  
  protected final void zza(AtomicReference<List<zzef>> paramAtomicReference, String paramString1, String paramString2, String paramString3)
  {
    zzab();
    zzch();
    zzf(new zziv(this, paramAtomicReference, paramString1, paramString2, paramString3, zzl(false)));
  }
  
  protected final void zza(AtomicReference<List<zzjs>> paramAtomicReference, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    zzab();
    zzch();
    zzf(new zziw(this, paramAtomicReference, paramString1, paramString2, paramString3, paramBoolean, zzl(false)));
  }
  
  protected final void zza(AtomicReference<List<zzjs>> paramAtomicReference, boolean paramBoolean)
  {
    zzab();
    zzch();
    zzf(new zziy(this, paramAtomicReference, zzl(false), paramBoolean));
  }
  
  protected final void zzb(zzjs paramzzjs)
  {
    zzab();
    zzch();
    if (zzfz().zza(paramzzjs)) {}
    for (boolean bool = true;; bool = false)
    {
      zzf(new zzix(this, bool, paramzzjs, zzl(true)));
      return;
    }
  }
  
  protected final void zzc(zzeu paramzzeu, String paramString)
  {
    Preconditions.checkNotNull(paramzzeu);
    zzab();
    zzch();
    if (zzfz().zza(paramzzeu)) {}
    for (boolean bool = true;; bool = false)
    {
      zzf(new zzit(this, true, bool, paramzzeu, zzl(true), paramString));
      return;
    }
  }
  
  final void zzdf()
  {
    int i = 1;
    zzab();
    zzch();
    if (isConnected()) {}
    for (;;)
    {
      return;
      boolean bool1;
      if (this.zzapt == null)
      {
        zzab();
        zzch();
        localObject = zzgh().zzix();
        if ((localObject != null) && (((Boolean)localObject).booleanValue()))
        {
          bool1 = true;
          this.zzapt = Boolean.valueOf(bool1);
        }
      }
      else
      {
        if (!this.zzapt.booleanValue()) {
          break label422;
        }
        this.zzapr.zzkp();
        continue;
      }
      boolean bool2;
      if (zzfv().zzij() == 1)
      {
        j = 1;
        bool2 = true;
      }
      for (;;)
      {
        bool1 = bool2;
        if (j == 0) {
          break;
        }
        zzgh().zzf(bool2);
        bool1 = bool2;
        break;
        zzgg().zzir().log("Checking service availability");
        localObject = zzgc();
        j = GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(((zzhj)localObject).getContext(), 12451);
        switch (j)
        {
        default: 
          zzgg().zzin().zzg("Unexpected service status", Integer.valueOf(j));
          j = 0;
          bool2 = false;
          break;
        case 0: 
          zzgg().zzir().log("Service available");
          j = 1;
          bool2 = true;
          break;
        case 1: 
          zzgg().zzir().log("Service missing");
          j = 1;
          bool2 = false;
          break;
        case 18: 
          zzgg().zzin().log("Service updating");
          j = 1;
          bool2 = true;
          break;
        case 2: 
          zzgg().zziq().log("Service container out of date");
          if (zzgc().zzkv() < 12400)
          {
            j = 1;
            bool2 = false;
          }
          else
          {
            localObject = zzgh().zzix();
            if ((localObject == null) || (((Boolean)localObject).booleanValue())) {}
            for (bool2 = true;; bool2 = false)
            {
              j = 0;
              break;
            }
          }
          break;
        case 3: 
          zzgg().zzin().log("Service disabled");
          j = 0;
          bool2 = false;
          break;
        case 9: 
          zzgg().zzin().log("Service invalid");
          j = 0;
          bool2 = false;
        }
      }
      label422:
      Object localObject = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
      if ((localObject != null) && (((List)localObject).size() > 0)) {}
      for (int j = i;; j = 0)
      {
        if (j == 0) {
          break label521;
        }
        localObject = new Intent("com.google.android.gms.measurement.START");
        ((Intent)localObject).setComponent(new ComponentName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"));
        this.zzapr.zzc((Intent)localObject);
        break;
      }
      label521:
      zzgg().zzil().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
    }
  }
  
  protected final void zzf(zzef paramzzef)
  {
    Preconditions.checkNotNull(paramzzef);
    zzab();
    zzch();
    if (zzfz().zzc(paramzzef)) {}
    for (boolean bool = true;; bool = false)
    {
      zzf(new zziu(this, true, bool, new zzef(paramzzef), zzl(true), paramzzef));
      return;
    }
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  protected final void zzkj()
  {
    zzab();
    zzch();
    zzf(new zzip(this, zzl(true)));
  }
  
  protected final void zzkm()
  {
    zzab();
    zzch();
    zzf(new zzis(this, zzl(true)));
  }
  
  final Boolean zzkn()
  {
    return this.zzapt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */