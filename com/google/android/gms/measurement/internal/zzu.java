package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzu
{
  static Boolean aqm;
  static Boolean aqn;
  private final zza aqo;
  
  public zzu(zza paramzza)
  {
    zzac.zzy(paramzza);
    this.aqo = paramzza;
  }
  
  public static boolean zzh(Context paramContext, boolean paramBoolean)
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
    for (String str = "com.google.android.gms.measurement.PackageMeasurementReceiver";; str = "com.google.android.gms.measurement.AppMeasurementReceiver")
    {
      bool = zzal.zza(paramContext, str, false);
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
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    final zzx localzzx = zzx.zzdt(paramContext);
    final Object localObject1 = localzzx.zzbvg();
    if (paramIntent == null) {
      ((zzp)localObject1).zzbwe().log("Receiver called with null intent");
    }
    boolean bool2;
    label86:
    label159:
    label164:
    do
    {
      return;
      bool2 = localzzx.zzbvi().zzact();
      localObject2 = paramIntent.getAction();
      boolean bool1;
      if (bool2)
      {
        ((zzp)localObject1).zzbwj().zzj("Device receiver got", localObject2);
        if (!"com.google.android.gms.measurement.UPLOAD".equals(localObject2)) {
          continue;
        }
        if ((!bool2) || (localzzx.zzbxg())) {
          break label159;
        }
        bool1 = true;
        zzae.zzi(paramContext, bool1);
        localObject1 = new Intent();
        if ((!bool2) || (localzzx.zzbxg())) {
          break label164;
        }
      }
      for (paramIntent = "com.google.android.gms.measurement.PackageMeasurementService";; paramIntent = "com.google.android.gms.measurement.AppMeasurementService")
      {
        paramIntent = ((Intent)localObject1).setClassName(paramContext, paramIntent);
        paramIntent.setAction("com.google.android.gms.measurement.UPLOAD");
        this.aqo.doStartService(paramContext, paramIntent);
        return;
        ((zzp)localObject1).zzbwj().zzj("Local receiver got", localObject2);
        break;
        bool1 = false;
        break label86;
      }
    } while ((bool2) || (!"com.android.vending.INSTALL_REFERRER".equals(localObject2)));
    Object localObject2 = paramIntent.getStringExtra("referrer");
    if (localObject2 == null)
    {
      ((zzp)localObject1).zzbwj().log("Install referrer extras are null");
      return;
    }
    localObject2 = Uri.parse((String)localObject2);
    localObject2 = localzzx.zzbvc().zzt((Uri)localObject2);
    if (localObject2 == null)
    {
      ((zzp)localObject1).zzbwj().log("No campaign defined in install referrer broadcast");
      return;
    }
    final long l = 1000L * paramIntent.getLongExtra("referrer_timestamp_seconds", 0L);
    if (l == 0L) {
      ((zzp)localObject1).zzbwe().log("Install referrer is missing timestamp");
    }
    localzzx.zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzak localzzak = localzzx.zzbvb().zzas(localzzx.zzbuy().zzti(), "_fot");
        if ((localzzak != null) && ((localzzak.zzctv instanceof Long))) {}
        for (long l1 = ((Long)localzzak.zzctv).longValue();; l1 = 0L)
        {
          long l2 = l;
          if ((l1 > 0L) && ((l2 >= l1) || (l2 <= 0L))) {}
          for (l1 -= 1L;; l1 = l2)
          {
            if (l1 > 0L) {
              paramContext.putLong("click_timestamp", l1);
            }
            AppMeasurement.getInstance(localObject1).zze("auto", "_cmp", paramContext);
            this.aqs.zzbwj().log("Install campaign recorded");
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