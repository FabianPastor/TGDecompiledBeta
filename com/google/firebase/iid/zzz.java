package com.google.firebase.iid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import java.util.ArrayDeque;
import java.util.Queue;
import javax.annotation.concurrent.GuardedBy;

public final class zzz
{
  private static zzz zzbru;
  @GuardedBy("serviceClassNames")
  private final SimpleArrayMap<String, String> zzbrv = new SimpleArrayMap();
  private Boolean zzbrw = null;
  final Queue<Intent> zzbrx = new ArrayDeque();
  private final Queue<Intent> zzbry = new ArrayDeque();
  
  public static PendingIntent zza(Context paramContext, int paramInt1, Intent paramIntent, int paramInt2)
  {
    Intent localIntent = new Intent(paramContext, FirebaseInstanceIdReceiver.class);
    localIntent.setAction("com.google.firebase.MESSAGING_EVENT");
    localIntent.putExtra("wrapped_intent", paramIntent);
    return PendingIntent.getBroadcast(paramContext, paramInt1, localIntent, NUM);
  }
  
  private final int zzb(Context paramContext, Intent paramIntent)
  {
    for (;;)
    {
      synchronized (this.zzbrv)
      {
        ??? = (String)this.zzbrv.get(paramIntent.getAction());
        ??? = ???;
        if (??? != null) {
          break label305;
        }
        ??? = paramContext.getPackageManager().resolveService(paramIntent, 0);
        if ((??? == null) || (((ResolveInfo)???).serviceInfo == null)) {
          Log.e("FirebaseInstanceId", "Failed to resolve target intent service, skipping classname enforcement");
        }
        try
        {
          if (this.zzbrw == null)
          {
            if (paramContext.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") != 0) {
              break label390;
            }
            bool = true;
            this.zzbrw = Boolean.valueOf(bool);
          }
          if (!this.zzbrw.booleanValue()) {
            break label396;
          }
          paramContext = WakefulBroadcastReceiver.startWakefulService(paramContext, paramIntent);
          if (paramContext != null) {
            break;
          }
          Log.e("FirebaseInstanceId", "Error while delivering the message: ServiceIntent not found.");
          i = 404;
        }
        catch (SecurityException paramContext)
        {
          boolean bool;
          Log.e("FirebaseInstanceId", "Error while delivering the message to the serviceIntent", paramContext);
          i = 401;
          continue;
          i = -1;
          continue;
        }
        catch (IllegalStateException paramContext)
        {
          paramContext = String.valueOf(paramContext);
          Log.e("FirebaseInstanceId", String.valueOf(paramContext).length() + 45 + "Failed to start service while in background: " + paramContext);
          int i = 402;
          continue;
        }
        return i;
      }
      ??? = ((ResolveInfo)???).serviceInfo;
      if ((!paramContext.getPackageName().equals(((ServiceInfo)???).packageName)) || (((ServiceInfo)???).name == null))
      {
        ??? = ((ServiceInfo)???).packageName;
        ??? = ((ServiceInfo)???).name;
        Log.e("FirebaseInstanceId", String.valueOf(???).length() + 94 + String.valueOf(???).length() + "Error resolving target intent service, skipping classname enforcement. Resolved service was: " + (String)??? + "/" + (String)???);
      }
      else
      {
        ??? = ((ServiceInfo)???).name;
        ??? = ???;
        if (((String)???).startsWith("."))
        {
          ??? = String.valueOf(paramContext.getPackageName());
          ??? = String.valueOf(???);
          if (((String)???).length() == 0) {
            break label358;
          }
          ??? = ((String)???).concat((String)???);
        }
        for (;;)
        {
          synchronized (this.zzbrv)
          {
            this.zzbrv.put(paramIntent.getAction(), ???);
            label305:
            if (Log.isLoggable("FirebaseInstanceId", 3))
            {
              ??? = String.valueOf(???);
              if (((String)???).length() != 0)
              {
                ??? = "Restricting intent to a specific service: ".concat((String)???);
                Log.d("FirebaseInstanceId", (String)???);
              }
            }
            else
            {
              paramIntent.setClassName(paramContext.getPackageName(), (String)???);
              break;
              label358:
              ??? = new String((String)???);
            }
          }
          ??? = new String("Restricting intent to a specific service: ");
        }
        label390:
        bool = false;
        continue;
        label396:
        paramContext = paramContext.startService(paramIntent);
        Log.d("FirebaseInstanceId", "Missing wake lock permission, service start may be delayed");
      }
    }
  }
  
  public static zzz zzta()
  {
    try
    {
      if (zzbru == null)
      {
        localzzz = new com/google/firebase/iid/zzz;
        localzzz.<init>();
        zzbru = localzzz;
      }
      zzz localzzz = zzbru;
      return localzzz;
    }
    finally {}
  }
  
  public final int zza(Context paramContext, String paramString, Intent paramIntent)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        paramContext = String.valueOf(paramString);
        if (paramContext.length() == 0) {}
        break;
      }
      break;
    }
    for (paramContext = "Unknown service action: ".concat(paramContext);; paramContext = new String("Unknown service action: "))
    {
      Log.w("FirebaseInstanceId", paramContext);
      i = 500;
      return i;
      if (!paramString.equals("com.google.firebase.INSTANCE_ID_EVENT")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("com.google.firebase.MESSAGING_EVENT")) {
        break;
      }
      i = 1;
      break;
      this.zzbrx.offer(paramIntent);
      for (;;)
      {
        paramString = new Intent(paramString);
        paramString.setPackage(paramContext.getPackageName());
        i = zzb(paramContext, paramString);
        break;
        this.zzbry.offer(paramIntent);
      }
    }
  }
  
  public final Intent zztb()
  {
    return (Intent)this.zzbry.poll();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */