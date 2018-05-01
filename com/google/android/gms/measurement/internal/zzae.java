package com.google.android.gms.measurement.internal;

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
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zze.zzb;
import com.google.android.gms.common.internal.zze.zzc;
import com.google.android.gms.common.stats.zza;
import com.google.android.gms.common.zzc;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class zzae
  extends zzaa
{
  private final zza avs;
  private zzm avt;
  private Boolean avu;
  private final zzf avv;
  private final zzah avw;
  private final List<Runnable> avx = new ArrayList();
  private final zzf avy;
  
  protected zzae(zzx paramzzx)
  {
    super(paramzzx);
    this.avw = new zzah(paramzzx.zzabz());
    this.avs = new zza();
    this.avv = new zzf(paramzzx)
    {
      public void run()
      {
        zzae.zzb(zzae.this);
      }
    };
    this.avy = new zzf(paramzzx)
    {
      public void run()
      {
        zzae.this.zzbwb().zzbxa().log("Tasks have been queued for a long time");
      }
    };
  }
  
  @WorkerThread
  private void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzzx();
    if (this.avt != null)
    {
      this.avt = null;
      zzbwb().zzbxe().zzj("Disconnected from device MeasurementService", paramComponentName);
      zzbyy();
    }
  }
  
  @WorkerThread
  private void zza(zzm paramzzm)
  {
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzy(paramzzm);
    this.avt = paramzzm;
    zzacw();
    zzbyz();
  }
  
  @WorkerThread
  private void zzacw()
  {
    zzzx();
    this.avw.start();
    zzx localzzx = this.aqw;
    this.avv.zzx(zzbwd().zzaez());
  }
  
  @WorkerThread
  private void zzacx()
  {
    zzzx();
    if (!isConnected()) {
      return;
    }
    zzbwb().zzbxe().log("Inactivity, disconnecting from the service");
    disconnect();
  }
  
  private boolean zzbyw()
  {
    zzbwd().zzayi();
    List localList = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), 65536);
    return (localList != null) && (localList.size() > 0);
  }
  
  @WorkerThread
  private void zzbyy()
  {
    zzzx();
    zzadl();
  }
  
  @WorkerThread
  private void zzbyz()
  {
    zzzx();
    zzbwb().zzbxe().zzj("Processing queued up service tasks", Integer.valueOf(this.avx.size()));
    Iterator localIterator = this.avx.iterator();
    while (localIterator.hasNext())
    {
      Runnable localRunnable = (Runnable)localIterator.next();
      zzbwa().zzm(localRunnable);
    }
    this.avx.clear();
    this.avy.cancel();
  }
  
  @WorkerThread
  private void zzo(Runnable paramRunnable)
    throws IllegalStateException
  {
    zzzx();
    if (isConnected())
    {
      paramRunnable.run();
      return;
    }
    if (this.avx.size() >= zzbwd().zzbuy())
    {
      zzbwb().zzbwy().log("Discarding data. Max runnable queue size reached");
      return;
    }
    this.avx.add(paramRunnable);
    paramRunnable = this.aqw;
    this.avy.zzx(60000L);
    zzadl();
  }
  
  @WorkerThread
  public void disconnect()
  {
    zzzx();
    zzacj();
    try
    {
      zza.zzaxr().zza(getContext(), this.avs);
      this.avt = null;
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
    zzzx();
    zzacj();
    return this.avt != null;
  }
  
  @WorkerThread
  protected void zza(final AppMeasurement.zzf paramzzf)
  {
    zzzx();
    zzacj();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzae.zzc(zzae.this);
        if (localzzm == null)
        {
          zzae.this.zzbwb().zzbwy().log("Failed to send current screen to service");
          return;
        }
        for (;;)
        {
          try
          {
            if (paramzzf == null)
            {
              localzzm.zza(0L, null, null, zzae.this.getContext().getPackageName());
              zzae.zzd(zzae.this);
              return;
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzae.this.zzbwb().zzbwy().zzj("Failed to send current screen to the service", localRemoteException);
            return;
          }
          localRemoteException.zza(paramzzf.aqB, paramzzf.aqz, paramzzf.aqA, zzae.this.getContext().getPackageName());
        }
      }
    });
  }
  
  @WorkerThread
  void zza(zzm paramzzm, AbstractSafeParcelable paramAbstractSafeParcelable)
  {
    zzzx();
    zzaby();
    zzacj();
    int j;
    ArrayList localArrayList;
    int k;
    label56:
    Object localObject;
    if ((Build.VERSION.SDK_INT >= 11) && (!zzbwd().zzayi()))
    {
      j = 1;
      localArrayList = new ArrayList();
      zzbwd().zzbvb();
      k = 0;
      i = 100;
      if ((k >= 1001) || (i != 100)) {
        break label302;
      }
      if (j == 0) {
        break label303;
      }
      localObject = zzbvv().zzxe(100);
      if (localObject == null) {
        break label303;
      }
      localArrayList.addAll((Collection)localObject);
    }
    label293:
    label302:
    label303:
    for (int i = ((List)localObject).size();; i = 0)
    {
      if ((paramAbstractSafeParcelable != null) && (i < 100)) {
        localArrayList.add(paramAbstractSafeParcelable);
      }
      localObject = localArrayList.iterator();
      for (;;)
      {
        if (!((Iterator)localObject).hasNext()) {
          break label293;
        }
        AbstractSafeParcelable localAbstractSafeParcelable = (AbstractSafeParcelable)((Iterator)localObject).next();
        if ((localAbstractSafeParcelable instanceof EventParcel))
        {
          try
          {
            paramzzm.zza((EventParcel)localAbstractSafeParcelable, zzbvr().zzmi(zzbwb().zzbxf()));
          }
          catch (RemoteException localRemoteException1)
          {
            zzbwb().zzbwy().zzj("Failed to send event to the service", localRemoteException1);
          }
          continue;
          j = 0;
          break;
        }
        if ((localRemoteException1 instanceof UserAttributeParcel)) {
          try
          {
            paramzzm.zza((UserAttributeParcel)localRemoteException1, zzbvr().zzmi(zzbwb().zzbxf()));
          }
          catch (RemoteException localRemoteException2)
          {
            zzbwb().zzbwy().zzj("Failed to send attribute to the service", localRemoteException2);
          }
        } else {
          zzbwb().zzbwy().log("Discarding data. Unrecognized parcel type.");
        }
      }
      k += 1;
      break label56;
      return;
    }
  }
  
  @WorkerThread
  protected void zza(final AtomicReference<List<UserAttributeParcel>> paramAtomicReference, final boolean paramBoolean)
  {
    zzzx();
    zzacj();
    zzo(new Runnable()
    {
      public void run()
      {
        localAtomicReference = paramAtomicReference;
        for (;;)
        {
          try
          {
            localzzm = zzae.zzc(zzae.this);
            if (localzzm == null) {
              zzae.this.zzbwb().zzbwy().log("Failed to get user properties");
            }
          }
          catch (RemoteException localRemoteException)
          {
            zzm localzzm;
            zzae.this.zzbwb().zzbwy().zzj("Failed to get user properties", localRemoteException);
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
        paramAtomicReference.set(localzzm.zza(zzae.this.zzbvr().zzmi(null), paramBoolean));
        zzae.zzd(zzae.this);
        paramAtomicReference.notify();
      }
    });
  }
  
  @WorkerThread
  void zzadl()
  {
    zzzx();
    zzacj();
    if (isConnected()) {
      return;
    }
    if (this.avu == null)
    {
      this.avu = zzbwc().zzbxl();
      if (this.avu == null)
      {
        zzbwb().zzbxe().log("State of service unknown");
        this.avu = Boolean.valueOf(zzbyx());
        zzbwc().zzcg(this.avu.booleanValue());
      }
    }
    if (this.avu.booleanValue())
    {
      zzbwb().zzbxe().log("Using measurement service");
      this.avs.zzbza();
      return;
    }
    Object localObject = this.aqw;
    if (zzbyw())
    {
      zzbwb().zzbxe().log("Using local app measurement service");
      localObject = new Intent("com.google.android.gms.measurement.START");
      Context localContext = getContext();
      zzbwd().zzayi();
      ((Intent)localObject).setComponent(new ComponentName(localContext, "com.google.android.gms.measurement.AppMeasurementService"));
      this.avs.zzac((Intent)localObject);
      return;
    }
    zzbwb().zzbwy().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
  }
  
  @WorkerThread
  protected void zzb(final UserAttributeParcel paramUserAttributeParcel)
  {
    final boolean bool = true;
    zzzx();
    zzacj();
    int i;
    if ((Build.VERSION.SDK_INT >= 11) && (!zzbwd().zzayi()))
    {
      i = 1;
      if ((i == 0) || (!zzbvv().zza(paramUserAttributeParcel))) {
        break label65;
      }
    }
    for (;;)
    {
      zzo(new Runnable()
      {
        public void run()
        {
          zzm localzzm = zzae.zzc(zzae.this);
          if (localzzm == null)
          {
            zzae.this.zzbwb().zzbwy().log("Discarding data. Failed to set user attribute");
            return;
          }
          zzae localzzae = zzae.this;
          if (bool) {}
          for (Object localObject = null;; localObject = paramUserAttributeParcel)
          {
            localzzae.zza(localzzm, (AbstractSafeParcelable)localObject);
            zzae.zzd(zzae.this);
            return;
          }
        }
      });
      return;
      i = 0;
      break;
      label65:
      bool = false;
    }
  }
  
  @WorkerThread
  protected void zzbyq()
  {
    zzzx();
    zzacj();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzae.zzc(zzae.this);
        if (localzzm == null)
        {
          zzae.this.zzbwb().zzbwy().log("Discarding data. Failed to send app launch");
          return;
        }
        try
        {
          zzae.this.zza(localzzm, null);
          localzzm.zza(zzae.this.zzbvr().zzmi(zzae.this.zzbwb().zzbxf()));
          zzae.zzd(zzae.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzae.this.zzbwb().zzbwy().zzj("Failed to send app launch to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected void zzbyv()
  {
    zzzx();
    zzacj();
    zzo(new Runnable()
    {
      public void run()
      {
        zzm localzzm = zzae.zzc(zzae.this);
        if (localzzm == null)
        {
          zzae.this.zzbwb().zzbwy().log("Failed to send measurementEnabled to service");
          return;
        }
        try
        {
          localzzm.zzb(zzae.this.zzbvr().zzmi(zzae.this.zzbwb().zzbxf()));
          zzae.zzd(zzae.this);
          return;
        }
        catch (RemoteException localRemoteException)
        {
          zzae.this.zzbwb().zzbwy().zzj("Failed to send measurementEnabled to the service", localRemoteException);
        }
      }
    });
  }
  
  @WorkerThread
  protected boolean zzbyx()
  {
    zzzx();
    zzacj();
    zzbwd().zzayi();
    zzbwb().zzbxe().log("Checking service availability");
    switch (zzc.zzaql().isGooglePlayServicesAvailable(getContext()))
    {
    default: 
      return false;
    case 0: 
      zzbwb().zzbxe().log("Service available");
      return true;
    case 1: 
      zzbwb().zzbxe().log("Service missing");
      return false;
    case 18: 
      zzbwb().zzbxa().log("Service updating");
      return true;
    case 2: 
      zzbwb().zzbxd().log("Service container out of date");
      return true;
    case 3: 
      zzbwb().zzbxa().log("Service disabled");
      return false;
    }
    zzbwb().zzbxa().log("Service invalid");
    return false;
  }
  
  @WorkerThread
  protected void zzc(final EventParcel paramEventParcel, final String paramString)
  {
    final boolean bool2 = true;
    com.google.android.gms.common.internal.zzaa.zzy(paramEventParcel);
    zzzx();
    zzacj();
    final boolean bool1;
    if ((Build.VERSION.SDK_INT >= 11) && (!zzbwd().zzayi()))
    {
      bool1 = true;
      if ((!bool1) || (!zzbvv().zza(paramEventParcel))) {
        break label74;
      }
    }
    for (;;)
    {
      zzo(new Runnable()
      {
        public void run()
        {
          zzm localzzm = zzae.zzc(zzae.this);
          if (localzzm == null)
          {
            zzae.this.zzbwb().zzbwy().log("Discarding data. Failed to send event to service");
            return;
          }
          Object localObject;
          if (bool1)
          {
            zzae localzzae = zzae.this;
            if (bool2)
            {
              localObject = null;
              localzzae.zza(localzzm, (AbstractSafeParcelable)localObject);
            }
          }
          for (;;)
          {
            zzae.zzd(zzae.this);
            return;
            localObject = paramEventParcel;
            break;
            try
            {
              if (!TextUtils.isEmpty(paramString)) {
                break label134;
              }
              localzzm.zza(paramEventParcel, zzae.this.zzbvr().zzmi(zzae.this.zzbwb().zzbxf()));
            }
            catch (RemoteException localRemoteException)
            {
              zzae.this.zzbwb().zzbwy().zzj("Failed to send event to the service", localRemoteException);
            }
            continue;
            label134:
            localzzm.zza(paramEventParcel, paramString, zzae.this.zzbwb().zzbxf());
          }
        }
      });
      return;
      bool1 = false;
      break;
      label74:
      bool2 = false;
    }
  }
  
  protected void zzzy() {}
  
  protected class zza
    implements ServiceConnection, zze.zzb, zze.zzc
  {
    private volatile boolean avE;
    private volatile zzp avF;
    
    protected zza() {}
    
    /* Error */
    @MainThread
    public void onConnected(@android.support.annotation.Nullable final android.os.Bundle paramBundle)
    {
      // Byte code:
      //   0: ldc 48
      //   2: invokestatic 54	com/google/android/gms/common/internal/zzaa:zzhs	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 56	com/google/android/gms/measurement/internal/zzae$zza:avF	Lcom/google/android/gms/measurement/internal/zzp;
      //   11: invokevirtual 62	com/google/android/gms/measurement/internal/zzp:zzavg	()Landroid/os/IInterface;
      //   14: checkcast 64	com/google/android/gms/measurement/internal/zzm
      //   17: astore_1
      //   18: aload_0
      //   19: aconst_null
      //   20: putfield 56	com/google/android/gms/measurement/internal/zzae$zza:avF	Lcom/google/android/gms/measurement/internal/zzp;
      //   23: aload_0
      //   24: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   27: invokevirtual 68	com/google/android/gms/measurement/internal/zzae:zzbwa	()Lcom/google/android/gms/measurement/internal/zzw;
      //   30: new 19	com/google/android/gms/measurement/internal/zzae$zza$3
      //   33: dup
      //   34: aload_0
      //   35: aload_1
      //   36: invokespecial 71	com/google/android/gms/measurement/internal/zzae$zza$3:<init>	(Lcom/google/android/gms/measurement/internal/zzae$zza;Lcom/google/android/gms/measurement/internal/zzm;)V
      //   39: invokevirtual 77	com/google/android/gms/measurement/internal/zzw:zzm	(Ljava/lang/Runnable;)V
      //   42: aload_0
      //   43: monitorexit
      //   44: return
      //   45: aload_0
      //   46: aconst_null
      //   47: putfield 56	com/google/android/gms/measurement/internal/zzae$zza:avF	Lcom/google/android/gms/measurement/internal/zzp;
      //   50: aload_0
      //   51: iconst_0
      //   52: putfield 38	com/google/android/gms/measurement/internal/zzae$zza:avE	Z
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
      com.google.android.gms.common.internal.zzaa.zzhs("MeasurementServiceConnection.onConnectionFailed");
      zzq localzzq = zzae.this.aqw.zzbxr();
      if (localzzq != null) {
        localzzq.zzbxa().zzj("Service connection failed", paramConnectionResult);
      }
      try
      {
        this.avE = false;
        this.avF = null;
        return;
      }
      finally {}
    }
    
    @MainThread
    public void onConnectionSuspended(int paramInt)
    {
      com.google.android.gms.common.internal.zzaa.zzhs("MeasurementServiceConnection.onConnectionSuspended");
      zzae.this.zzbwb().zzbxd().log("Service connection suspended");
      zzae.this.zzbwa().zzm(new Runnable()
      {
        public void run()
        {
          zzae localzzae = zzae.this;
          Context localContext = zzae.this.getContext();
          zzae.this.zzbwd().zzayi();
          zzae.zza(localzzae, new ComponentName(localContext, "com.google.android.gms.measurement.AppMeasurementService"));
        }
      });
    }
    
    /* Error */
    @MainThread
    public void onServiceConnected(final ComponentName paramComponentName, android.os.IBinder paramIBinder)
    {
      // Byte code:
      //   0: ldc -122
      //   2: invokestatic 54	com/google/android/gms/common/internal/zzaa:zzhs	(Ljava/lang/String;)V
      //   5: aload_0
      //   6: monitorenter
      //   7: aload_2
      //   8: ifnonnull +26 -> 34
      //   11: aload_0
      //   12: iconst_0
      //   13: putfield 38	com/google/android/gms/measurement/internal/zzae$zza:avE	Z
      //   16: aload_0
      //   17: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   20: invokevirtual 115	com/google/android/gms/measurement/internal/zzae:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
      //   23: invokevirtual 137	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
      //   26: ldc -117
      //   28: invokevirtual 123	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
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
      //   67: invokestatic 159	com/google/android/gms/measurement/internal/zzm$zza:zzjp	(Landroid/os/IBinder;)Lcom/google/android/gms/measurement/internal/zzm;
      //   70: astore_2
      //   71: aload_2
      //   72: astore_1
      //   73: aload_0
      //   74: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   77: invokevirtual 115	com/google/android/gms/measurement/internal/zzae:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
      //   80: invokevirtual 162	com/google/android/gms/measurement/internal/zzq:zzbxe	()Lcom/google/android/gms/measurement/internal/zzq$zza;
      //   83: ldc -92
      //   85: invokevirtual 123	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
      //   88: aload_2
      //   89: astore_1
      //   90: aload_1
      //   91: ifnonnull +80 -> 171
      //   94: aload_0
      //   95: iconst_0
      //   96: putfield 38	com/google/android/gms/measurement/internal/zzae$zza:avE	Z
      //   99: invokestatic 170	com/google/android/gms/common/stats/zza:zzaxr	()Lcom/google/android/gms/common/stats/zza;
      //   102: aload_0
      //   103: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   106: invokevirtual 174	com/google/android/gms/measurement/internal/zzae:getContext	()Landroid/content/Context;
      //   109: aload_0
      //   110: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   113: invokestatic 177	com/google/android/gms/measurement/internal/zzae:zza	(Lcom/google/android/gms/measurement/internal/zzae;)Lcom/google/android/gms/measurement/internal/zzae$zza;
      //   116: invokevirtual 180	com/google/android/gms/common/stats/zza:zza	(Landroid/content/Context;Landroid/content/ServiceConnection;)V
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
      //   131: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   134: invokevirtual 115	com/google/android/gms/measurement/internal/zzae:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
      //   137: invokevirtual 137	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
      //   140: ldc -74
      //   142: aload 5
      //   144: invokevirtual 108	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
      //   147: aload_3
      //   148: astore_1
      //   149: goto -59 -> 90
      //   152: astore_2
      //   153: aload_0
      //   154: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   157: invokevirtual 115	com/google/android/gms/measurement/internal/zzae:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
      //   160: invokevirtual 137	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
      //   163: ldc -72
      //   165: invokevirtual 123	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
      //   168: goto -78 -> 90
      //   171: aload_0
      //   172: getfield 31	com/google/android/gms/measurement/internal/zzae$zza:avz	Lcom/google/android/gms/measurement/internal/zzae;
      //   175: invokevirtual 68	com/google/android/gms/measurement/internal/zzae:zzbwa	()Lcom/google/android/gms/measurement/internal/zzw;
      //   178: new 15	com/google/android/gms/measurement/internal/zzae$zza$1
      //   181: dup
      //   182: aload_0
      //   183: aload_1
      //   184: invokespecial 185	com/google/android/gms/measurement/internal/zzae$zza$1:<init>	(Lcom/google/android/gms/measurement/internal/zzae$zza;Lcom/google/android/gms/measurement/internal/zzm;)V
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
      com.google.android.gms.common.internal.zzaa.zzhs("MeasurementServiceConnection.onServiceDisconnected");
      zzae.this.zzbwb().zzbxd().log("Service disconnected");
      zzae.this.zzbwa().zzm(new Runnable()
      {
        public void run()
        {
          zzae.zza(zzae.this, paramComponentName);
        }
      });
    }
    
    @WorkerThread
    public void zzac(Intent paramIntent)
    {
      zzae.this.zzzx();
      Context localContext = zzae.this.getContext();
      zza localzza = zza.zzaxr();
      try
      {
        if (this.avE)
        {
          zzae.this.zzbwb().zzbxe().log("Connection attempt already in progress");
          return;
        }
        this.avE = true;
        localzza.zza(localContext, paramIntent, zzae.zza(zzae.this), 129);
        return;
      }
      finally {}
    }
    
    @WorkerThread
    public void zzbza()
    {
      zzae.this.zzzx();
      Context localContext1 = zzae.this.getContext();
      try
      {
        if (this.avE)
        {
          zzae.this.zzbwb().zzbxe().log("Connection attempt already in progress");
          return;
        }
        if (this.avF != null)
        {
          zzae.this.zzbwb().zzbxe().log("Already awaiting connection attempt");
          return;
        }
      }
      finally {}
      this.avF = new zzp(localContext2, Looper.getMainLooper(), this, this);
      zzae.this.zzbwb().zzbxe().log("Connecting to remote service");
      this.avE = true;
      this.avF.zzavd();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */