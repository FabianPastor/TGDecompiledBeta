package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;

public final class zzae
{
  private static Boolean aqm;
  private static Boolean aqn;
  private final zza asd;
  private final Context mContext;
  private final Handler mHandler;
  
  public zzae(zza paramzza)
  {
    this.mContext = paramzza.getContext();
    zzac.zzy(this.mContext);
    this.asd = paramzza;
    this.mHandler = new Handler();
  }
  
  private zzp zzbvg()
  {
    return zzx.zzdt(this.mContext).zzbvg();
  }
  
  public static boolean zzi(Context paramContext, boolean paramBoolean)
  {
    zzac.zzy(paramContext);
    if ((aqm != null) && (!paramBoolean)) {
      return aqm.booleanValue();
    }
    if ((aqn != null) && (paramBoolean)) {
      return aqn.booleanValue();
    }
    if (paramBoolean) {}
    boolean bool;
    for (String str = "com.google.android.gms.measurement.PackageMeasurementService";; str = "com.google.android.gms.measurement.AppMeasurementService")
    {
      bool = zzal.zzq(paramContext, str);
      if (!paramBoolean) {
        break;
      }
      aqn = Boolean.valueOf(bool);
      return bool;
    }
    aqm = Boolean.valueOf(bool);
    return bool;
  }
  
  @MainThread
  public IBinder onBind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzbvg().zzbwc().log("onBind called with null intent");
      return null;
    }
    paramIntent = paramIntent.getAction();
    if ("com.google.android.gms.measurement.START".equals(paramIntent)) {
      return new zzy(zzx.zzdt(this.mContext));
    }
    zzbvg().zzbwe().zzj("onBind received unknown action", paramIntent);
    return null;
  }
  
  @MainThread
  public void onCreate()
  {
    zzx localzzx = zzx.zzdt(this.mContext);
    zzp localzzp = localzzx.zzbvg();
    if (localzzx.zzbvi().zzact())
    {
      localzzp.zzbwj().log("Device PackageMeasurementService is starting up");
      return;
    }
    localzzp.zzbwj().log("Local AppMeasurementService is starting up");
  }
  
  @MainThread
  public void onDestroy()
  {
    zzx localzzx = zzx.zzdt(this.mContext);
    zzp localzzp = localzzx.zzbvg();
    if (localzzx.zzbvi().zzact())
    {
      localzzp.zzbwj().log("Device PackageMeasurementService is shutting down");
      return;
    }
    localzzp.zzbwj().log("Local AppMeasurementService is shutting down");
  }
  
  @MainThread
  public void onRebind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzbvg().zzbwc().log("onRebind called with null intent");
      return;
    }
    paramIntent = paramIntent.getAction();
    zzbvg().zzbwj().zzj("onRebind called. action", paramIntent);
  }
  
  @MainThread
  public int onStartCommand(Intent paramIntent, int paramInt1, final int paramInt2)
  {
    final zzx localzzx = zzx.zzdt(this.mContext);
    final zzp localzzp = localzzx.zzbvg();
    if (paramIntent == null) {
      localzzp.zzbwe().log("AppMeasurementService started with null intent");
    }
    for (;;)
    {
      return 2;
      paramIntent = paramIntent.getAction();
      if (localzzx.zzbvi().zzact()) {
        localzzp.zzbwj().zze("Device PackageMeasurementService called. startId, action", Integer.valueOf(paramInt2), paramIntent);
      }
      while ("com.google.android.gms.measurement.UPLOAD".equals(paramIntent))
      {
        localzzx.zzbvf().zzm(new Runnable()
        {
          public void run()
          {
            localzzx.zzbxp();
            localzzx.zzbxk();
            zzae.zzb(zzae.this).post(new Runnable()
            {
              public void run()
              {
                if (zzae.zza(zzae.this).callServiceStopSelfResult(zzae.1.this.zzcyr))
                {
                  if (zzae.1.this.aqp.zzbvi().zzact()) {
                    zzae.1.this.aqs.zzbwj().log("Device PackageMeasurementService processed last upload request");
                  }
                }
                else {
                  return;
                }
                zzae.1.this.aqs.zzbwj().log("Local AppMeasurementService processed last upload request");
              }
            });
          }
        });
        return 2;
        localzzp.zzbwj().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(paramInt2), paramIntent);
      }
    }
  }
  
  @MainThread
  public boolean onUnbind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzbvg().zzbwc().log("onUnbind called with null intent");
      return true;
    }
    paramIntent = paramIntent.getAction();
    zzbvg().zzbwj().zzj("onUnbind called for intent. action", paramIntent);
    return true;
  }
  
  public static abstract interface zza
  {
    public abstract boolean callServiceStopSelfResult(int paramInt);
    
    public abstract Context getContext();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */