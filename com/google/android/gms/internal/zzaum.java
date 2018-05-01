package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;

public final class zzaum
{
  private final Context mContext;
  private final Handler mHandler;
  private final zza zzbvW;
  
  public zzaum(zza paramzza)
  {
    this.mContext = paramzza.getContext();
    zzac.zzw(this.mContext);
    this.zzbvW = paramzza;
    this.mHandler = new Handler();
  }
  
  private zzatx zzKl()
  {
    return zzaue.zzbM(this.mContext).zzKl();
  }
  
  public static boolean zzj(Context paramContext, boolean paramBoolean)
  {
    zzac.zzw(paramContext);
    if (paramBoolean) {}
    for (String str = "com.google.android.gms.measurement.PackageMeasurementService";; str = "com.google.android.gms.measurement.AppMeasurementService") {
      return zzaut.zzy(paramContext, str);
    }
  }
  
  @MainThread
  public IBinder onBind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzKl().zzLZ().log("onBind called with null intent");
      return null;
    }
    paramIntent = paramIntent.getAction();
    if ("com.google.android.gms.measurement.START".equals(paramIntent)) {
      return new zzauf(zzaue.zzbM(this.mContext));
    }
    zzKl().zzMb().zzj("onBind received unknown action", paramIntent);
    return null;
  }
  
  @MainThread
  public void onCreate()
  {
    zzaue localzzaue = zzaue.zzbM(this.mContext);
    zzatx localzzatx = localzzaue.zzKl();
    localzzaue.zzKn().zzLh();
    localzzatx.zzMf().log("Local AppMeasurementService is starting up");
  }
  
  @MainThread
  public void onDestroy()
  {
    zzaue localzzaue = zzaue.zzbM(this.mContext);
    zzatx localzzatx = localzzaue.zzKl();
    localzzaue.zzKn().zzLh();
    localzzatx.zzMf().log("Local AppMeasurementService is shutting down");
  }
  
  @MainThread
  public void onRebind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzKl().zzLZ().log("onRebind called with null intent");
      return;
    }
    paramIntent = paramIntent.getAction();
    zzKl().zzMf().zzj("onRebind called. action", paramIntent);
  }
  
  @MainThread
  public int onStartCommand(Intent paramIntent, int paramInt1, final int paramInt2)
  {
    final zzaue localzzaue = zzaue.zzbM(this.mContext);
    final zzatx localzzatx = localzzaue.zzKl();
    if (paramIntent == null) {
      localzzatx.zzMb().log("AppMeasurementService started with null intent");
    }
    do
    {
      return 2;
      paramIntent = paramIntent.getAction();
      localzzaue.zzKn().zzLh();
      localzzatx.zzMf().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(paramInt2), paramIntent);
    } while (!"com.google.android.gms.measurement.UPLOAD".equals(paramIntent));
    localzzaue.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        localzzaue.zzMN();
        localzzaue.zzMI();
        zzaum.zzb(zzaum.this).post(new Runnable()
        {
          public void run()
          {
            if (zzaum.zza(zzaum.this).callServiceStopSelfResult(zzaum.1.this.zzabA))
            {
              zzaum.1.this.zzbtB.zzKn().zzLh();
              zzaum.1.this.zzbtE.zzMf().log("Local AppMeasurementService processed last upload request");
            }
          }
        });
      }
    });
    return 2;
  }
  
  @MainThread
  public boolean onUnbind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzKl().zzLZ().log("onUnbind called with null intent");
      return true;
    }
    paramIntent = paramIntent.getAction();
    zzKl().zzMf().zzj("onUnbind called for intent. action", paramIntent);
    return true;
  }
  
  public static abstract interface zza
  {
    public abstract boolean callServiceStopSelfResult(int paramInt);
    
    public abstract Context getContext();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaum.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */