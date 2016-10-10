package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;
import com.google.android.gms.common.stats.zzb;
import com.google.android.gms.common.zzc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class zzad
  extends zzaa
{
  private final zza arP;
  private zzm arQ;
  private Boolean arR;
  private final zzf arS;
  private final zzah arT;
  private final List<Runnable> arU = new ArrayList();
  private final zzf arV;
  
  protected zzad(zzx paramzzx)
  {
    super(paramzzx);
    this.arT = new zzah(paramzzx.zzaan());
    this.arP = new zza();
    this.arS = new zzf(paramzzx)
    {
      public void run()
      {
        zzad.zzb(zzad.this);
      }
    };
    this.arV = new zzf(paramzzx)
    {
      public void run()
      {
        zzad.this.zzbvg().zzbwe().log("Tasks have been queued for a long time");
      }
    };
  }
  
  @WorkerThread
  private void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzyl();
    if (this.arQ != null)
    {
      this.arQ = null;
      zzbvg().zzbwj().zzj("Disconnected from device MeasurementService", paramComponentName);
      zzbyc();
    }
  }
  
  @WorkerThread
  private void zza(zzm paramzzm)
  {
    zzyl();
    zzac.zzy(paramzzm);
    this.arQ = paramzzm;
    zzabk();
    zzbyd();
  }
  
  @WorkerThread
  private void zzabk()
  {
    zzyl();
    this.arT.start();
    if (!this.anq.zzbxg()) {
      this.arS.zzx(zzbvi().zzado());
    }
  }
  
  @WorkerThread
  private void zzabl()
  {
    zzyl();
    if (!isConnected()) {
      return;
    }
    zzbvg().zzbwj().log("Inactivity, disconnecting from the service");
    disconnect();
  }
  
  private boolean zzbya()
  {
    if (zzbvi().zzact()) {}
    List localList;
    do
    {
      return false;
      localList = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
    } while ((localList == null) || (localList.size() <= 0));
    return true;
  }
  
  @WorkerThread
  private void zzbyc()
  {
    zzyl();
    zzabz();
  }
  
  @WorkerThread
  private void zzbyd()
  {
    zzyl();
    zzbvg().zzbwj().zzj("Processing queued up service tasks", Integer.valueOf(this.arU.size()));
    Iterator localIterator = this.arU.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      zzbvf().zzm(localRunnable);
    }
    this.arU.clear();
    this.arV.cancel();
  }
  
  @WorkerThread
  private void zzo(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzyl();
    if (isConnected())
    {
      paramRunnable.run();
      return;
    }
    if (this.arU.size() >= zzbvi().zzbug())
    {
      zzbvg().zzbwc().log("Discarding data. Max runnable queue size reached");
      return;
    }
    this.arU.add(paramRunnable);
    if (!this.anq.zzbxg()) {
      this.arV.zzx(60000L);
    }
    zzabz();
  }
  
  @WorkerThread
  public void disconnect()
  {
    zzyl();
    zzaax();
    try
    {
      zzb.zzawu().zza(getContext(), this.arP);
      this.arQ = null;
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
    zzyl();
    zzaax();
    return this.arQ != null;
  }
  
  @WorkerThread
  protected void zza(final UserAttributeParcel paramUserAttributeParcel)
  {
    zzyl();
    zzaax();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzad.zzc(zzad.this);
        if (localzzm == null)
        {
          zzad.this.zzbvg().zzbwc().log("Discarding data. Failed to set user attribute");
          return;
        }
        try
        {
          localzzm.zza(paramUserAttributeParcel, zzad.this.zzbuy().zzmi(zzad.this.zzbvg().zzbwk()));
          zzad.zzd(zzad.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzad.this.zzbvg().zzbwc().zzj("Failed to send attribute to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<UserAttributeParcel>> paramAtomicReference, final boolean paramBoolean)
  {
    zzyl();
    zzaax();
    zzo(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzm = zzad.zzc(zzad.this);
            if (localzzm == null) {
              zzad.this.zzbvg().zzbwc().log("Failed to get user properties");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzm localzzm;
            zzad.this.zzbvg().zzbwc().zzj("Failed to get user properties", localRemoteException);
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
        paramAtomicReference.set(localzzm.zza(zzad.this.zzbuy().zzmi(null), paramBoolean));
        zzad.zzd(zzad.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  void zzabz()
  {
    zzyl();
    zzaax();
    if (isConnected()) {
      return;
    }
    if (this.arR == null)
    {
      this.arR = zzbvh().zzbwq();
      if (this.arR == null)
      {
        zzbvg().zzbwj().log("State of service unknown");
        this.arR = Boolean.valueOf(zzbyb());
        zzbvh().zzcf(this.arR.booleanValue());
      }
    }
    if (this.arR.booleanValue())
    {
      zzbvg().zzbwj().log("Using measurement service");
      this.arP.zzbye();
      return;
    }
    if ((!this.anq.zzbxg()) && (zzbya()))
    {
      zzbvg().zzbwj().log("Using local app measurement service");
      Intent localIntent = new Intent("com.google.android.gms.measurement.START");
      Context localContext = getContext();
      if (zzbvi().zzact()) {}
      for (String str = "com.google.android.gms.measurement.PackageMeasurementService";; str = "com.google.android.gms.measurement.AppMeasurementService")
      {
        localIntent.setComponent(new ComponentName(localContext, str));
        this.arP.zzac(localIntent);
        return;
      }
    }
    zzbvg().zzbwc().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
  }
  
  @WorkerThread
  protected void zzbxw()
  {
    zzyl();
    zzaax();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzad.zzc(zzad.this);
        if (localzzm == null)
        {
          zzad.this.zzbvg().zzbwc().log("Discarding data. Failed to send app launch");
          return;
        }
        try
        {
          localzzm.zza(zzad.this.zzbuy().zzmi(zzad.this.zzbvg().zzbwk()));
          zzad.zzd(zzad.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzad.this.zzbvg().zzbwc().zzj("Failed to send app launch to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected void zzbxz()
  {
    zzyl();
    zzaax();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzad.zzc(zzad.this);
        if (localzzm == null)
        {
          zzad.this.zzbvg().zzbwc().log("Failed to send measurementEnabled to service");
          return;
        }
        try
        {
          localzzm.zzb(zzad.this.zzbuy().zzmi(zzad.this.zzbvg().zzbwk()));
          zzad.zzd(zzad.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzad.this.zzbvg().zzbwc().zzj("Failed to send measurementEnabled to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected boolean zzbyb()
  {
    zzyl();
    zzaax();
    if (zzbvi().zzact()) {
      return true;
    }
    zzbvg().zzbwj().log("Checking service availability");
    switch (zzc.zzapd().isGooglePlayServicesAvailable(getContext()))
    {
    default: 
      return false;
    case 0: 
      zzbvg().zzbwj().log("Service available");
      return true;
    case 1: 
      zzbvg().zzbwj().log("Service missing");
      return false;
    case 18: 
      zzbvg().zzbwe().log("Service updating");
      return true;
    case 2: 
      zzbvg().zzbwi().log("Service container out of date");
      return true;
    case 3: 
      zzbvg().zzbwe().log("Service disabled");
      return false;
    }
    zzbvg().zzbwe().log("Service invalid");
    return false;
  }
  
  @WorkerThread
  protected void zzc(final EventParcel paramEventParcel, final String paramString)
  {
    zzac.zzy(paramEventParcel);
    zzyl();
    zzaax();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzad.zzc(zzad.this);
        if (localzzm == null)
        {
          zzad.this.zzbvg().zzbwc().log("Discarding data. Failed to send event to service");
          return;
        }
        for (;;)
        {
          try
          {
            if (TextUtils.isEmpty(paramString))
            {
              localzzm.zza(paramEventParcel, zzad.this.zzbuy().zzmi(zzad.this.zzbvg().zzbwk()));
              zzad.zzd(zzad.this);
              return;
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzad.this.zzbvg().zzbwc().zzj("Failed to send event to the service", localRemoteException);
            return;
          }
          localRemoteException.zza(paramEventParcel, paramString, zzad.this.zzbvg().zzbwk());
        }
      }
    });
  }
  
  protected void zzym() {}
  
  protected class zza
    implements ServiceConnection, zze.zzb, zze.zzc
  {
    private volatile boolean arY;
    private volatile zzo arZ;
    
    protected zza() {}
    
    /* Error */
    @MainThread
    public void onConnected(@android.support.annotation.Nullable final android.os.Bundle paramBundle)
    {
      // Byte code:
      //   0: ldc 48
      //   2: invokestatic 54	com/google/android/gms/common/internal/zzac:zzhq	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 56	com/google/android/gms/measurement/internal/zzad$zza:arZ	Lcom/google/android/gms/measurement/internal/zzo;
      //   11: invokevirtual 62	com/google/android/gms/measurement/internal/zzo:zzatx	()Landroid/os/IInterface;
      //   14: checkcast 64	com/google/android/gms/measurement/internal/zzm
      //   17: astore_1
      //   18: aload_0
      //   19: aconst_null
      //   20: putfield 56	com/google/android/gms/measurement/internal/zzad$zza:arZ	Lcom/google/android/gms/measurement/internal/zzo;
      //   23: aload_0
      //   24: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   27: invokevirtual 68	com/google/android/gms/measurement/internal/zzad:zzbvf	()Lcom/google/android/gms/measurement/internal/zzw;
      //   30: new 19	com/google/android/gms/measurement/internal/zzad$zza$3
      //   33: dup
      //   34: aload_0
      //   35: aload_1
      //   36: invokespecial 71	com/google/android/gms/measurement/internal/zzad$zza$3:<init>	(Lcom/google/android/gms/measurement/internal/zzad$zza;Lcom/google/android/gms/measurement/internal/zzm;)V
      //   39: invokevirtual 77	com/google/android/gms/measurement/internal/zzw:zzm	(Ljava/lang/Runnable;)V
      //   42: aload_0
      //   43: monitorexit
      //   44: return
      //   45: aload_0
      //   46: aconst_null
      //   47: putfield 56	com/google/android/gms/measurement/internal/zzad$zza:arZ	Lcom/google/android/gms/measurement/internal/zzo;
      //   50: aload_0
      //   51: iconst_0
      //   52: putfield 38	com/google/android/gms/measurement/internal/zzad$zza:arY	Z
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
      zzac.zzhq("MeasurementServiceConnection.onConnectionFailed");
      zzp localzzp = zzad.this.anq.zzbww();
      if (localzzp != null) {
        localzzp.zzbwe().zzj("Service connection failed", paramConnectionResult);
      }
      try
      {
        this.arY = false;
        this.arZ = null;
        return;
      }
      finally {}
    }
    
    @MainThread
    public void onConnectionSuspended(int paramInt)
    {
      zzac.zzhq("MeasurementServiceConnection.onConnectionSuspended");
      zzad.this.zzbvg().zzbwi().log("Service connection suspended");
      zzad.this.zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzad localzzad = zzad.this;
          Context localContext = zzad.this.getContext();
          if ((zzad.this.zzbvi().zzact()) && (!zzad.this.anq.zzbxg())) {}
          for (String str = "com.google.android.gms.measurement.PackageMeasurementService";; str = "com.google.android.gms.measurement.AppMeasurementService")
          {
            zzad.zza(localzzad, new ComponentName(localContext, str));
            return;
          }
        }
      });
    }
    
    /* Error */
    @MainThread
    public void onServiceConnected(final ComponentName paramComponentName, android.os.IBinder paramIBinder)
    {
      // Byte code:
      //   0: ldc -122
      //   2: invokestatic 54	com/google/android/gms/common/internal/zzac:zzhq	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_2
      //   8: ifnonnull +26 -> 34
      //   11: aload_0
      //   12: iconst_0
      //   13: putfield 38	com/google/android/gms/measurement/internal/zzad$zza:arY	Z
      //   16: aload_0
      //   17: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   20: invokevirtual 115	com/google/android/gms/measurement/internal/zzad:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   23: invokevirtual 137	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   26: ldc -117
      //   28: invokevirtual 123	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
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
      //   43: invokeinterface 145 1 0
      //   48: astore 5
      //   50: aload 4
      //   52: astore_1
      //   53: ldc -109
      //   55: aload 5
      //   57: invokevirtual 153	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   60: ifeq +67 -> 127
      //   63: aload 4
      //   65: astore_1
      //   66: aload_2
      //   67: invokestatic 159	com/google/android/gms/measurement/internal/zzm$zza:zzjl	(Landroid/os/IBinder;)Lcom/google/android/gms/measurement/internal/zzm;
      //   70: astore_2
      //   71: aload_2
      //   72: astore_1
      //   73: aload_0
      //   74: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   77: invokevirtual 115	com/google/android/gms/measurement/internal/zzad:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   80: invokevirtual 162	com/google/android/gms/measurement/internal/zzp:zzbwj	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   83: ldc -92
      //   85: invokevirtual 123	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
      //   88: aload_2
      //   89: astore_1
      //   90: aload_1
      //   91: ifnonnull +80 -> 171
      //   94: aload_0
      //   95: iconst_0
      //   96: putfield 38	com/google/android/gms/measurement/internal/zzad$zza:arY	Z
      //   99: invokestatic 170	com/google/android/gms/common/stats/zzb:zzawu	()Lcom/google/android/gms/common/stats/zzb;
      //   102: aload_0
      //   103: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   106: invokevirtual 174	com/google/android/gms/measurement/internal/zzad:getContext	()Landroid/content/Context;
      //   109: aload_0
      //   110: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   113: invokestatic 177	com/google/android/gms/measurement/internal/zzad:zza	(Lcom/google/android/gms/measurement/internal/zzad;)Lcom/google/android/gms/measurement/internal/zzad$zza;
      //   116: invokevirtual 180	com/google/android/gms/common/stats/zzb:zza	(Landroid/content/Context;Landroid/content/ServiceConnection;)V
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
      //   131: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   134: invokevirtual 115	com/google/android/gms/measurement/internal/zzad:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   137: invokevirtual 137	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   140: ldc -74
      //   142: aload 5
      //   144: invokevirtual 108	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
      //   147: aload_3
      //   148: astore_1
      //   149: goto -59 -> 90
      //   152: astore_2
      //   153: aload_0
      //   154: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   157: invokevirtual 115	com/google/android/gms/measurement/internal/zzad:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   160: invokevirtual 137	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   163: ldc -72
      //   165: invokevirtual 123	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
      //   168: goto -78 -> 90
      //   171: aload_0
      //   172: getfield 31	com/google/android/gms/measurement/internal/zzad$zza:arW	Lcom/google/android/gms/measurement/internal/zzad;
      //   175: invokevirtual 68	com/google/android/gms/measurement/internal/zzad:zzbvf	()Lcom/google/android/gms/measurement/internal/zzw;
      //   178: new 15	com/google/android/gms/measurement/internal/zzad$zza$1
      //   181: dup
      //   182: aload_0
      //   183: aload_1
      //   184: invokespecial 185	com/google/android/gms/measurement/internal/zzad$zza$1:<init>	(Lcom/google/android/gms/measurement/internal/zzad$zza;Lcom/google/android/gms/measurement/internal/zzm;)V
      //   187: invokevirtual 77	com/google/android/gms/measurement/internal/zzw:zzm	(Ljava/lang/Runnable;)V
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
      zzac.zzhq("MeasurementServiceConnection.onServiceDisconnected");
      zzad.this.zzbvg().zzbwi().log("Service disconnected");
      zzad.this.zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzad.zza(zzad.this, paramComponentName);
        }
      });
    }
    
    @WorkerThread
    public void zzac(Intent paramIntent)
    {
      zzad.this.zzyl();
      Context localContext = zzad.this.getContext();
      zzb localzzb = zzb.zzawu();
      try
      {
        if (this.arY)
        {
          zzad.this.zzbvg().zzbwj().log("Connection attempt already in progress");
          return;
        }
        this.arY = true;
        localzzb.zza(localContext, paramIntent, zzad.zza(zzad.this), 129);
        return;
      }
      finally {}
    }
    
    @WorkerThread
    public void zzbye()
    {
      zzad.this.zzyl();
      Context localContext1 = zzad.this.getContext();
      try
      {
        if (this.arY)
        {
          zzad.this.zzbvg().zzbwj().log("Connection attempt already in progress");
          return;
        }
        if (this.arZ != null)
        {
          zzad.this.zzbvg().zzbwj().log("Already awaiting connection attempt");
          return;
        }
      }
      finally {}
      this.arZ = new zzo(localContext2, Looper.getMainLooper(), this, this);
      zzad.this.zzbvg().zzbwj().log("Connecting to remote service");
      this.arY = true;
      this.arZ.zzatu();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */