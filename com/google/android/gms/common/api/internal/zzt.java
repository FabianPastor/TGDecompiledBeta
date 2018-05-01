package com.google.android.gms.common.api.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import java.util.concurrent.locks.Lock;

final class zzt
  implements zzbq
{
  private zzt(zzr paramzzr) {}
  
  /* Error */
  public final void zzb(int paramInt, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   4: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   7: invokeinterface 32 1 0
    //   12: aload_0
    //   13: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   16: invokestatic 36	com/google/android/gms/common/api/internal/zzr:zzc	(Lcom/google/android/gms/common/api/internal/zzr;)Z
    //   19: ifne +26 -> 45
    //   22: aload_0
    //   23: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   26: invokestatic 40	com/google/android/gms/common/api/internal/zzr:zzd	(Lcom/google/android/gms/common/api/internal/zzr;)Lcom/google/android/gms/common/ConnectionResult;
    //   29: ifnull +16 -> 45
    //   32: aload_0
    //   33: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   36: invokestatic 40	com/google/android/gms/common/api/internal/zzr:zzd	(Lcom/google/android/gms/common/api/internal/zzr;)Lcom/google/android/gms/common/ConnectionResult;
    //   39: invokevirtual 46	com/google/android/gms/common/ConnectionResult:isSuccess	()Z
    //   42: ifne +34 -> 76
    //   45: aload_0
    //   46: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   49: iconst_0
    //   50: invokestatic 49	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;Z)Z
    //   53: pop
    //   54: aload_0
    //   55: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   58: iload_1
    //   59: iload_2
    //   60: invokestatic 52	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;IZ)V
    //   63: aload_0
    //   64: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   67: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   70: invokeinterface 55 1 0
    //   75: return
    //   76: aload_0
    //   77: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   80: iconst_1
    //   81: invokestatic 49	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;Z)Z
    //   84: pop
    //   85: aload_0
    //   86: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   89: invokestatic 59	com/google/android/gms/common/api/internal/zzr:zze	(Lcom/google/android/gms/common/api/internal/zzr;)Lcom/google/android/gms/common/api/internal/zzbd;
    //   92: iload_1
    //   93: invokevirtual 65	com/google/android/gms/common/api/internal/zzbd:onConnectionSuspended	(I)V
    //   96: aload_0
    //   97: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   100: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   103: invokeinterface 55 1 0
    //   108: goto -33 -> 75
    //   111: astore_3
    //   112: aload_0
    //   113: getfield 12	com/google/android/gms/common/api/internal/zzt:zzgc	Lcom/google/android/gms/common/api/internal/zzr;
    //   116: invokestatic 27	com/google/android/gms/common/api/internal/zzr:zza	(Lcom/google/android/gms/common/api/internal/zzr;)Ljava/util/concurrent/locks/Lock;
    //   119: invokeinterface 55 1 0
    //   124: aload_3
    //   125: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	zzt
    //   0	126	1	paramInt	int
    //   0	126	2	paramBoolean	boolean
    //   111	14	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	45	111	finally
    //   45	63	111	finally
    //   76	96	111	finally
  }
  
  public final void zzb(Bundle paramBundle)
  {
    zzr.zza(this.zzgc).lock();
    try
    {
      zzr.zza(this.zzgc, paramBundle);
      zzr.zza(this.zzgc, ConnectionResult.RESULT_SUCCESS);
      zzr.zzb(this.zzgc);
      return;
    }
    finally
    {
      zzr.zza(this.zzgc).unlock();
    }
  }
  
  public final void zzc(ConnectionResult paramConnectionResult)
  {
    zzr.zza(this.zzgc).lock();
    try
    {
      zzr.zza(this.zzgc, paramConnectionResult);
      zzr.zzb(this.zzgc);
      return;
    }
    finally
    {
      zzr.zza(this.zzgc).unlock();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */