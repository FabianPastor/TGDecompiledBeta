package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import java.util.Iterator;
import java.util.List;

public class GcmNetworkManager
{
  public static final int RESULT_FAILURE = 2;
  public static final int RESULT_RESCHEDULE = 1;
  public static final int RESULT_SUCCESS = 0;
  private static GcmNetworkManager aeF;
  private Context mContext;
  private final PendingIntent mPendingIntent;
  
  private GcmNetworkManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(), 0);
  }
  
  public static GcmNetworkManager getInstance(Context paramContext)
  {
    try
    {
      if (aeF == null) {
        aeF = new GcmNetworkManager(paramContext.getApplicationContext());
      }
      paramContext = aeF;
      return paramContext;
    }
    finally {}
  }
  
  private void zza(ComponentName paramComponentName)
  {
    zzkj(paramComponentName.getClassName());
    Intent localIntent = zzbny();
    if (localIntent == null) {
      return;
    }
    localIntent.putExtra("scheduler_action", "CANCEL_ALL");
    localIntent.putExtra("component", paramComponentName);
    this.mContext.sendBroadcast(localIntent);
  }
  
  private void zza(String paramString, ComponentName paramComponentName)
  {
    zzki(paramString);
    zzkj(paramComponentName.getClassName());
    Intent localIntent = zzbny();
    if (localIntent == null) {
      return;
    }
    localIntent.putExtra("scheduler_action", "CANCEL_TASK");
    localIntent.putExtra("tag", paramString);
    localIntent.putExtra("component", paramComponentName);
    this.mContext.sendBroadcast(localIntent);
  }
  
  private Intent zzbny()
  {
    String str = GoogleCloudMessaging.zzde(this.mContext);
    int i = -1;
    if (str != null) {
      i = GoogleCloudMessaging.zzdf(this.mContext);
    }
    if ((str == null) || (i < GoogleCloudMessaging.aeP))
    {
      Log.e("GcmNetworkManager", 91 + "Google Play Services is not available, dropping GcmNetworkManager request. code=" + i);
      return null;
    }
    Intent localIntent = new Intent("com.google.android.gms.gcm.ACTION_SCHEDULE");
    localIntent.setPackage(str);
    localIntent.putExtra("app", this.mPendingIntent);
    return localIntent;
  }
  
  static void zzki(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Must provide a valid tag.");
    }
    if (100 < paramString.length()) {
      throw new IllegalArgumentException("Tag is larger than max permissible tag length (100)");
    }
  }
  
  private void zzkj(String paramString)
  {
    boolean bool2 = true;
    zzac.zzb(paramString, "GcmTaskService must not be null.");
    Object localObject = new Intent("com.google.android.gms.gcm.ACTION_TASK_READY");
    ((Intent)localObject).setPackage(this.mContext.getPackageName());
    localObject = this.mContext.getPackageManager().queryIntentServices((Intent)localObject, 0);
    if ((localObject != null) && (((List)localObject).size() != 0))
    {
      bool1 = true;
      zzac.zzb(bool1, "There is no GcmTaskService component registered within this package. Have you extended GcmTaskService correctly?");
      localObject = ((List)localObject).iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
      } while (!((ResolveInfo)((Iterator)localObject).next()).serviceInfo.name.equals(paramString));
    }
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzac.zzb(bool1, String.valueOf(paramString).length() + 119 + "The GcmTaskService class you provided " + paramString + " does not seem to support receiving com.google.android.gms.gcm.ACTION_TASK_READY.");
      return;
      bool1 = false;
      break;
    }
  }
  
  public void cancelAllTasks(Class<? extends GcmTaskService> paramClass)
  {
    zze(paramClass);
  }
  
  public void cancelTask(String paramString, Class<? extends GcmTaskService> paramClass)
  {
    zzb(paramString, paramClass);
  }
  
  public void schedule(Task paramTask)
  {
    zzkj(paramTask.getServiceName());
    Intent localIntent = zzbny();
    if (localIntent == null) {
      return;
    }
    Bundle localBundle = localIntent.getExtras();
    localBundle.putString("scheduler_action", "SCHEDULE_TASK");
    paramTask.toBundle(localBundle);
    localIntent.putExtras(localBundle);
    this.mContext.sendBroadcast(localIntent);
  }
  
  public void zzb(String paramString, Class<? extends Service> paramClass)
  {
    zza(paramString, new ComponentName(this.mContext, paramClass));
  }
  
  public void zze(Class<? extends Service> paramClass)
  {
    zza(new ComponentName(this.mContext, paramClass));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmNetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */