package com.google.firebase.iid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import java.io.IOException;

final class zzac
  implements Runnable
{
  private final zzw zzbqn;
  private final long zzbsc;
  private final PowerManager.WakeLock zzbsd;
  private final FirebaseInstanceId zzbse;
  
  zzac(FirebaseInstanceId paramFirebaseInstanceId, zzw paramzzw, long paramLong)
  {
    this.zzbse = paramFirebaseInstanceId;
    this.zzbqn = paramzzw;
    this.zzbsc = paramLong;
    this.zzbsd = ((PowerManager)getContext().getSystemService("power")).newWakeLock(1, "fiid-sync");
    this.zzbsd.setReferenceCounted(false);
  }
  
  private final boolean zzfg(String paramString)
  {
    boolean bool1 = true;
    Object localObject = paramString.split("!");
    boolean bool2 = bool1;
    int i;
    int j;
    if (localObject.length == 2)
    {
      paramString = localObject[0];
      localObject = localObject[1];
      i = -1;
      j = i;
    }
    try
    {
      switch (paramString.hashCode())
      {
      default: 
        j = i;
      case 84: 
        switch (j)
        {
        default: 
          bool2 = bool1;
        }
        break;
      }
      for (;;)
      {
        return bool2;
        j = i;
        if (!paramString.equals("S")) {
          break;
        }
        j = 0;
        break;
        j = i;
        if (!paramString.equals("U")) {
          break;
        }
        j = 1;
        break;
        this.zzbse.zzew((String)localObject);
        bool2 = bool1;
        if (FirebaseInstanceId.zzsj())
        {
          Log.d("FirebaseInstanceId", "subscribe operation succeeded");
          bool2 = bool1;
        }
      }
      paramString = "Topic sync failed: ".concat(paramString);
    }
    catch (IOException paramString)
    {
      paramString = String.valueOf(paramString.getMessage());
      if (paramString.length() == 0) {}
    }
    for (;;)
    {
      Log.e("FirebaseInstanceId", paramString);
      bool2 = false;
      break;
      this.zzbse.zzex((String)localObject);
      bool2 = bool1;
      if (!FirebaseInstanceId.zzsj()) {
        break;
      }
      Log.d("FirebaseInstanceId", "unsubscribe operation succeeded");
      bool2 = bool1;
      break;
      paramString = new String("Topic sync failed: ");
    }
  }
  
  private final boolean zzte()
  {
    boolean bool1 = true;
    Object localObject1 = this.zzbse.zzsg();
    boolean bool2;
    if ((localObject1 != null) && (!((zzab)localObject1).zzff(this.zzbqn.zzsv()))) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      try
      {
        Object localObject2 = this.zzbse.zzsh();
        if (localObject2 == null)
        {
          Log.e("FirebaseInstanceId", "Token retrieval failed: null");
          bool2 = false;
          continue;
        }
        if (Log.isLoggable("FirebaseInstanceId", 3)) {
          Log.d("FirebaseInstanceId", "Token successfully retrieved");
        }
        if (localObject1 != null)
        {
          bool2 = bool1;
          if (localObject1 == null) {
            continue;
          }
          bool2 = bool1;
          if (((String)localObject2).equals(((zzab)localObject1).zzbsb)) {
            continue;
          }
        }
        Context localContext = getContext();
        localObject1 = new android/content/Intent;
        ((Intent)localObject1).<init>("com.google.firebase.iid.TOKEN_REFRESH");
        localObject2 = new android/content/Intent;
        ((Intent)localObject2).<init>("com.google.firebase.INSTANCE_ID_EVENT");
        ((Intent)localObject2).setClass(localContext, FirebaseInstanceIdReceiver.class);
        ((Intent)localObject2).putExtra("wrapped_intent", (Parcelable)localObject1);
        localContext.sendBroadcast((Intent)localObject2);
        bool2 = bool1;
      }
      catch (IOException localIOException)
      {
        String str = String.valueOf(localIOException.getMessage());
        if (str.length() != 0) {}
        for (str = "Token retrieval failed: ".concat(str);; str = new String("Token retrieval failed: "))
        {
          Log.e("FirebaseInstanceId", str);
          bool2 = false;
          break;
        }
      }
      catch (SecurityException localSecurityException)
      {
        for (;;) {}
      }
    }
  }
  
  private final boolean zztf()
  {
    for (;;)
    {
      synchronized (this.zzbse)
      {
        String str1 = FirebaseInstanceId.zzsi().zztc();
        boolean bool;
        if (str1 == null)
        {
          Log.d("FirebaseInstanceId", "topic sync succeeded");
          bool = true;
          return bool;
        }
        if (!zzfg(str1)) {
          bool = false;
        }
      }
      FirebaseInstanceId.zzsi().zzez(str2);
    }
  }
  
  final Context getContext()
  {
    return this.zzbse.zzsf().getApplicationContext();
  }
  
  /* Error */
  public final void run()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 47	com/google/firebase/iid/zzac:zzbsd	Landroid/os/PowerManager$WakeLock;
    //   6: invokevirtual 215	android/os/PowerManager$WakeLock:acquire	()V
    //   9: aload_0
    //   10: getfield 21	com/google/firebase/iid/zzac:zzbse	Lcom/google/firebase/iid/FirebaseInstanceId;
    //   13: iconst_1
    //   14: invokevirtual 218	com/google/firebase/iid/FirebaseInstanceId:zzu	(Z)V
    //   17: aload_0
    //   18: getfield 23	com/google/firebase/iid/zzac:zzbqn	Lcom/google/firebase/iid/zzw;
    //   21: invokevirtual 221	com/google/firebase/iid/zzw:zzsu	()I
    //   24: ifeq +23 -> 47
    //   27: iload_1
    //   28: ifne +24 -> 52
    //   31: aload_0
    //   32: getfield 21	com/google/firebase/iid/zzac:zzbse	Lcom/google/firebase/iid/FirebaseInstanceId;
    //   35: iconst_0
    //   36: invokevirtual 218	com/google/firebase/iid/FirebaseInstanceId:zzu	(Z)V
    //   39: aload_0
    //   40: getfield 47	com/google/firebase/iid/zzac:zzbsd	Landroid/os/PowerManager$WakeLock;
    //   43: invokevirtual 224	android/os/PowerManager$WakeLock:release	()V
    //   46: return
    //   47: iconst_0
    //   48: istore_1
    //   49: goto -22 -> 27
    //   52: aload_0
    //   53: invokevirtual 227	com/google/firebase/iid/zzac:zztg	()Z
    //   56: ifne +26 -> 82
    //   59: new 229	com/google/firebase/iid/zzad
    //   62: astore_2
    //   63: aload_2
    //   64: aload_0
    //   65: invokespecial 232	com/google/firebase/iid/zzad:<init>	(Lcom/google/firebase/iid/zzac;)V
    //   68: aload_2
    //   69: invokevirtual 235	com/google/firebase/iid/zzad:zzth	()V
    //   72: aload_0
    //   73: getfield 47	com/google/firebase/iid/zzac:zzbsd	Landroid/os/PowerManager$WakeLock;
    //   76: invokevirtual 224	android/os/PowerManager$WakeLock:release	()V
    //   79: goto -33 -> 46
    //   82: aload_0
    //   83: invokespecial 237	com/google/firebase/iid/zzac:zzte	()Z
    //   86: ifeq +28 -> 114
    //   89: aload_0
    //   90: invokespecial 239	com/google/firebase/iid/zzac:zztf	()Z
    //   93: ifeq +21 -> 114
    //   96: aload_0
    //   97: getfield 21	com/google/firebase/iid/zzac:zzbse	Lcom/google/firebase/iid/FirebaseInstanceId;
    //   100: iconst_0
    //   101: invokevirtual 218	com/google/firebase/iid/FirebaseInstanceId:zzu	(Z)V
    //   104: aload_0
    //   105: getfield 47	com/google/firebase/iid/zzac:zzbsd	Landroid/os/PowerManager$WakeLock;
    //   108: invokevirtual 224	android/os/PowerManager$WakeLock:release	()V
    //   111: goto -65 -> 46
    //   114: aload_0
    //   115: getfield 21	com/google/firebase/iid/zzac:zzbse	Lcom/google/firebase/iid/FirebaseInstanceId;
    //   118: aload_0
    //   119: getfield 25	com/google/firebase/iid/zzac:zzbsc	J
    //   122: invokevirtual 243	com/google/firebase/iid/FirebaseInstanceId:zzan	(J)V
    //   125: goto -21 -> 104
    //   128: astore_2
    //   129: aload_0
    //   130: getfield 47	com/google/firebase/iid/zzac:zzbsd	Landroid/os/PowerManager$WakeLock;
    //   133: invokevirtual 224	android/os/PowerManager$WakeLock:release	()V
    //   136: aload_2
    //   137: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	this	zzac
    //   1	48	1	i	int
    //   62	7	2	localzzad	zzad
    //   128	9	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	27	128	finally
    //   31	39	128	finally
    //   52	72	128	finally
    //   82	104	128	finally
    //   114	125	128	finally
  }
  
  final boolean zztg()
  {
    Object localObject = (ConnectivityManager)getContext().getSystemService("connectivity");
    if (localObject != null)
    {
      localObject = ((ConnectivityManager)localObject).getActiveNetworkInfo();
      if ((localObject == null) || (!((NetworkInfo)localObject).isConnected())) {
        break label42;
      }
    }
    label42:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      localObject = null;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */