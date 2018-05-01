package com.google.android.gms.gcm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.iid.zzh;

public class GcmReceiver
  extends WakefulBroadcastReceiver
{
  private static boolean zzicc = false;
  private static zzh zzicd;
  private static zzh zzice;
  
  private final void doStartService(Context paramContext, Intent paramIntent)
  {
    if (isOrderedBroadcast()) {
      setResultCode(500);
    }
    Object localObject1 = paramContext.getPackageManager().resolveService(paramIntent, 0);
    if ((localObject1 == null) || (((ResolveInfo)localObject1).serviceInfo == null)) {
      Log.e("GcmReceiver", "Failed to resolve target intent service, skipping classname enforcement");
    }
    do
    {
      for (;;)
      {
        try
        {
          if (paramContext.checkCallingOrSelfPermission("android.permission.WAKE_LOCK") != 0) {
            continue;
          }
          paramContext = startWakefulService(paramContext, paramIntent);
          if (paramContext != null) {
            break;
          }
          Log.e("GcmReceiver", "Error while delivering the message: ServiceIntent not found.");
          if (isOrderedBroadcast()) {
            setResultCode(404);
          }
          return;
        }
        catch (SecurityException paramContext)
        {
          Object localObject2;
          Log.e("GcmReceiver", "Error while delivering the message to the serviceIntent", paramContext);
          if (!isOrderedBroadcast()) {
            continue;
          }
          setResultCode(401);
          return;
        }
        localObject2 = ((ResolveInfo)localObject1).serviceInfo;
        if ((!paramContext.getPackageName().equals(((ServiceInfo)localObject2).packageName)) || (((ServiceInfo)localObject2).name == null))
        {
          localObject1 = ((ServiceInfo)localObject2).packageName;
          localObject2 = ((ServiceInfo)localObject2).name;
          Log.e("GcmReceiver", String.valueOf(localObject1).length() + 94 + String.valueOf(localObject2).length() + "Error resolving target intent service, skipping classname enforcement. Resolved service was: " + (String)localObject1 + "/" + (String)localObject2);
        }
        else
        {
          localObject2 = ((ServiceInfo)localObject2).name;
          localObject1 = localObject2;
          if (((String)localObject2).startsWith("."))
          {
            localObject1 = String.valueOf(paramContext.getPackageName());
            localObject2 = String.valueOf(localObject2);
            if (((String)localObject2).length() != 0) {
              localObject1 = ((String)localObject1).concat((String)localObject2);
            }
          }
          else
          {
            if (Log.isLoggable("GcmReceiver", 3))
            {
              localObject2 = String.valueOf(localObject1);
              if (((String)localObject2).length() == 0) {
                continue;
              }
              localObject2 = "Restricting intent to a specific service: ".concat((String)localObject2);
              Log.d("GcmReceiver", (String)localObject2);
            }
            paramIntent.setClassName(paramContext.getPackageName(), (String)localObject1);
            continue;
          }
          localObject1 = new String((String)localObject1);
          continue;
          localObject2 = new String("Restricting intent to a specific service: ");
          continue;
          paramContext = paramContext.startService(paramIntent);
          Log.d("GcmReceiver", "Missing wake lock permission, service start may be delayed");
        }
      }
    } while (!isOrderedBroadcast());
    setResultCode(-1);
  }
  
  /* Error */
  private final zzh zzae(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc -102
    //   4: aload_2
    //   5: invokevirtual 85	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   8: ifeq +29 -> 37
    //   11: getstatic 156	com/google/android/gms/gcm/GcmReceiver:zzice	Lcom/google/android/gms/iid/zzh;
    //   14: ifnonnull +15 -> 29
    //   17: new 158	com/google/android/gms/iid/zzh
    //   20: dup
    //   21: aload_1
    //   22: aload_2
    //   23: invokespecial 161	com/google/android/gms/iid/zzh:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   26: putstatic 156	com/google/android/gms/gcm/GcmReceiver:zzice	Lcom/google/android/gms/iid/zzh;
    //   29: getstatic 156	com/google/android/gms/gcm/GcmReceiver:zzice	Lcom/google/android/gms/iid/zzh;
    //   32: astore_1
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_1
    //   36: areturn
    //   37: getstatic 163	com/google/android/gms/gcm/GcmReceiver:zzicd	Lcom/google/android/gms/iid/zzh;
    //   40: ifnonnull +15 -> 55
    //   43: new 158	com/google/android/gms/iid/zzh
    //   46: dup
    //   47: aload_1
    //   48: aload_2
    //   49: invokespecial 161	com/google/android/gms/iid/zzh:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   52: putstatic 163	com/google/android/gms/gcm/GcmReceiver:zzicd	Lcom/google/android/gms/iid/zzh;
    //   55: getstatic 163	com/google/android/gms/gcm/GcmReceiver:zzicd	Lcom/google/android/gms/iid/zzh;
    //   58: astore_1
    //   59: goto -26 -> 33
    //   62: astore_1
    //   63: aload_0
    //   64: monitorexit
    //   65: aload_1
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	GcmReceiver
    //   0	67	1	paramContext	Context
    //   0	67	2	paramString	String
    // Exception table:
    //   from	to	target	type
    //   2	29	62	finally
    //   29	33	62	finally
    //   37	55	62	finally
    //   55	59	62	finally
  }
  
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    int j = 0;
    if (Log.isLoggable("GcmReceiver", 3)) {
      Log.d("GcmReceiver", "received new intent");
    }
    paramIntent.setComponent(null);
    paramIntent.setPackage(paramContext.getPackageName());
    if (Build.VERSION.SDK_INT <= 18) {
      paramIntent.removeCategory(paramContext.getPackageName());
    }
    String str = paramIntent.getStringExtra("from");
    if (("google.com/iid".equals(str)) || ("gcm.googleapis.com/refresh".equals(str))) {
      paramIntent.setAction("com.google.android.gms.iid.InstanceID");
    }
    str = paramIntent.getStringExtra("gcm.rawData64");
    if (str != null)
    {
      paramIntent.putExtra("rawData", Base64.decode(str, 0));
      paramIntent.removeExtra("gcm.rawData64");
    }
    int i = j;
    if (zzq.isAtLeastO())
    {
      i = j;
      if (paramContext.getApplicationInfo().targetSdkVersion > 25) {
        i = 1;
      }
    }
    if (i != 0)
    {
      if (isOrderedBroadcast()) {
        setResultCode(-1);
      }
      zzae(paramContext, paramIntent.getAction()).zza(paramIntent, goAsync());
    }
    for (;;)
    {
      return;
      if ("com.google.android.c2dm.intent.RECEIVE".equals(paramIntent.getAction())) {
        doStartService(paramContext, paramIntent);
      }
      while ((isOrderedBroadcast()) && (getResultCode() == 0))
      {
        setResultCode(-1);
        return;
        doStartService(paramContext, paramIntent);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */