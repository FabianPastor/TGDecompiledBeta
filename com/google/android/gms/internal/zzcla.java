package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PersistableBundle;
import com.google.android.gms.common.internal.zzbq;

public final class zzcla<T extends Context,  extends zzcle>
{
  private final T zzdyu;
  
  public zzcla(T paramT)
  {
    zzbq.checkNotNull(paramT);
    this.zzdyu = paramT;
  }
  
  private final zzchm zzawy()
  {
    return zzcim.zzdx(this.zzdyu).zzawy();
  }
  
  private final void zzk(Runnable paramRunnable)
  {
    zzcim localzzcim = zzcim.zzdx(this.zzdyu);
    localzzcim.zzawy();
    localzzcim.zzawx().zzg(new zzcld(this, localzzcim, paramRunnable));
  }
  
  public static boolean zzk(Context paramContext, boolean paramBoolean)
  {
    zzbq.checkNotNull(paramContext);
    if (Build.VERSION.SDK_INT >= 24) {
      return zzclq.zzt(paramContext, "com.google.android.gms.measurement.AppMeasurementJobService");
    }
    return zzclq.zzt(paramContext, "com.google.android.gms.measurement.AppMeasurementService");
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzawy().zzazd().log("onBind called with null intent");
      return null;
    }
    paramIntent = paramIntent.getAction();
    if ("com.google.android.gms.measurement.START".equals(paramIntent)) {
      return new zzcir(zzcim.zzdx(this.zzdyu));
    }
    zzawy().zzazf().zzj("onBind received unknown action", paramIntent);
    return null;
  }
  
  public final void onCreate()
  {
    zzcim.zzdx(this.zzdyu).zzawy().zzazj().log("Local AppMeasurementService is starting up");
  }
  
  public final void onDestroy()
  {
    zzcim.zzdx(this.zzdyu).zzawy().zzazj().log("Local AppMeasurementService is shutting down");
  }
  
  public final void onRebind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzawy().zzazd().log("onRebind called with null intent");
      return;
    }
    paramIntent = paramIntent.getAction();
    zzawy().zzazj().zzj("onRebind called. action", paramIntent);
  }
  
  public final int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    zzchm localzzchm = zzcim.zzdx(this.zzdyu).zzawy();
    if (paramIntent == null) {
      localzzchm.zzazf().log("AppMeasurementService started with null intent");
    }
    String str;
    do
    {
      return 2;
      str = paramIntent.getAction();
      localzzchm.zzazj().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(paramInt2), str);
    } while (!"com.google.android.gms.measurement.UPLOAD".equals(str));
    zzk(new zzclb(this, paramInt2, localzzchm, paramIntent));
    return 2;
  }
  
  @TargetApi(24)
  public final boolean onStartJob(JobParameters paramJobParameters)
  {
    zzchm localzzchm = zzcim.zzdx(this.zzdyu).zzawy();
    String str = paramJobParameters.getExtras().getString("action");
    localzzchm.zzazj().zzj("Local AppMeasurementJobService called. action", str);
    if ("com.google.android.gms.measurement.UPLOAD".equals(str)) {
      zzk(new zzclc(this, localzzchm, paramJobParameters));
    }
    return true;
  }
  
  public final boolean onUnbind(Intent paramIntent)
  {
    if (paramIntent == null)
    {
      zzawy().zzazd().log("onUnbind called with null intent");
      return true;
    }
    paramIntent = paramIntent.getAction();
    zzawy().zzazj().zzj("onUnbind called for intent. action", paramIntent);
    return true;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcla.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */