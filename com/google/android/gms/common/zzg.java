package com.google.android.gms.common;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.common.internal.zzba;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.dynamite.DynamiteModule.zzc;

final class zzg
{
  private static zzba zzfky;
  private static final Object zzfkz = new Object();
  private static Context zzfla;
  
  static boolean zza(String paramString, zzh paramzzh)
  {
    return zza(paramString, paramzzh, false);
  }
  
  private static boolean zza(String paramString, zzh paramzzh, boolean paramBoolean)
  {
    if (!zzafz()) {
      return false;
    }
    zzbq.checkNotNull(zzfla);
    try
    {
      paramString = new zzn(paramString, paramzzh, paramBoolean);
      paramBoolean = zzfky.zza(paramString, com.google.android.gms.dynamic.zzn.zzz(zzfla.getPackageManager()));
      return paramBoolean;
    }
    catch (RemoteException paramString)
    {
      Log.e("GoogleCertificates", "Failed to get Google certificates from remote", paramString);
    }
    return false;
  }
  
  private static boolean zzafz()
  {
    if (zzfky != null) {
      return true;
    }
    zzbq.checkNotNull(zzfla);
    synchronized (zzfkz)
    {
      zzba localzzba = zzfky;
      if (localzzba != null) {}
    }
    return false;
  }
  
  static boolean zzb(String paramString, zzh paramzzh)
  {
    return zza(paramString, paramzzh, true);
  }
  
  /* Error */
  static void zzcg(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 31	com/google/android/gms/common/zzg:zzfla	Landroid/content/Context;
    //   6: ifnonnull +18 -> 24
    //   9: aload_0
    //   10: ifnull +10 -> 20
    //   13: aload_0
    //   14: invokevirtual 105	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   17: putstatic 31	com/google/android/gms/common/zzg:zzfla	Landroid/content/Context;
    //   20: ldc 2
    //   22: monitorexit
    //   23: return
    //   24: ldc 63
    //   26: ldc 107
    //   28: invokestatic 111	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   31: pop
    //   32: goto -12 -> 20
    //   35: astore_0
    //   36: ldc 2
    //   38: monitorexit
    //   39: aload_0
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	paramContext	Context
    // Exception table:
    //   from	to	target	type
    //   3	9	35	finally
    //   13	20	35	finally
    //   24	32	35	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */