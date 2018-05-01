package com.google.android.gms.internal;

import android.content.Context;

public final class zzbhd
{
  private static Context zzgfe;
  private static Boolean zzgff;
  
  /* Error */
  public static boolean zzcz(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_0
    //   4: invokevirtual 18	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   7: astore_2
    //   8: getstatic 20	com/google/android/gms/internal/zzbhd:zzgfe	Landroid/content/Context;
    //   11: ifnull +28 -> 39
    //   14: getstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   17: ifnull +22 -> 39
    //   20: getstatic 20	com/google/android/gms/internal/zzbhd:zzgfe	Landroid/content/Context;
    //   23: aload_2
    //   24: if_acmpne +15 -> 39
    //   27: getstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   30: invokevirtual 28	java/lang/Boolean:booleanValue	()Z
    //   33: istore_1
    //   34: ldc 2
    //   36: monitorexit
    //   37: iload_1
    //   38: ireturn
    //   39: aconst_null
    //   40: putstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   43: invokestatic 33	com/google/android/gms/common/util/zzq:isAtLeastO	()Z
    //   46: ifeq +30 -> 76
    //   49: aload_2
    //   50: invokevirtual 37	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   53: invokevirtual 42	android/content/pm/PackageManager:isInstantApp	()Z
    //   56: invokestatic 46	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   59: putstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   62: aload_2
    //   63: putstatic 20	com/google/android/gms/internal/zzbhd:zzgfe	Landroid/content/Context;
    //   66: getstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   69: invokevirtual 28	java/lang/Boolean:booleanValue	()Z
    //   72: istore_1
    //   73: goto -39 -> 34
    //   76: aload_0
    //   77: invokevirtual 50	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   80: ldc 52
    //   82: invokevirtual 58	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   85: pop
    //   86: iconst_1
    //   87: invokestatic 46	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   90: putstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   93: goto -31 -> 62
    //   96: astore_0
    //   97: iconst_0
    //   98: invokestatic 46	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   101: putstatic 22	com/google/android/gms/internal/zzbhd:zzgff	Ljava/lang/Boolean;
    //   104: goto -42 -> 62
    //   107: astore_0
    //   108: ldc 2
    //   110: monitorexit
    //   111: aload_0
    //   112: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	113	0	paramContext	Context
    //   33	40	1	bool	boolean
    //   7	56	2	localContext	Context
    // Exception table:
    //   from	to	target	type
    //   76	93	96	java/lang/ClassNotFoundException
    //   3	34	107	finally
    //   39	62	107	finally
    //   62	73	107	finally
    //   76	93	107	finally
    //   97	104	107	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbhd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */