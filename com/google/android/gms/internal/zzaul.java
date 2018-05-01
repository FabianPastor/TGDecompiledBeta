package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;
import com.google.android.gms.common.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class zzaul
  extends zzauh
{
  private final zza zzbvD;
  private zzatt zzbvE;
  private Boolean zzbvF;
  private final zzatk zzbvG;
  private final zzauo zzbvH;
  private final List<Runnable> zzbvI = new ArrayList();
  private final zzatk zzbvJ;
  
  protected zzaul(zzaue paramzzaue)
  {
    super(paramzzaue);
    this.zzbvH = new zzauo(paramzzaue.zznR());
    this.zzbvD = new zza();
    this.zzbvG = new zzatk(paramzzaue)
    {
      public void run()
      {
        zzaul.zzc(zzaul.this);
      }
    };
    this.zzbvJ = new zzatk(paramzzaue)
    {
      public void run()
      {
        zzaul.this.zzKl().zzMb().log("Tasks have been queued for a long time");
      }
    };
  }
  
  @WorkerThread
  private void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzmR();
    if (this.zzbvE != null)
    {
      this.zzbvE = null;
      zzKl().zzMf().zzj("Disconnected from device MeasurementService", paramComponentName);
      zzNb();
    }
  }
  
  private boolean zzMZ()
  {
    zzKn().zzLh();
    List localList = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
    return (localList != null) && (localList.size() > 0);
  }
  
  @WorkerThread
  private void zzNb()
  {
    zzmR();
    zzoD();
  }
  
  @WorkerThread
  private void zzNc()
  {
    zzmR();
    zzKl().zzMf().zzj("Processing queued up service tasks", Integer.valueOf(this.zzbvI.size()));
    Iterator localIterator = this.zzbvI.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      zzKk().zzm(localRunnable);
    }
    this.zzbvI.clear();
    this.zzbvJ.cancel();
  }
  
  @WorkerThread
  private void zzo(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzmR();
    if (isConnected())
    {
      paramRunnable.run();
      return;
    }
    if (this.zzbvI.size() >= zzKn().zzLn())
    {
      zzKl().zzLZ().log("Discarding data. Max runnable queue size reached");
      return;
    }
    this.zzbvI.add(paramRunnable);
    this.zzbvJ.zzy(60000L);
    zzoD();
  }
  
  @WorkerThread
  private void zzoo()
  {
    zzmR();
    this.zzbvH.start();
    this.zzbvG.zzy(zzKn().zzpq());
  }
  
  @WorkerThread
  private void zzop()
  {
    zzmR();
    if (!isConnected()) {
      return;
    }
    zzKl().zzMf().log("Inactivity, disconnecting from the service");
    disconnect();
  }
  
  @WorkerThread
  public void disconnect()
  {
    zzmR();
    zzob();
    try
    {
      com.google.android.gms.common.stats.zza.zzyJ().zza(getContext(), this.zzbvD);
      this.zzbvE = null;
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
  
  @WorkerThread
  public boolean isConnected()
  {
    zzmR();
    zzob();
    return this.zzbvE != null;
  }
  
  @WorkerThread
  protected void zzMT()
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        zzatt localzzatt = zzaul.zzd(zzaul.this);
        if (localzzatt == null)
        {
          zzaul.this.zzKl().zzLZ().log("Discarding data. Failed to send app launch");
          return;
        }
        try
        {
          localzzatt.zza(zzaul.this.zzKb().zzfD(zzaul.this.zzKl().zzMg()));
          zzaul.this.zza(localzzatt, null);
          zzaul.zze(zzaul.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzaul.this.zzKl().zzLZ().zzj("Failed to send app launch to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected void zzMY()
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        zzatt localzzatt = zzaul.zzd(zzaul.this);
        if (localzzatt == null)
        {
          zzaul.this.zzKl().zzLZ().log("Failed to send measurementEnabled to service");
          return;
        }
        try
        {
          localzzatt.zzb(zzaul.this.zzKb().zzfD(zzaul.this.zzKl().zzMg()));
          zzaul.zze(zzaul.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzaul.this.zzKl().zzLZ().zzj("Failed to send measurementEnabled to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected boolean zzNa()
  {
    zzmR();
    zzob();
    zzKn().zzLh();
    zzKl().zzMf().log("Checking service availability");
    switch (zze.zzuY().isGooglePlayServicesAvailable(getContext()))
    {
    default: 
      return false;
    case 0: 
      zzKl().zzMf().log("Service available");
      return true;
    case 1: 
      zzKl().zzMf().log("Service missing");
      return false;
    case 18: 
      zzKl().zzMb().log("Service updating");
      return true;
    case 2: 
      zzKl().zzMe().log("Service container out of date");
      return true;
    case 3: 
      zzKl().zzMb().log("Service disabled");
      return false;
    }
    zzKl().zzMb().log("Service invalid");
    return false;
  }
  
  @WorkerThread
  protected void zza(zzatt paramzzatt)
  {
    zzmR();
    zzac.zzw(paramzzatt);
    this.zzbvE = paramzzatt;
    zzoo();
    zzNc();
  }
  
  @WorkerThread
  void zza(zzatt paramzzatt, com.google.android.gms.common.internal.safeparcel.zza paramzza)
  {
    zzmR();
    zzJW();
    zzob();
    int i = Build.VERSION.SDK_INT;
    zzKn().zzLh();
    ArrayList localArrayList = new ArrayList();
    zzKn().zzLq();
    int j = 0;
    i = 100;
    Object localObject;
    if ((j < 1001) && (i == 100))
    {
      localObject = zzKf().zzlD(100);
      if (localObject == null) {
        break label339;
      }
      localArrayList.addAll((Collection)localObject);
    }
    label339:
    for (i = ((List)localObject).size();; i = 0)
    {
      if ((paramzza != null) && (i < 100)) {
        localArrayList.add(paramzza);
      }
      localObject = localArrayList.iterator();
      while (((Iterator)localObject).hasNext())
      {
        com.google.android.gms.common.internal.safeparcel.zza localzza = (com.google.android.gms.common.internal.safeparcel.zza)((Iterator)localObject).next();
        if ((localzza instanceof zzatq)) {
          try
          {
            paramzzatt.zza((zzatq)localzza, zzKb().zzfD(zzKl().zzMg()));
          }
          catch (RemoteException localRemoteException1)
          {
            zzKl().zzLZ().zzj("Failed to send event to the service", localRemoteException1);
          }
        } else if ((localRemoteException1 instanceof zzauq)) {
          try
          {
            paramzzatt.zza((zzauq)localRemoteException1, zzKb().zzfD(zzKl().zzMg()));
          }
          catch (RemoteException localRemoteException2)
          {
            zzKl().zzLZ().zzj("Failed to send attribute to the service", localRemoteException2);
          }
        } else if ((localRemoteException2 instanceof zzatg)) {
          try
          {
            paramzzatt.zza((zzatg)localRemoteException2, zzKb().zzfD(zzKl().zzMg()));
          }
          catch (RemoteException localRemoteException3)
          {
            zzKl().zzLZ().zzj("Failed to send conditional property to the service", localRemoteException3);
          }
        } else {
          zzKl().zzLZ().log("Discarding data. Unrecognized parcel type.");
        }
      }
      j += 1;
      break;
      return;
    }
  }
  
  @WorkerThread
  protected void zza(final AppMeasurement.zzf paramzzf)
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        zzatt localzzatt = zzaul.zzd(zzaul.this);
        if (localzzatt == null)
        {
          zzaul.this.zzKl().zzLZ().log("Failed to send current screen to service");
          return;
        }
        for (;;)
        {
          try
          {
            if (paramzzf == null)
            {
              localzzatt.zza(0L, null, null, zzaul.this.getContext().getPackageName());
              zzaul.zze(zzaul.this);
              return;
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzaul.this.zzKl().zzLZ().zzj("Failed to send current screen to the service", localRemoteException);
            return;
          }
          localRemoteException.zza(paramzzf.zzbqg, paramzzf.zzbqe, paramzzf.zzbqf, zzaul.this.getContext().getPackageName());
        }
      }
    });
  }
  
  @WorkerThread
  public void zza(final AtomicReference<String> paramAtomicReference)
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatt = zzaul.zzd(zzaul.this);
            if (localzzatt == null) {
              zzaul.this.zzKl().zzLZ().log("Failed to get app instance id");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatt localzzatt;
            zzaul.this.zzKl().zzLZ().zzj("Failed to get app instance id", localRemoteException);
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        paramAtomicReference.set(localzzatt.zzc(zzaul.this.zzKb().zzfD(null)));
        zzaul.zze(zzaul.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<zzatg>> paramAtomicReference, final String paramString1, final String paramString2, final String paramString3)
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        AtomicReference localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatt = zzaul.zzd(zzaul.this);
            if (localzzatt == null)
            {
              zzaul.this.zzKl().zzLZ().zzd("Failed to get conditional properties", zzatx.zzfE(paramString1), paramString2, paramString3);
              paramAtomicReference.set(Collections.emptyList());
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatt localzzatt;
            zzaul.this.zzKl().zzLZ().zzd("Failed to get conditional properties", zzatx.zzfE(paramString1), paramString2, localRemoteException);
            paramAtomicReference.set(Collections.emptyList());
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        if (TextUtils.isEmpty(paramString1)) {
          paramAtomicReference.set(localzzatt.zza(paramString2, paramString3, zzaul.this.zzKb().zzfD(zzaul.this.zzKl().zzMg())));
        }
        for (;;)
        {
          zzaul.zze(zzaul.this);
          paramAtomicReference.notify();
          return;
          paramAtomicReference.set(((zzatt)localObject1).zzn(paramString1, paramString2, paramString3));
        }
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<zzauq>> paramAtomicReference, final String paramString1, final String paramString2, final String paramString3, final boolean paramBoolean)
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        AtomicReference localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatt = zzaul.zzd(zzaul.this);
            if (localzzatt == null)
            {
              zzaul.this.zzKl().zzLZ().zzd("Failed to get user properties", zzatx.zzfE(paramString1), paramString2, paramString3);
              paramAtomicReference.set(Collections.emptyList());
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatt localzzatt;
            zzaul.this.zzKl().zzLZ().zzd("Failed to get user properties", zzatx.zzfE(paramString1), paramString2, localRemoteException);
            paramAtomicReference.set(Collections.emptyList());
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        if (TextUtils.isEmpty(paramString1)) {
          paramAtomicReference.set(localzzatt.zza(paramString2, paramString3, paramBoolean, zzaul.this.zzKb().zzfD(zzaul.this.zzKl().zzMg())));
        }
        for (;;)
        {
          zzaul.zze(zzaul.this);
          paramAtomicReference.notify();
          return;
          paramAtomicReference.set(((zzatt)localObject1).zza(paramString1, paramString2, paramString3, paramBoolean));
        }
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<zzauq>> paramAtomicReference, final boolean paramBoolean)
  {
    zzmR();
    zzob();
    zzo(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzatt = zzaul.zzd(zzaul.this);
            if (localzzatt == null) {
              zzaul.this.zzKl().zzLZ().log("Failed to get user properties");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzatt localzzatt;
            zzaul.this.zzKl().zzLZ().zzj("Failed to get user properties", localRemoteException);
            paramAtomicReference.notify();
            continue;
          }
          finally
          {
            paramAtomicReference.notify();
          }
          try
          {
            paramAtomicReference.notify();
            return;
          }
          finally {}
        }
        paramAtomicReference.set(localzzatt.zza(zzaul.this.zzKb().zzfD(null), paramBoolean));
        zzaul.zze(zzaul.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  protected void zzb(final zzauq paramzzauq)
  {
    zzmR();
    zzob();
    int i = Build.VERSION.SDK_INT;
    zzKn().zzLh();
    if (zzKf().zza(paramzzauq)) {}
    for (final boolean bool = true;; bool = false)
    {
      zzo(new Runnable()
      {
        public void run()
        {
          zzatt localzzatt = zzaul.zzd(zzaul.this);
          if (localzzatt == null)
          {
            zzaul.this.zzKl().zzLZ().log("Discarding data. Failed to set user attribute");
            return;
          }
          zzaul localzzaul = zzaul.this;
          if (bool) {}
          for (Object localObject = null;; localObject = paramzzauq)
          {
            localzzaul.zza(localzzatt, (com.google.android.gms.common.internal.safeparcel.zza)localObject);
            zzaul.zze(zzaul.this);
            return;
          }
        }
      });
      return;
    }
  }
  
  @WorkerThread
  protected void zzc(final zzatq paramzzatq, final String paramString)
  {
    zzac.zzw(paramzzatq);
    zzmR();
    zzob();
    int i = Build.VERSION.SDK_INT;
    zzKn().zzLh();
    if (zzKf().zza(paramzzatq)) {}
    for (final boolean bool = true;; bool = false)
    {
      zzo(new Runnable()
      {
        public void run()
        {
          zzatt localzzatt = zzaul.zzd(zzaul.this);
          if (localzzatt == null)
          {
            zzaul.this.zzKl().zzLZ().log("Discarding data. Failed to send event to service");
            return;
          }
          Object localObject;
          if (this.zzbvN)
          {
            zzaul localzzaul = zzaul.this;
            if (bool)
            {
              localObject = null;
              localzzaul.zza(localzzatt, (com.google.android.gms.common.internal.safeparcel.zza)localObject);
            }
          }
          for (;;)
          {
            zzaul.zze(zzaul.this);
            return;
            localObject = paramzzatq;
            break;
            try
            {
              if (!TextUtils.isEmpty(paramString)) {
                break label134;
              }
              localzzatt.zza(paramzzatq, zzaul.this.zzKb().zzfD(zzaul.this.zzKl().zzMg()));
            }
            catch (RemoteException localRemoteException)
            {
              zzaul.this.zzKl().zzLZ().zzj("Failed to send event to the service", localRemoteException);
            }
            continue;
            label134:
            localzzatt.zza(paramzzatq, paramString, zzaul.this.zzKl().zzMg());
          }
        }
      });
      return;
    }
  }
  
  @WorkerThread
  protected void zzf(final zzatg paramzzatg)
  {
    zzac.zzw(paramzzatg);
    zzmR();
    zzob();
    zzKn().zzLh();
    if (zzKf().zzc(paramzzatg)) {}
    for (final boolean bool = true;; bool = false)
    {
      zzo(new Runnable()
      {
        public void run()
        {
          zzatt localzzatt = zzaul.zzd(zzaul.this);
          if (localzzatt == null)
          {
            zzaul.this.zzKl().zzLZ().log("Discarding data. Failed to send conditional user property to service");
            return;
          }
          Object localObject;
          if (this.zzbvN)
          {
            zzaul localzzaul = zzaul.this;
            if (bool)
            {
              localObject = null;
              localzzaul.zza(localzzatt, (com.google.android.gms.common.internal.safeparcel.zza)localObject);
            }
          }
          for (;;)
          {
            zzaul.zze(zzaul.this);
            return;
            localObject = this.zzbvP;
            break;
            try
            {
              if (!TextUtils.isEmpty(paramzzatg.packageName)) {
                break label137;
              }
              localzzatt.zza(this.zzbvP, zzaul.this.zzKb().zzfD(zzaul.this.zzKl().zzMg()));
            }
            catch (RemoteException localRemoteException)
            {
              zzaul.this.zzKl().zzLZ().zzj("Failed to send conditional user property to the service", localRemoteException);
            }
            continue;
            label137:
            localzzatt.zzb(this.zzbvP);
          }
        }
      });
      return;
    }
  }
  
  protected void zzmS() {}
  
  @WorkerThread
  void zzoD()
  {
    zzmR();
    zzob();
    if (isConnected()) {
      return;
    }
    if (this.zzbvF == null)
    {
      this.zzbvF = zzKm().zzMn();
      if (this.zzbvF == null)
      {
        zzKl().zzMf().log("State of service unknown");
        this.zzbvF = Boolean.valueOf(zzNa());
        zzKm().zzaJ(this.zzbvF.booleanValue());
      }
    }
    if (this.zzbvF.booleanValue())
    {
      zzKl().zzMf().log("Using measurement service");
      this.zzbvD.zzNd();
      return;
    }
    if (zzMZ())
    {
      zzKl().zzMf().log("Using local app measurement service");
      Intent localIntent = new Intent("com.google.android.gms.measurement.START");
      Context localContext = getContext();
      zzKn().zzLh();
      localIntent.setComponent(new ComponentName(localContext, "com.google.android.gms.measurement.AppMeasurementService"));
      this.zzbvD.zzz(localIntent);
      return;
    }
    zzKl().zzLZ().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
  }
  
  protected class zza
    implements ServiceConnection, zzf.zzb, zzf.zzc
  {
    private volatile boolean zzbvR;
    private volatile zzatw zzbvS;
    
    protected zza() {}
    
    /* Error */
    @MainThread
    public void onConnected(@android.support.annotation.Nullable final android.os.Bundle paramBundle)
    {
      // Byte code:
      //   0: ldc 50
      //   2: invokestatic 56	com/google/android/gms/common/internal/zzac:zzdj	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 58	com/google/android/gms/internal/zzaul$zza:zzbvS	Lcom/google/android/gms/internal/zzatw;
      //   11: invokevirtual 64	com/google/android/gms/internal/zzatw:zzxD	()Landroid/os/IInterface;
      //   14: checkcast 66	com/google/android/gms/internal/zzatt
      //   17: astore_1
      //   18: aload_0
      //   19: aconst_null
      //   20: putfield 58	com/google/android/gms/internal/zzaul$zza:zzbvS	Lcom/google/android/gms/internal/zzatw;
      //   23: aload_0
      //   24: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   27: invokevirtual 70	com/google/android/gms/internal/zzaul:zzKk	()Lcom/google/android/gms/internal/zzaud;
      //   30: new 19	com/google/android/gms/internal/zzaul$zza$3
      //   33: dup
      //   34: aload_0
      //   35: aload_1
      //   36: invokespecial 73	com/google/android/gms/internal/zzaul$zza$3:<init>	(Lcom/google/android/gms/internal/zzaul$zza;Lcom/google/android/gms/internal/zzatt;)V
      //   39: invokevirtual 79	com/google/android/gms/internal/zzaud:zzm	(Ljava/lang/Runnable;)V
      //   42: aload_0
      //   43: monitorexit
      //   44: return
      //   45: aload_0
      //   46: aconst_null
      //   47: putfield 58	com/google/android/gms/internal/zzaul$zza:zzbvS	Lcom/google/android/gms/internal/zzatw;
      //   50: aload_0
      //   51: iconst_0
      //   52: putfield 40	com/google/android/gms/internal/zzaul$zza:zzbvR	Z
      //   55: goto -13 -> 42
      //   58: astore_1
      //   59: aload_0
      //   60: monitorexit
      //   61: aload_1
      //   62: athrow
      //   63: astore_1
      //   64: goto -19 -> 45
      //   67: astore_1
      //   68: goto -23 -> 45
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	71	0	this	zza
      //   0	71	1	paramBundle	android.os.Bundle
      // Exception table:
      //   from	to	target	type
      //   7	42	58	finally
      //   42	44	58	finally
      //   45	55	58	finally
      //   59	61	58	finally
      //   7	42	63	android/os/DeadObjectException
      //   7	42	67	java/lang/IllegalStateException
    }
    
    @MainThread
    public void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult)
    {
      zzac.zzdj("MeasurementServiceConnection.onConnectionFailed");
      zzatx localzzatx = zzaul.this.zzbqb.zzMv();
      if (localzzatx != null) {
        localzzatx.zzMb().zzj("Service connection failed", paramConnectionResult);
      }
      try
      {
        this.zzbvR = false;
        this.zzbvS = null;
        zzaul.this.zzKk().zzm(new Runnable()
        {
          public void run()
          {
            zzaul.zza(zzaul.this, null);
            zzaul.zzb(zzaul.this);
          }
        });
        return;
      }
      finally {}
    }
    
    @MainThread
    public void onConnectionSuspended(int paramInt)
    {
      zzac.zzdj("MeasurementServiceConnection.onConnectionSuspended");
      zzaul.this.zzKl().zzMe().log("Service connection suspended");
      zzaul.this.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzaul localzzaul = zzaul.this;
          Context localContext = zzaul.this.getContext();
          zzaul.this.zzKn().zzLh();
          zzaul.zza(localzzaul, new ComponentName(localContext, "com.google.android.gms.measurement.AppMeasurementService"));
        }
      });
    }
    
    /* Error */
    @MainThread
    public void onServiceConnected(final ComponentName paramComponentName, android.os.IBinder paramIBinder)
    {
      // Byte code:
      //   0: ldc -119
      //   2: invokestatic 56	com/google/android/gms/common/internal/zzac:zzdj	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_2
      //   8: ifnonnull +26 -> 34
      //   11: aload_0
      //   12: iconst_0
      //   13: putfield 40	com/google/android/gms/internal/zzaul$zza:zzbvR	Z
      //   16: aload_0
      //   17: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   20: invokevirtual 120	com/google/android/gms/internal/zzaul:zzKl	()Lcom/google/android/gms/internal/zzatx;
      //   23: invokevirtual 140	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
      //   26: ldc -114
      //   28: invokevirtual 128	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
      //   31: aload_0
      //   32: monitorexit
      //   33: return
      //   34: aconst_null
      //   35: astore 4
      //   37: aconst_null
      //   38: astore_3
      //   39: aload 4
      //   41: astore_1
      //   42: aload_2
      //   43: invokeinterface 148 1 0
      //   48: astore 5
      //   50: aload 4
      //   52: astore_1
      //   53: ldc -106
      //   55: aload 5
      //   57: invokevirtual 156	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   60: ifeq +67 -> 127
      //   63: aload 4
      //   65: astore_1
      //   66: aload_2
      //   67: invokestatic 162	com/google/android/gms/internal/zzatt$zza:zzes	(Landroid/os/IBinder;)Lcom/google/android/gms/internal/zzatt;
      //   70: astore_2
      //   71: aload_2
      //   72: astore_1
      //   73: aload_0
      //   74: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   77: invokevirtual 120	com/google/android/gms/internal/zzaul:zzKl	()Lcom/google/android/gms/internal/zzatx;
      //   80: invokevirtual 165	com/google/android/gms/internal/zzatx:zzMf	()Lcom/google/android/gms/internal/zzatx$zza;
      //   83: ldc -89
      //   85: invokevirtual 128	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
      //   88: aload_2
      //   89: astore_1
      //   90: aload_1
      //   91: ifnonnull +80 -> 171
      //   94: aload_0
      //   95: iconst_0
      //   96: putfield 40	com/google/android/gms/internal/zzaul$zza:zzbvR	Z
      //   99: invokestatic 173	com/google/android/gms/common/stats/zza:zzyJ	()Lcom/google/android/gms/common/stats/zza;
      //   102: aload_0
      //   103: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   106: invokevirtual 177	com/google/android/gms/internal/zzaul:getContext	()Landroid/content/Context;
      //   109: aload_0
      //   110: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   113: invokestatic 180	com/google/android/gms/internal/zzaul:zza	(Lcom/google/android/gms/internal/zzaul;)Lcom/google/android/gms/internal/zzaul$zza;
      //   116: invokevirtual 183	com/google/android/gms/common/stats/zza:zza	(Landroid/content/Context;Landroid/content/ServiceConnection;)V
      //   119: aload_0
      //   120: monitorexit
      //   121: return
      //   122: astore_1
      //   123: aload_0
      //   124: monitorexit
      //   125: aload_1
      //   126: athrow
      //   127: aload 4
      //   129: astore_1
      //   130: aload_0
      //   131: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   134: invokevirtual 120	com/google/android/gms/internal/zzaul:zzKl	()Lcom/google/android/gms/internal/zzatx;
      //   137: invokevirtual 140	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
      //   140: ldc -71
      //   142: aload 5
      //   144: invokevirtual 110	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
      //   147: aload_3
      //   148: astore_1
      //   149: goto -59 -> 90
      //   152: astore_2
      //   153: aload_0
      //   154: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   157: invokevirtual 120	com/google/android/gms/internal/zzaul:zzKl	()Lcom/google/android/gms/internal/zzatx;
      //   160: invokevirtual 140	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
      //   163: ldc -69
      //   165: invokevirtual 128	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
      //   168: goto -78 -> 90
      //   171: aload_0
      //   172: getfield 33	com/google/android/gms/internal/zzaul$zza:zzbvK	Lcom/google/android/gms/internal/zzaul;
      //   175: invokevirtual 70	com/google/android/gms/internal/zzaul:zzKk	()Lcom/google/android/gms/internal/zzaud;
      //   178: new 15	com/google/android/gms/internal/zzaul$zza$1
      //   181: dup
      //   182: aload_0
      //   183: aload_1
      //   184: invokespecial 188	com/google/android/gms/internal/zzaul$zza$1:<init>	(Lcom/google/android/gms/internal/zzaul$zza;Lcom/google/android/gms/internal/zzatt;)V
      //   187: invokevirtual 79	com/google/android/gms/internal/zzaud:zzm	(Ljava/lang/Runnable;)V
      //   190: goto -71 -> 119
      //   193: astore_1
      //   194: goto -75 -> 119
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	197	0	this	zza
      //   0	197	1	paramComponentName	ComponentName
      //   0	197	2	paramIBinder	android.os.IBinder
      //   38	110	3	localObject1	Object
      //   35	93	4	localObject2	Object
      //   48	95	5	str	String
      // Exception table:
      //   from	to	target	type
      //   11	33	122	finally
      //   42	50	122	finally
      //   53	63	122	finally
      //   66	71	122	finally
      //   73	88	122	finally
      //   94	99	122	finally
      //   99	119	122	finally
      //   119	121	122	finally
      //   123	125	122	finally
      //   130	147	122	finally
      //   153	168	122	finally
      //   171	190	122	finally
      //   42	50	152	android/os/RemoteException
      //   53	63	152	android/os/RemoteException
      //   66	71	152	android/os/RemoteException
      //   73	88	152	android/os/RemoteException
      //   130	147	152	android/os/RemoteException
      //   99	119	193	java/lang/IllegalArgumentException
    }
    
    @MainThread
    public void onServiceDisconnected(final ComponentName paramComponentName)
    {
      zzac.zzdj("MeasurementServiceConnection.onServiceDisconnected");
      zzaul.this.zzKl().zzMe().log("Service disconnected");
      zzaul.this.zzKk().zzm(new Runnable()
      {
        public void run()
        {
          zzaul.zza(zzaul.this, paramComponentName);
        }
      });
    }
    
    @WorkerThread
    public void zzNd()
    {
      zzaul.this.zzmR();
      Context localContext1 = zzaul.this.getContext();
      try
      {
        if (this.zzbvR)
        {
          zzaul.this.zzKl().zzMf().log("Connection attempt already in progress");
          return;
        }
        if (this.zzbvS != null)
        {
          zzaul.this.zzKl().zzMf().log("Already awaiting connection attempt");
          return;
        }
      }
      finally {}
      this.zzbvS = new zzatw(localContext2, Looper.getMainLooper(), this, this);
      zzaul.this.zzKl().zzMf().log("Connecting to remote service");
      this.zzbvR = true;
      this.zzbvS.zzxz();
    }
    
    @WorkerThread
    public void zzz(Intent paramIntent)
    {
      zzaul.this.zzmR();
      Context localContext = zzaul.this.getContext();
      com.google.android.gms.common.stats.zza localzza = com.google.android.gms.common.stats.zza.zzyJ();
      try
      {
        if (this.zzbvR)
        {
          zzaul.this.zzKl().zzMf().log("Connection attempt already in progress");
          return;
        }
        this.zzbvR = true;
        localzza.zza(localContext, paramIntent, zzaul.zza(zzaul.this), 129);
        return;
      }
      finally {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaul.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */