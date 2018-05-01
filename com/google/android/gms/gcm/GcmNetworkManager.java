package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.iid.zze;
import java.util.Iterator;
import java.util.List;

public class GcmNetworkManager
{
  public static final int RESULT_FAILURE = 2;
  public static final int RESULT_RESCHEDULE = 1;
  public static final int RESULT_SUCCESS = 0;
  private static GcmNetworkManager zzbfv;
  private Context mContext;
  private final PendingIntent mPendingIntent;
  
  private GcmNetworkManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent().setPackage("com.google.example.invalidpackage"), 0);
  }
  
  public static GcmNetworkManager getInstance(Context paramContext)
  {
    try
    {
      if (zzbfv == null) {
        zzbfv = new GcmNetworkManager(paramContext.getApplicationContext());
      }
      paramContext = zzbfv;
      return paramContext;
    }
    finally {}
  }
  
  static void zzdn(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("Must provide a valid tag.");
    }
    if (100 < paramString.length()) {
      throw new IllegalArgumentException("Tag is larger than max permissible tag length (100)");
    }
  }
  
  private final void zzdo(String paramString)
  {
    boolean bool2 = true;
    zzbo.zzb(paramString, "GcmTaskService must not be null.");
    Object localObject = new Intent("com.google.android.gms.gcm.ACTION_TASK_READY");
    ((Intent)localObject).setPackage(this.mContext.getPackageName());
    localObject = this.mContext.getPackageManager().queryIntentServices((Intent)localObject, 0);
    if ((localObject != null) && (((List)localObject).size() != 0))
    {
      bool1 = true;
      zzbo.zzb(bool1, "There is no GcmTaskService component registered within this package. Have you extended GcmTaskService correctly?");
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
      zzbo.zzb(bool1, String.valueOf(paramString).length() + 119 + "The GcmTaskService class you provided " + paramString + " does not seem to support receiving com.google.android.gms.gcm.ACTION_TASK_READY.");
      return;
      bool1 = false;
      break;
    }
  }
  
  private final Intent zzvB()
  {
    String str = zze.zzbd(this.mContext);
    int i = -1;
    if (str != null) {
      i = GoogleCloudMessaging.zzaZ(this.mContext);
    }
    if ((str == null) || (i < GoogleCloudMessaging.zzbfL))
    {
      Log.e("GcmNetworkManager", 91 + "Google Play Services is not available, dropping GcmNetworkManager request. code=" + i);
      return null;
    }
    Intent localIntent = new Intent("com.google.android.gms.gcm.ACTION_SCHEDULE");
    localIntent.setPackage(str);
    localIntent.putExtra("app", this.mPendingIntent);
    localIntent.putExtra("source", 4);
    localIntent.putExtra("source_version", 11020000);
    return localIntent;
  }
  
  public void cancelAllTasks(Class<? extends GcmTaskService> paramClass)
  {
    paramClass = new ComponentName(this.mContext, paramClass);
    zzdo(paramClass.getClassName());
    Intent localIntent = zzvB();
    if (localIntent != null)
    {
      localIntent.putExtra("scheduler_action", "CANCEL_ALL");
      localIntent.putExtra("component", paramClass);
      this.mContext.sendBroadcast(localIntent);
    }
  }
  
  public void cancelTask(String paramString, Class<? extends GcmTaskService> paramClass)
  {
    paramClass = new ComponentName(this.mContext, paramClass);
    zzdn(paramString);
    zzdo(paramClass.getClassName());
    Intent localIntent = zzvB();
    if (localIntent != null)
    {
      localIntent.putExtra("scheduler_action", "CANCEL_TASK");
      localIntent.putExtra("tag", paramString);
      localIntent.putExtra("component", paramClass);
      this.mContext.sendBroadcast(localIntent);
    }
  }
  
  public void schedule(Task paramTask)
  {
    zzdo(paramTask.getServiceName());
    Intent localIntent = zzvB();
    if (localIntent == null) {
      return;
    }
    Bundle localBundle = localIntent.getExtras();
    localBundle.putString("scheduler_action", "SCHEDULE_TASK");
    paramTask.toBundle(localBundle);
    localIntent.putExtras(localBundle);
    this.mContext.sendBroadcast(localIntent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmNetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */