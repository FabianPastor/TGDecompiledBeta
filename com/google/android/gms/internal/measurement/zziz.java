package com.google.android.gms.internal.measurement;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.BaseGmsClient.BaseConnectionCallbacks;
import com.google.android.gms.common.internal.BaseGmsClient.BaseOnConnectionFailedListener;
import com.google.android.gms.common.internal.Preconditions;

public final class zziz
  implements ServiceConnection, BaseGmsClient.BaseConnectionCallbacks, BaseGmsClient.BaseOnConnectionFailedListener
{
  private volatile boolean zzaqf;
  private volatile zzff zzaqg;
  
  protected zziz(zzil paramzzil) {}
  
  /* Error */
  public final void onConnected(android.os.Bundle paramBundle)
  {
    // Byte code:
    //   0: ldc 36
    //   2: invokestatic 42	com/google/android/gms/common/internal/Preconditions:checkMainThread	(Ljava/lang/String;)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 44	com/google/android/gms/internal/measurement/zziz:zzaqg	Lcom/google/android/gms/internal/measurement/zzff;
    //   11: invokevirtual 50	com/google/android/gms/internal/measurement/zzff:getService	()Landroid/os/IInterface;
    //   14: checkcast 52	com/google/android/gms/internal/measurement/zzey
    //   17: astore_1
    //   18: aload_0
    //   19: aconst_null
    //   20: putfield 44	com/google/android/gms/internal/measurement/zziz:zzaqg	Lcom/google/android/gms/internal/measurement/zzff;
    //   23: aload_0
    //   24: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   27: invokevirtual 58	com/google/android/gms/internal/measurement/zzhj:zzgf	()Lcom/google/android/gms/internal/measurement/zzgg;
    //   30: astore_2
    //   31: new 60	com/google/android/gms/internal/measurement/zzjc
    //   34: astore_3
    //   35: aload_3
    //   36: aload_0
    //   37: aload_1
    //   38: invokespecial 63	com/google/android/gms/internal/measurement/zzjc:<init>	(Lcom/google/android/gms/internal/measurement/zziz;Lcom/google/android/gms/internal/measurement/zzey;)V
    //   41: aload_2
    //   42: aload_3
    //   43: invokevirtual 69	com/google/android/gms/internal/measurement/zzgg:zzc	(Ljava/lang/Runnable;)V
    //   46: aload_0
    //   47: monitorexit
    //   48: return
    //   49: astore_1
    //   50: aload_0
    //   51: aconst_null
    //   52: putfield 44	com/google/android/gms/internal/measurement/zziz:zzaqg	Lcom/google/android/gms/internal/measurement/zzff;
    //   55: aload_0
    //   56: iconst_0
    //   57: putfield 28	com/google/android/gms/internal/measurement/zziz:zzaqf	Z
    //   60: goto -14 -> 46
    //   63: astore_1
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_1
    //   67: athrow
    //   68: astore_1
    //   69: goto -19 -> 50
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	zziz
    //   0	72	1	paramBundle	android.os.Bundle
    //   30	12	2	localzzgg	zzgg
    //   34	9	3	localzzjc	zzjc
    // Exception table:
    //   from	to	target	type
    //   7	46	49	java/lang/IllegalStateException
    //   7	46	63	finally
    //   46	48	63	finally
    //   50	60	63	finally
    //   64	66	63	finally
    //   7	46	68	android/os/DeadObjectException
  }
  
  public final void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    Preconditions.checkMainThread("MeasurementServiceConnection.onConnectionFailed");
    zzfg localzzfg = this.zzapy.zzacr.zzjl();
    if (localzzfg != null) {
      localzzfg.zzin().zzg("Service connection failed", paramConnectionResult);
    }
    try
    {
      this.zzaqf = false;
      this.zzaqg = null;
      this.zzapy.zzgf().zzc(new zzje(this));
      return;
    }
    finally {}
  }
  
  public final void onConnectionSuspended(int paramInt)
  {
    Preconditions.checkMainThread("MeasurementServiceConnection.onConnectionSuspended");
    this.zzapy.zzgg().zziq().log("Service connection suspended");
    this.zzapy.zzgf().zzc(new zzjd(this));
  }
  
  /* Error */
  public final void onServiceConnected(ComponentName paramComponentName, android.os.IBinder paramIBinder)
  {
    // Byte code:
    //   0: ldc -126
    //   2: invokestatic 42	com/google/android/gms/common/internal/Preconditions:checkMainThread	(Ljava/lang/String;)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_2
    //   8: ifnonnull +26 -> 34
    //   11: aload_0
    //   12: iconst_0
    //   13: putfield 28	com/google/android/gms/internal/measurement/zziz:zzaqf	Z
    //   16: aload_0
    //   17: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   20: invokevirtual 111	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   23: invokevirtual 133	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   26: ldc -121
    //   28: invokevirtual 119	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
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
    //   49: ifeq +120 -> 169
    //   52: aload_2
    //   53: ifnonnull +59 -> 112
    //   56: aconst_null
    //   57: astore_1
    //   58: aload_0
    //   59: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   62: invokevirtual 111	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   65: invokevirtual 152	com/google/android/gms/internal/measurement/zzfg:zzir	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   68: ldc -102
    //   70: invokevirtual 119	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   73: aload_1
    //   74: ifnonnull +116 -> 190
    //   77: aload_0
    //   78: iconst_0
    //   79: putfield 28	com/google/android/gms/internal/measurement/zziz:zzaqf	Z
    //   82: invokestatic 160	com/google/android/gms/common/stats/ConnectionTracker:getInstance	()Lcom/google/android/gms/common/stats/ConnectionTracker;
    //   85: aload_0
    //   86: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   89: invokevirtual 164	com/google/android/gms/internal/measurement/zzhj:getContext	()Landroid/content/Context;
    //   92: aload_0
    //   93: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   96: invokestatic 167	com/google/android/gms/internal/measurement/zzil:zza	(Lcom/google/android/gms/internal/measurement/zzil;)Lcom/google/android/gms/internal/measurement/zziz;
    //   99: invokevirtual 171	com/google/android/gms/common/stats/ConnectionTracker:unbindService	(Landroid/content/Context;Landroid/content/ServiceConnection;)V
    //   102: aload_0
    //   103: monitorexit
    //   104: goto -71 -> 33
    //   107: astore_1
    //   108: aload_0
    //   109: monitorexit
    //   110: aload_1
    //   111: athrow
    //   112: aload_2
    //   113: ldc -113
    //   115: invokeinterface 175 2 0
    //   120: astore_1
    //   121: aload_1
    //   122: instanceof 52
    //   125: ifeq +11 -> 136
    //   128: aload_1
    //   129: checkcast 52	com/google/android/gms/internal/measurement/zzey
    //   132: astore_1
    //   133: goto -75 -> 58
    //   136: new 177	com/google/android/gms/internal/measurement/zzfa
    //   139: dup
    //   140: aload_2
    //   141: invokespecial 180	com/google/android/gms/internal/measurement/zzfa:<init>	(Landroid/os/IBinder;)V
    //   144: astore_1
    //   145: goto -87 -> 58
    //   148: astore_1
    //   149: aconst_null
    //   150: astore_1
    //   151: aload_0
    //   152: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   155: invokevirtual 111	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   158: invokevirtual 133	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   161: ldc -74
    //   163: invokevirtual 119	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   166: goto -93 -> 73
    //   169: aload_0
    //   170: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   173: invokevirtual 111	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   176: invokevirtual 133	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   179: ldc -72
    //   181: aload_1
    //   182: invokevirtual 99	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   185: aconst_null
    //   186: astore_1
    //   187: goto -114 -> 73
    //   190: aload_0
    //   191: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   194: invokevirtual 58	com/google/android/gms/internal/measurement/zzhj:zzgf	()Lcom/google/android/gms/internal/measurement/zzgg;
    //   197: astore_2
    //   198: new 186	com/google/android/gms/internal/measurement/zzja
    //   201: astore 4
    //   203: aload 4
    //   205: aload_0
    //   206: aload_1
    //   207: invokespecial 187	com/google/android/gms/internal/measurement/zzja:<init>	(Lcom/google/android/gms/internal/measurement/zziz;Lcom/google/android/gms/internal/measurement/zzey;)V
    //   210: aload_2
    //   211: aload 4
    //   213: invokevirtual 69	com/google/android/gms/internal/measurement/zzgg:zzc	(Ljava/lang/Runnable;)V
    //   216: goto -114 -> 102
    //   219: astore_1
    //   220: goto -118 -> 102
    //   223: astore_2
    //   224: goto -73 -> 151
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	227	0	this	zziz
    //   0	227	1	paramComponentName	ComponentName
    //   0	227	2	paramIBinder	android.os.IBinder
    //   47	2	3	bool	boolean
    //   201	11	4	localzzja	zzja
    // Exception table:
    //   from	to	target	type
    //   11	33	107	finally
    //   34	48	107	finally
    //   58	73	107	finally
    //   77	82	107	finally
    //   82	102	107	finally
    //   102	104	107	finally
    //   108	110	107	finally
    //   112	133	107	finally
    //   136	145	107	finally
    //   151	166	107	finally
    //   169	185	107	finally
    //   190	216	107	finally
    //   34	48	148	android/os/RemoteException
    //   112	133	148	android/os/RemoteException
    //   136	145	148	android/os/RemoteException
    //   169	185	148	android/os/RemoteException
    //   82	102	219	java/lang/IllegalArgumentException
    //   58	73	223	android/os/RemoteException
  }
  
  public final void onServiceDisconnected(ComponentName paramComponentName)
  {
    Preconditions.checkMainThread("MeasurementServiceConnection.onServiceDisconnected");
    this.zzapy.zzgg().zziq().log("Service disconnected");
    this.zzapy.zzgf().zzc(new zzjb(this, paramComponentName));
  }
  
  /* Error */
  public final void zzc(android.content.Intent paramIntent)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   4: invokevirtual 202	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   7: aload_0
    //   8: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   11: invokevirtual 164	com/google/android/gms/internal/measurement/zzhj:getContext	()Landroid/content/Context;
    //   14: astore_2
    //   15: invokestatic 160	com/google/android/gms/common/stats/ConnectionTracker:getInstance	()Lcom/google/android/gms/common/stats/ConnectionTracker;
    //   18: astore_3
    //   19: aload_0
    //   20: monitorenter
    //   21: aload_0
    //   22: getfield 28	com/google/android/gms/internal/measurement/zziz:zzaqf	Z
    //   25: ifeq +21 -> 46
    //   28: aload_0
    //   29: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   32: invokevirtual 111	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   35: invokevirtual 152	com/google/android/gms/internal/measurement/zzfg:zzir	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   38: ldc -52
    //   40: invokevirtual 119	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   43: aload_0
    //   44: monitorexit
    //   45: return
    //   46: aload_0
    //   47: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   50: invokevirtual 111	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   53: invokevirtual 152	com/google/android/gms/internal/measurement/zzfg:zzir	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   56: ldc -50
    //   58: invokevirtual 119	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   61: aload_0
    //   62: iconst_1
    //   63: putfield 28	com/google/android/gms/internal/measurement/zziz:zzaqf	Z
    //   66: aload_3
    //   67: aload_2
    //   68: aload_1
    //   69: aload_0
    //   70: getfield 20	com/google/android/gms/internal/measurement/zziz:zzapy	Lcom/google/android/gms/internal/measurement/zzil;
    //   73: invokestatic 167	com/google/android/gms/internal/measurement/zzil:zza	(Lcom/google/android/gms/internal/measurement/zzil;)Lcom/google/android/gms/internal/measurement/zziz;
    //   76: sipush 129
    //   79: invokevirtual 210	com/google/android/gms/common/stats/ConnectionTracker:bindService	(Landroid/content/Context;Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z
    //   82: pop
    //   83: aload_0
    //   84: monitorexit
    //   85: goto -40 -> 45
    //   88: astore_1
    //   89: aload_0
    //   90: monitorexit
    //   91: aload_1
    //   92: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	zziz
    //   0	93	1	paramIntent	android.content.Intent
    //   14	54	2	localContext	Context
    //   18	49	3	localConnectionTracker	com.google.android.gms.common.stats.ConnectionTracker
    // Exception table:
    //   from	to	target	type
    //   21	45	88	finally
    //   46	85	88	finally
    //   89	91	88	finally
  }
  
  public final void zzkp()
  {
    this.zzapy.zzab();
    Context localContext1 = this.zzapy.getContext();
    for (;;)
    {
      try
      {
        if (this.zzaqf)
        {
          this.zzapy.zzgg().zzir().log("Connection attempt already in progress");
          return;
        }
        if (this.zzaqg != null)
        {
          this.zzapy.zzgg().zzir().log("Already awaiting connection attempt");
          continue;
        }
        localzzff = new com/google/android/gms/internal/measurement/zzff;
      }
      finally {}
      zzff localzzff;
      localzzff.<init>(localContext2, Looper.getMainLooper(), this, this);
      this.zzaqg = localzzff;
      this.zzapy.zzgg().zzir().log("Connecting to remote service");
      this.zzaqf = true;
      this.zzaqg.checkAvailabilityAndConnect();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zziz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */