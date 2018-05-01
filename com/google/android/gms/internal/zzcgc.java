package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcgc
{
  private final zzcge zzbrL;
  
  public zzcgc(zzcge paramzzcge)
  {
    zzbo.zzu(paramzzcge);
    this.zzbrL = paramzzcge;
  }
  
  public static boolean zzj(Context paramContext, boolean paramBoolean)
  {
    zzbo.zzu(paramContext);
    return zzcjl.zza(paramContext, "com.google.android.gms.measurement.AppMeasurementReceiver", false);
  }
  
  @MainThread
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    zzcgl localzzcgl = zzcgl.zzbj(paramContext);
    zzcfl localzzcfl = localzzcgl.zzwF();
    if (paramIntent == null) {
      localzzcfl.zzyz().log("Receiver called with null intent");
    }
    do
    {
      return;
      zzcem.zzxE();
      localObject = paramIntent.getAction();
      localzzcfl.zzyD().zzj("Local receiver got", localObject);
      if ("com.google.android.gms.measurement.UPLOAD".equals(localObject))
      {
        zzciw.zzk(paramContext, false);
        paramIntent = new Intent().setClassName(paramContext, "com.google.android.gms.measurement.AppMeasurementService");
        paramIntent.setAction("com.google.android.gms.measurement.UPLOAD");
        this.zzbrL.doStartService(paramContext, paramIntent);
        return;
      }
    } while (!"com.android.vending.INSTALL_REFERRER".equals(localObject));
    String str = paramIntent.getStringExtra("referrer");
    if (str == null)
    {
      localzzcfl.zzyD().log("Install referrer extras are null");
      return;
    }
    localzzcfl.zzyB().zzj("Install referrer extras are", str);
    Object localObject = str;
    if (!str.contains("?"))
    {
      localObject = String.valueOf(str);
      if (((String)localObject).length() == 0) {
        break label218;
      }
    }
    label218:
    for (localObject = "?".concat((String)localObject);; localObject = new String("?"))
    {
      localObject = Uri.parse((String)localObject);
      localObject = localzzcgl.zzwB().zzq((Uri)localObject);
      if (localObject != null) {
        break;
      }
      localzzcfl.zzyD().log("No campaign defined in install referrer broadcast");
      return;
    }
    long l = 1000L * paramIntent.getLongExtra("referrer_timestamp_seconds", 0L);
    if (l == 0L) {
      localzzcfl.zzyz().log("Install referrer is missing timestamp");
    }
    localzzcgl.zzwE().zzj(new zzcgd(this, localzzcgl, l, (Bundle)localObject, paramContext, localzzcfl));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */