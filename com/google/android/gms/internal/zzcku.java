package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzd;
import com.google.android.gms.common.internal.zzf;
import com.google.android.gms.common.internal.zzg;
import com.google.android.gms.common.stats.zza;

public final class zzcku
  implements ServiceConnection, zzf, zzg
{
  private volatile boolean zzjiq;
  private volatile zzchl zzjir;
  
  protected zzcku(zzckg paramzzckg) {}
  
  /* Error */
  public final void onConnected(android.os.Bundle paramBundle)
  {
    // Byte code:
    //   0: ldc 36
    //   2: invokestatic 42	com/google/android/gms/common/internal/zzbq:zzge	(Ljava/lang/String;)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 44	com/google/android/gms/internal/zzcku:zzjir	Lcom/google/android/gms/internal/zzchl;
    //   11: invokevirtual 50	com/google/android/gms/common/internal/zzd:zzakn	()Landroid/os/IInterface;
    //   14: checkcast 52	com/google/android/gms/internal/zzche
    //   17: astore_1
    //   18: aload_0
    //   19: aconst_null
    //   20: putfield 44	com/google/android/gms/internal/zzcku:zzjir	Lcom/google/android/gms/internal/zzchl;
    //   23: aload_0
    //   24: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   27: invokevirtual 58	com/google/android/gms/internal/zzcjk:zzawx	()Lcom/google/android/gms/internal/zzcih;
    //   30: new 60	com/google/android/gms/internal/zzckx
    //   33: dup
    //   34: aload_0
    //   35: aload_1
    //   36: invokespecial 63	com/google/android/gms/internal/zzckx:<init>	(Lcom/google/android/gms/internal/zzcku;Lcom/google/android/gms/internal/zzche;)V
    //   39: invokevirtual 69	com/google/android/gms/internal/zzcih:zzg	(Ljava/lang/Runnable;)V
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 44	com/google/android/gms/internal/zzcku:zzjir	Lcom/google/android/gms/internal/zzchl;
    //   50: aload_0
    //   51: iconst_0
    //   52: putfield 28	com/google/android/gms/internal/zzcku:zzjiq	Z
    //   55: goto -13 -> 42
    //   58: astore_1
    //   59: aload_0
    //   60: monitorexit
    //   61: aload_1
    //   62: athrow
    //   63: astore_1
    //   64: goto -19 -> 45
    //   67: astore_1
    //   68: goto -23 -> 45
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	71	0	this	zzcku
    //   0	71	1	paramBundle	android.os.Bundle
    // Exception table:
    //   from	to	target	type
    //   7	42	58	finally
    //   42	44	58	finally
    //   45	55	58	finally
    //   59	61	58	finally
    //   7	42	63	android/os/DeadObjectException
    //   7	42	67	java/lang/IllegalStateException
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    zzbq.zzge("MeasurementServiceConnection.onConnectionFailed");
    zzchm localzzchm = this.zzjij.zziwf.zzazx();
    if (localzzchm != null) {
      localzzchm.zzazf().zzj("Service connection failed", paramConnectionResult);
    }
    try
    {
      this.zzjiq = false;
      this.zzjir = null;
      this.zzjij.zzawx().zzg(new zzckz(this));
      return;
    }
    finally {}
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    zzbq.zzge("MeasurementServiceConnection.onConnectionSuspended");
    this.zzjij.zzawy().zzazi().log("Service connection suspended");
    this.zzjij.zzawx().zzg(new zzcky(this));
  }
  
  /* Error */
  public final void onServiceConnected(ComponentName paramComponentName, android.os.IBinder paramIBinder)
  {
    // Byte code:
    //   0: ldc -126
    //   2: invokestatic 42	com/google/android/gms/common/internal/zzbq:zzge	(Ljava/lang/String;)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_2
    //   8: ifnonnull +26 -> 34
    //   11: aload_0
    //   12: iconst_0
    //   13: putfield 28	com/google/android/gms/internal/zzcku:zzjiq	Z
    //   16: aload_0
    //   17: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   20: invokevirtual 111	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   23: invokevirtual 133	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   26: ldc -121
    //   28: invokevirtual 119	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   31: aload_0
    //   32: monitorexit
    //   33: return
    //   34: aload_2
    //   35: invokeinterface 141 1 0
    //   40: astore_1
    //   41: ldc -113
    //   43: aload_1
    //   44: invokevirtual 149	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   47: istore_3
    //   48: iload_3
    //   49: ifeq +119 -> 168
    //   52: aload_2
    //   53: ifnonnull +58 -> 111
    //   56: aconst_null
    //   57: astore_1
    //   58: aload_0
    //   59: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   62: invokevirtual 111	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   65: invokevirtual 152	com/google/android/gms/internal/zzchm:zzazj	()Lcom/google/android/gms/internal/zzcho;
    //   68: ldc -102
    //   70: invokevirtual 119	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   73: aload_1
    //   74: ifnonnull +115 -> 189
    //   77: aload_0
    //   78: iconst_0
    //   79: putfield 28	com/google/android/gms/internal/zzcku:zzjiq	Z
    //   82: invokestatic 160	com/google/android/gms/common/stats/zza:zzamc	()Lcom/google/android/gms/common/stats/zza;
    //   85: pop
    //   86: aload_0
    //   87: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   90: invokevirtual 164	com/google/android/gms/internal/zzcjk:getContext	()Landroid/content/Context;
    //   93: aload_0
    //   94: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   97: invokestatic 167	com/google/android/gms/internal/zzckg:zza	(Lcom/google/android/gms/internal/zzckg;)Lcom/google/android/gms/internal/zzcku;
    //   100: invokevirtual 173	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   103: aload_0
    //   104: monitorexit
    //   105: return
    //   106: astore_1
    //   107: aload_0
    //   108: monitorexit
    //   109: aload_1
    //   110: athrow
    //   111: aload_2
    //   112: ldc -113
    //   114: invokeinterface 177 2 0
    //   119: astore_1
    //   120: aload_1
    //   121: instanceof 52
    //   124: ifeq +11 -> 135
    //   127: aload_1
    //   128: checkcast 52	com/google/android/gms/internal/zzche
    //   131: astore_1
    //   132: goto -74 -> 58
    //   135: new 179	com/google/android/gms/internal/zzchg
    //   138: dup
    //   139: aload_2
    //   140: invokespecial 182	com/google/android/gms/internal/zzchg:<init>	(Landroid/os/IBinder;)V
    //   143: astore_1
    //   144: goto -86 -> 58
    //   147: astore_1
    //   148: aconst_null
    //   149: astore_1
    //   150: aload_0
    //   151: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   154: invokevirtual 111	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   157: invokevirtual 133	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   160: ldc -72
    //   162: invokevirtual 119	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   165: goto -92 -> 73
    //   168: aload_0
    //   169: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   172: invokevirtual 111	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   175: invokevirtual 133	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   178: ldc -70
    //   180: aload_1
    //   181: invokevirtual 99	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   184: aconst_null
    //   185: astore_1
    //   186: goto -113 -> 73
    //   189: aload_0
    //   190: getfield 20	com/google/android/gms/internal/zzcku:zzjij	Lcom/google/android/gms/internal/zzckg;
    //   193: invokevirtual 58	com/google/android/gms/internal/zzcjk:zzawx	()Lcom/google/android/gms/internal/zzcih;
    //   196: new 188	com/google/android/gms/internal/zzckv
    //   199: dup
    //   200: aload_0
    //   201: aload_1
    //   202: invokespecial 189	com/google/android/gms/internal/zzckv:<init>	(Lcom/google/android/gms/internal/zzcku;Lcom/google/android/gms/internal/zzche;)V
    //   205: invokevirtual 69	com/google/android/gms/internal/zzcih:zzg	(Ljava/lang/Runnable;)V
    //   208: goto -105 -> 103
    //   211: astore_1
    //   212: goto -109 -> 103
    //   215: astore_2
    //   216: goto -66 -> 150
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	219	0	this	zzcku
    //   0	219	1	paramComponentName	ComponentName
    //   0	219	2	paramIBinder	android.os.IBinder
    //   47	2	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   11	33	106	finally
    //   34	48	106	finally
    //   58	73	106	finally
    //   77	82	106	finally
    //   82	103	106	finally
    //   103	105	106	finally
    //   107	109	106	finally
    //   111	132	106	finally
    //   135	144	106	finally
    //   150	165	106	finally
    //   168	184	106	finally
    //   189	208	106	finally
    //   34	48	147	android/os/RemoteException
    //   111	132	147	android/os/RemoteException
    //   135	144	147	android/os/RemoteException
    //   168	184	147	android/os/RemoteException
    //   82	103	211	java/lang/IllegalArgumentException
    //   58	73	215	android/os/RemoteException
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    zzbq.zzge("MeasurementServiceConnection.onServiceDisconnected");
    this.zzjij.zzawy().zzazi().log("Service disconnected");
    this.zzjij.zzawx().zzg(new zzckw(this, paramComponentName));
  }
  
  public final void zzbau()
  {
    this.zzjij.zzve();
    Context localContext1 = this.zzjij.getContext();
    try
    {
      if (this.zzjiq)
      {
        this.zzjij.zzawy().zzazj().log("Connection attempt already in progress");
        return;
      }
      if (this.zzjir != null)
      {
        this.zzjij.zzawy().zzazj().log("Already awaiting connection attempt");
        return;
      }
    }
    finally {}
    this.zzjir = new zzchl(localContext2, Looper.getMainLooper(), this, this);
    this.zzjij.zzawy().zzazj().log("Connecting to remote service");
    this.zzjiq = true;
    this.zzjir.zzakj();
  }
  
  public final void zzn(Intent paramIntent)
  {
    this.zzjij.zzve();
    Context localContext = this.zzjij.getContext();
    zza localzza = zza.zzamc();
    try
    {
      if (this.zzjiq)
      {
        this.zzjij.zzawy().zzazj().log("Connection attempt already in progress");
        return;
      }
      this.zzjij.zzawy().zzazj().log("Using local app measurement service");
      this.zzjiq = true;
      localzza.zza(localContext, paramIntent, zzckg.zza(this.zzjij), 129);
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcku.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */