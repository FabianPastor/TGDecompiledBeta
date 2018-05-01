package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.PlatformVersion;
import javax.annotation.concurrent.GuardedBy;

public final class FirebaseInstanceIdReceiver
  extends WakefulBroadcastReceiver
{
  private static boolean zzbqs = false;
  @GuardedBy("FirebaseInstanceIdReceiver.class")
  private static zzh zzbqt;
  @GuardedBy("FirebaseInstanceIdReceiver.class")
  private static zzh zzbqu;
  
  private final void zza(Context paramContext, Intent paramIntent, String paramString)
  {
    Object localObject = null;
    int i = 0;
    int j = -1;
    paramIntent.setComponent(null);
    paramIntent.setPackage(paramContext.getPackageName());
    if (Build.VERSION.SDK_INT <= 18) {
      paramIntent.removeCategory(paramContext.getPackageName());
    }
    String str = paramIntent.getStringExtra("gcm.rawData64");
    if (str != null)
    {
      paramIntent.putExtra("rawData", Base64.decode(str, 0));
      paramIntent.removeExtra("gcm.rawData64");
    }
    if (("google.com/iid".equals(paramIntent.getStringExtra("from"))) || ("com.google.firebase.INSTANCE_ID_EVENT".equals(paramString)))
    {
      paramString = "com.google.firebase.INSTANCE_ID_EVENT";
      k = j;
      if (paramString != null)
      {
        k = i;
        if (PlatformVersion.isAtLeastO())
        {
          k = i;
          if (paramContext.getApplicationInfo().targetSdkVersion >= 26) {
            k = 1;
          }
        }
        if (k == 0) {
          break label221;
        }
        if (isOrderedBroadcast()) {
          setResultCode(-1);
        }
        zzi(paramContext, paramString).zza(paramIntent, goAsync());
      }
    }
    label221:
    for (int k = j;; k = zzz.zzta().zza(paramContext, paramString, paramIntent))
    {
      if (isOrderedBroadcast()) {
        setResultCode(k);
      }
      return;
      if (("com.google.android.c2dm.intent.RECEIVE".equals(paramString)) || ("com.google.firebase.MESSAGING_EVENT".equals(paramString)))
      {
        paramString = "com.google.firebase.MESSAGING_EVENT";
        break;
      }
      Log.d("FirebaseInstanceId", "Unexpected intent");
      paramString = (String)localObject;
      break;
    }
  }
  
  /* Error */
  private static zzh zzi(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: ldc 120
    //   5: aload_1
    //   6: invokevirtual 79	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   9: ifeq +32 -> 41
    //   12: getstatic 141	com/google/firebase/iid/FirebaseInstanceIdReceiver:zzbqu	Lcom/google/firebase/iid/zzh;
    //   15: ifnonnull +17 -> 32
    //   18: new 113	com/google/firebase/iid/zzh
    //   21: astore_2
    //   22: aload_2
    //   23: aload_0
    //   24: aload_1
    //   25: invokespecial 144	com/google/firebase/iid/zzh:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   28: aload_2
    //   29: putstatic 141	com/google/firebase/iid/FirebaseInstanceIdReceiver:zzbqu	Lcom/google/firebase/iid/zzh;
    //   32: getstatic 141	com/google/firebase/iid/FirebaseInstanceIdReceiver:zzbqu	Lcom/google/firebase/iid/zzh;
    //   35: astore_0
    //   36: ldc 2
    //   38: monitorexit
    //   39: aload_0
    //   40: areturn
    //   41: getstatic 146	com/google/firebase/iid/FirebaseInstanceIdReceiver:zzbqt	Lcom/google/firebase/iid/zzh;
    //   44: ifnonnull +17 -> 61
    //   47: new 113	com/google/firebase/iid/zzh
    //   50: astore_2
    //   51: aload_2
    //   52: aload_0
    //   53: aload_1
    //   54: invokespecial 144	com/google/firebase/iid/zzh:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   57: aload_2
    //   58: putstatic 146	com/google/firebase/iid/FirebaseInstanceIdReceiver:zzbqt	Lcom/google/firebase/iid/zzh;
    //   61: getstatic 146	com/google/firebase/iid/FirebaseInstanceIdReceiver:zzbqt	Lcom/google/firebase/iid/zzh;
    //   64: astore_0
    //   65: goto -29 -> 36
    //   68: astore_0
    //   69: ldc 2
    //   71: monitorexit
    //   72: aload_0
    //   73: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	74	0	paramContext	Context
    //   0	74	1	paramString	String
    //   21	37	2	localzzh	zzh
    // Exception table:
    //   from	to	target	type
    //   3	32	68	finally
    //   32	36	68	finally
    //   41	61	68	finally
    //   61	65	68	finally
  }
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null) {}
    for (;;)
    {
      return;
      Parcelable localParcelable = paramIntent.getParcelableExtra("wrapped_intent");
      if ((localParcelable instanceof Intent)) {
        zza(paramContext, (Intent)localParcelable, paramIntent.getAction());
      } else {
        zza(paramContext, paramIntent, paramIntent.getAction());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/FirebaseInstanceIdReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */