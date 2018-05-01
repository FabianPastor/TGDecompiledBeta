package com.google.android.gms.internal;

import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.common.internal.zzbq;

public final class zzcid
{
  private final zzcif zzjds;
  
  public zzcid(zzcif paramzzcif)
  {
    zzbq.checkNotNull(paramzzcif);
    this.zzjds = paramzzcif;
  }
  
  public static boolean zzbk(Context paramContext)
  {
    zzbq.checkNotNull(paramContext);
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return false;
      }
      paramContext = localPackageManager.getReceiverInfo(new ComponentName(paramContext, "com.google.android.gms.measurement.AppMeasurementReceiver"), 2);
      if (paramContext != null)
      {
        boolean bool = paramContext.enabled;
        if (bool) {
          return true;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    zzcim localzzcim = zzcim.zzdx(paramContext);
    zzchm localzzchm = localzzcim.zzawy();
    if (paramIntent == null) {
      localzzchm.zzazf().log("Receiver called with null intent");
    }
    BroadcastReceiver.PendingResult localPendingResult;
    String str;
    do
    {
      do
      {
        return;
        localObject = paramIntent.getAction();
        localzzchm.zzazj().zzj("Local receiver got", localObject);
        if ("com.google.android.gms.measurement.UPLOAD".equals(localObject))
        {
          paramIntent = new Intent().setClassName(paramContext, "com.google.android.gms.measurement.AppMeasurementService");
          paramIntent.setAction("com.google.android.gms.measurement.UPLOAD");
          localzzchm.zzazj().log("Starting wakeful intent.");
          this.zzjds.doStartService(paramContext, paramIntent);
          return;
        }
      } while (!"com.android.vending.INSTALL_REFERRER".equals(localObject));
      localPendingResult = this.zzjds.doGoAsync();
      str = paramIntent.getStringExtra("referrer");
      if (str != null) {
        break;
      }
      localzzchm.zzazj().log("Install referrer extras are null");
    } while (localPendingResult == null);
    localPendingResult.finish();
    return;
    localzzchm.zzazh().zzj("Install referrer extras are", str);
    Object localObject = str;
    if (!str.contains("?"))
    {
      localObject = String.valueOf(str);
      if (((String)localObject).length() == 0) {
        break label249;
      }
    }
    label249:
    for (localObject = "?".concat((String)localObject);; localObject = new String("?"))
    {
      localObject = Uri.parse((String)localObject);
      localObject = localzzcim.zzawu().zzp((Uri)localObject);
      if (localObject != null) {
        break label263;
      }
      localzzchm.zzazj().log("No campaign defined in install referrer broadcast");
      if (localPendingResult == null) {
        break;
      }
      localPendingResult.finish();
      return;
    }
    label263:
    long l = 1000L * paramIntent.getLongExtra("referrer_timestamp_seconds", 0L);
    if (l == 0L) {
      localzzchm.zzazf().log("Install referrer is missing timestamp");
    }
    localzzcim.zzawx().zzg(new zzcie(this, localzzcim, l, (Bundle)localObject, paramContext, localzzchm, localPendingResult));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcid.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */