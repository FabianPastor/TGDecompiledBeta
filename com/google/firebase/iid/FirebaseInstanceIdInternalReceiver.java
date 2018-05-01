package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public final class FirebaseInstanceIdInternalReceiver
  extends WakefulBroadcastReceiver
{
  private static boolean zzbfB = false;
  private static zzh zzckA;
  private static zzh zzckz;
  
  /* Error */
  static zzh zzH(Context paramContext, String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: ldc 21
    //   5: aload_1
    //   6: invokevirtual 27	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   9: ifeq +30 -> 39
    //   12: getstatic 29	com/google/firebase/iid/FirebaseInstanceIdInternalReceiver:zzckA	Lcom/google/firebase/iid/zzh;
    //   15: ifnonnull +15 -> 30
    //   18: new 31	com/google/firebase/iid/zzh
    //   21: dup
    //   22: aload_0
    //   23: aload_1
    //   24: invokespecial 34	com/google/firebase/iid/zzh:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   27: putstatic 29	com/google/firebase/iid/FirebaseInstanceIdInternalReceiver:zzckA	Lcom/google/firebase/iid/zzh;
    //   30: getstatic 29	com/google/firebase/iid/FirebaseInstanceIdInternalReceiver:zzckA	Lcom/google/firebase/iid/zzh;
    //   33: astore_0
    //   34: ldc 2
    //   36: monitorexit
    //   37: aload_0
    //   38: areturn
    //   39: getstatic 36	com/google/firebase/iid/FirebaseInstanceIdInternalReceiver:zzckz	Lcom/google/firebase/iid/zzh;
    //   42: ifnonnull +15 -> 57
    //   45: new 31	com/google/firebase/iid/zzh
    //   48: dup
    //   49: aload_0
    //   50: aload_1
    //   51: invokespecial 34	com/google/firebase/iid/zzh:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   54: putstatic 36	com/google/firebase/iid/FirebaseInstanceIdInternalReceiver:zzckz	Lcom/google/firebase/iid/zzh;
    //   57: getstatic 36	com/google/firebase/iid/FirebaseInstanceIdInternalReceiver:zzckz	Lcom/google/firebase/iid/zzh;
    //   60: astore_0
    //   61: goto -27 -> 34
    //   64: astore_0
    //   65: ldc 2
    //   67: monitorexit
    //   68: aload_0
    //   69: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	70	0	paramContext	Context
    //   0	70	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   3	30	64	finally
    //   30	34	64	finally
    //   39	57	64	finally
    //   57	61	64	finally
  }
  
  static boolean zzbH(Context paramContext)
  {
    if (!com.google.android.gms.common.util.zzq.isAtLeastO()) {}
    while (paramContext.getApplicationInfo().targetSdkVersion <= 25) {
      return false;
    }
    return true;
  }
  
  public final void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null) {
      return;
    }
    Object localObject = paramIntent.getParcelableExtra("wrapped_intent");
    if (!(localObject instanceof Intent))
    {
      Log.e("FirebaseInstanceId", "Missing or invalid wrapped intent");
      return;
    }
    localObject = (Intent)localObject;
    if (zzbH(paramContext))
    {
      zzH(paramContext, paramIntent.getAction()).zza((Intent)localObject, goAsync());
      return;
    }
    zzq.zzJX().zza(paramContext, paramIntent.getAction(), (Intent)localObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/FirebaseInstanceIdInternalReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */