package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzu
{
  private final zza atv;
  
  public zzu(zza paramzza)
  {
    zzaa.zzy(paramzza);
    this.atv = paramzza;
  }
  
  public static boolean zzh(Context paramContext, boolean paramBoolean)
  {
    zzaa.zzy(paramContext);
    if (paramBoolean) {}
    for (String str = "com.google.android.gms.measurement.PackageMeasurementReceiver";; str = "com.google.android.gms.measurement.AppMeasurementReceiver") {
      return zzal.zza(paramContext, str, false);
    }
  }
  
  @MainThread
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    final zzx localzzx = zzx.zzdq(paramContext);
    final zzq localzzq = localzzx.zzbwb();
    if (paramIntent == null) {
      localzzq.zzbxa().log("Receiver called with null intent");
    }
    do
    {
      return;
      localzzx.zzbwd().zzayi();
      localObject = paramIntent.getAction();
      localzzq.zzbxe().zzj("Local receiver got", localObject);
      if ("com.google.android.gms.measurement.UPLOAD".equals(localObject))
      {
        zzaf.zzi(paramContext, false);
        paramIntent = new Intent().setClassName(paramContext, "com.google.android.gms.measurement.AppMeasurementService");
        paramIntent.setAction("com.google.android.gms.measurement.UPLOAD");
        this.atv.doStartService(paramContext, paramIntent);
        return;
      }
    } while (!"com.android.vending.INSTALL_REFERRER".equals(localObject));
    Object localObject = paramIntent.getStringExtra("referrer");
    if (localObject == null)
    {
      localzzq.zzbxe().log("Install referrer extras are null");
      return;
    }
    localObject = Uri.parse((String)localObject);
    localObject = localzzx.zzbvx().zzu((Uri)localObject);
    if (localObject == null)
    {
      localzzq.zzbxe().log("No campaign defined in install referrer broadcast");
      return;
    }
    final long l = 1000L * paramIntent.getLongExtra("referrer_timestamp_seconds", 0L);
    if (l == 0L) {
      localzzq.zzbxa().log("Install referrer is missing timestamp");
    }
    localzzx.zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzak localzzak = localzzx.zzbvw().zzar(localzzx.zzbvr().zzup(), "_fot");
        if ((localzzak != null) && ((localzzak.zzcyd instanceof Long))) {}
        for (long l1 = ((Long)localzzak.zzcyd).longValue();; l1 = 0L)
        {
          long l2 = l;
          if ((l1 > 0L) && ((l2 >= l1) || (l2 <= 0L))) {}
          for (l1 -= 1L;; l1 = l2)
          {
            if (l1 > 0L) {
              paramContext.putLong("click_timestamp", l1);
            }
            AppMeasurement.getInstance(localzzq).zze("auto", "_cmp", paramContext);
            this.atz.zzbxe().log("Install campaign recorded");
            return;
          }
        }
      }
    });
  }
  
  public static abstract interface zza
  {
    public abstract void doStartService(Context paramContext, Intent paramIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */