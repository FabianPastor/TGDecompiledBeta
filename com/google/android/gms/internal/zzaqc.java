package com.google.android.gms.internal;

public class zzaqc
{
  private zzaqd zzaXk = null;
  private boolean zztZ = false;
  
  /* Error */
  public void initialize(android.content.Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 14	com/google/android/gms/internal/zzaqc:zztZ	Z
    //   6: ifeq +6 -> 12
    //   9: aload_0
    //   10: monitorexit
    //   11: return
    //   12: aload_0
    //   13: aload_1
    //   14: getstatic 29	com/google/android/gms/dynamite/DynamiteModule:zzaRY	Lcom/google/android/gms/dynamite/DynamiteModule$zzb;
    //   17: ldc 31
    //   19: invokestatic 35	com/google/android/gms/dynamite/DynamiteModule:zza	(Landroid/content/Context;Lcom/google/android/gms/dynamite/DynamiteModule$zzb;Ljava/lang/String;)Lcom/google/android/gms/dynamite/DynamiteModule;
    //   22: ldc 37
    //   24: invokevirtual 41	com/google/android/gms/dynamite/DynamiteModule:zzdT	(Ljava/lang/String;)Landroid/os/IBinder;
    //   27: invokestatic 47	com/google/android/gms/internal/zzaqd$zza:asInterface	(Landroid/os/IBinder;)Lcom/google/android/gms/internal/zzaqd;
    //   30: putfield 16	com/google/android/gms/internal/zzaqc:zzaXk	Lcom/google/android/gms/internal/zzaqd;
    //   33: aload_0
    //   34: getfield 16	com/google/android/gms/internal/zzaqc:zzaXk	Lcom/google/android/gms/internal/zzaqd;
    //   37: aload_1
    //   38: invokestatic 53	com/google/android/gms/dynamic/zzd:zzA	(Ljava/lang/Object;)Lcom/google/android/gms/dynamic/IObjectWrapper;
    //   41: invokeinterface 59 2 0
    //   46: aload_0
    //   47: iconst_1
    //   48: putfield 14	com/google/android/gms/internal/zzaqc:zztZ	Z
    //   51: aload_0
    //   52: monitorexit
    //   53: return
    //   54: astore_1
    //   55: aload_0
    //   56: monitorexit
    //   57: aload_1
    //   58: athrow
    //   59: astore_1
    //   60: ldc 61
    //   62: ldc 63
    //   64: aload_1
    //   65: invokestatic 69	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   68: pop
    //   69: goto -18 -> 51
    //   72: astore_1
    //   73: goto -13 -> 60
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	76	0	this	zzaqc
    //   0	76	1	paramContext	android.content.Context
    // Exception table:
    //   from	to	target	type
    //   2	11	54	finally
    //   12	51	54	finally
    //   51	53	54	finally
    //   55	57	54	finally
    //   60	69	54	finally
    //   12	51	59	android/os/RemoteException
    //   12	51	72	com/google/android/gms/dynamite/DynamiteModule$zza
  }
  
  public <T> T zzb(zzaqa<T> paramzzaqa)
  {
    try
    {
      if (!this.zztZ)
      {
        paramzzaqa = paramzzaqa.zzfr();
        return paramzzaqa;
      }
      return (T)paramzzaqa.zza(this.zzaXk);
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */