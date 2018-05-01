package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

final class zzbcm
  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
  private zzbcm(zzbcd paramzzbcd) {}
  
  public final void onConnected(Bundle paramBundle)
  {
    zzbcd.zzf(this.zzaDp).zza(new zzbck(this.zzaDp));
  }
  
  /* Error */
  public final void onConnectionFailed(@android.support.annotation.NonNull com.google.android.gms.common.ConnectionResult paramConnectionResult)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   4: invokestatic 45	com/google/android/gms/internal/zzbcd:zzc	(Lcom/google/android/gms/internal/zzbcd;)Ljava/util/concurrent/locks/Lock;
    //   7: invokeinterface 50 1 0
    //   12: aload_0
    //   13: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   16: aload_1
    //   17: invokestatic 54	com/google/android/gms/internal/zzbcd:zzb	(Lcom/google/android/gms/internal/zzbcd;Lcom/google/android/gms/common/ConnectionResult;)Z
    //   20: ifeq +30 -> 50
    //   23: aload_0
    //   24: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   27: invokestatic 57	com/google/android/gms/internal/zzbcd:zzi	(Lcom/google/android/gms/internal/zzbcd;)V
    //   30: aload_0
    //   31: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   34: invokestatic 60	com/google/android/gms/internal/zzbcd:zzj	(Lcom/google/android/gms/internal/zzbcd;)V
    //   37: aload_0
    //   38: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   41: invokestatic 45	com/google/android/gms/internal/zzbcd:zzc	(Lcom/google/android/gms/internal/zzbcd;)Ljava/util/concurrent/locks/Lock;
    //   44: invokeinterface 63 1 0
    //   49: return
    //   50: aload_0
    //   51: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   54: aload_1
    //   55: invokestatic 66	com/google/android/gms/internal/zzbcd:zza	(Lcom/google/android/gms/internal/zzbcd;Lcom/google/android/gms/common/ConnectionResult;)V
    //   58: goto -21 -> 37
    //   61: astore_1
    //   62: aload_0
    //   63: getfield 14	com/google/android/gms/internal/zzbcm:zzaDp	Lcom/google/android/gms/internal/zzbcd;
    //   66: invokestatic 45	com/google/android/gms/internal/zzbcd:zzc	(Lcom/google/android/gms/internal/zzbcd;)Ljava/util/concurrent/locks/Lock;
    //   69: invokeinterface 63 1 0
    //   74: aload_1
    //   75: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	76	0	this	zzbcm
    //   0	76	1	paramConnectionResult	com.google.android.gms.common.ConnectionResult
    // Exception table:
    //   from	to	target	type
    //   12	37	61	finally
    //   50	58	61	finally
  }
  
  public final void onConnectionSuspended(int paramInt) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbcm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */