package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzaub
{
  private final zza zzbtA;
  
  public zzaub(zza paramzza)
  {
    zzac.zzw(paramzza);
    this.zzbtA = paramzza;
  }
  
  public static boolean zzi(Context paramContext, boolean paramBoolean)
  {
    zzac.zzw(paramContext);
    if (paramBoolean) {}
    for (String str = "com.google.android.gms.measurement.PackageMeasurementReceiver";; str = "com.google.android.gms.measurement.AppMeasurementReceiver") {
      return zzaut.zza(paramContext, str, false);
    }
  }
  
  @MainThread
  public void onReceive(final Context paramContext, Intent paramIntent)
  {
    final zzaue localzzaue = zzaue.zzbM(paramContext);
    final zzatx localzzatx = localzzaue.zzKl();
    if (paramIntent == null) {
      localzzatx.zzMb().log("Receiver called with null intent");
    }
    do
    {
      return;
      localzzaue.zzKn().zzLh();
      localObject = paramIntent.getAction();
      localzzatx.zzMf().zzj("Local receiver got", localObject);
      if ("com.google.android.gms.measurement.UPLOAD".equals(localObject))
      {
        zzaum.zzj(paramContext, false);
        paramIntent = new Intent().setClassName(paramContext, "com.google.android.gms.measurement.AppMeasurementService");
        paramIntent.setAction("com.google.android.gms.measurement.UPLOAD");
        this.zzbtA.doStartService(paramContext, paramIntent);
        return;
      }
    } while (!"com.android.vending.INSTALL_REFERRER".equals(localObject));
    String str = paramIntent.getStringExtra("referrer");
    if (str == null)
    {
      localzzatx.zzMf().log("Install referrer extras are null");
      return;
    }
    localzzatx.zzMd().zzj("Install referrer extras are", str);
    Object localObject = str;
    if (!str.contains("?"))
    {
      localObject = String.valueOf(str);
      if (((String)localObject).length() == 0) {
        break label223;
      }
    }
    label223:
    for (localObject = "?".concat((String)localObject);; localObject = new String("?"))
    {
      localObject = Uri.parse((String)localObject);
      localObject = localzzaue.zzKh().zzu((Uri)localObject);
      if (localObject != null) {
        break;
      }
      localzzatx.zzMf().log("No campaign defined in install referrer broadcast");
      return;
    }
    final long l = 1000L * paramIntent.getLongExtra("referrer_timestamp_seconds", 0L);
    if (l == 0L) {
      localzzatx.zzMb().log("Install referrer is missing timestamp");
    }
    localzzaue.zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzaus localzzaus = localzzaue.zzKg().zzS(localzzaue.zzKb().zzke(), "_fot");
        if ((localzzaus != null) && ((localzzaus.mValue instanceof Long))) {}
        for (long l1 = ((Long)localzzaus.mValue).longValue();; l1 = 0L)
        {
          long l2 = l;
          if ((l1 > 0L) && ((l2 >= l1) || (l2 <= 0L))) {}
          for (l1 -= 1L;; l1 = l2)
          {
            if (l1 > 0L) {
              paramContext.putLong("click_timestamp", l1);
            }
            AppMeasurement.getInstance(localzzatx).logEventInternal("auto", "_cmp", paramContext);
            this.zzbtE.zzMf().log("Install campaign recorded");
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */