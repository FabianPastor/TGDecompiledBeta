package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemClock;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.zzf;
import com.google.android.gms.internal.zzfo;
import com.google.android.gms.internal.zzfp;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AdvertisingIdClient
{
  private final Context mContext;
  private com.google.android.gms.common.zza zzamu;
  private zzfo zzamv;
  private boolean zzamw;
  private Object zzamx = new Object();
  private zza zzamy;
  private boolean zzamz;
  private long zzana;
  
  public AdvertisingIdClient(Context paramContext, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    zzbq.checkNotNull(paramContext);
    Context localContext;
    if (paramBoolean1)
    {
      localContext = paramContext.getApplicationContext();
      if (localContext != null) {}
    }
    for (this.mContext = paramContext;; this.mContext = paramContext)
    {
      this.zzamw = false;
      this.zzana = paramLong;
      this.zzamz = paramBoolean2;
      return;
      paramContext = localContext;
      break;
    }
  }
  
  public static Info getAdvertisingIdInfo(Context paramContext)
    throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    Object localObject2 = new zzb(paramContext);
    boolean bool = ((zzb)localObject2).getBoolean("gads:ad_id_app_context:enabled", false);
    float f = ((zzb)localObject2).getFloat("gads:ad_id_app_context:ping_ratio", 0.0F);
    String str = ((zzb)localObject2).getString("gads:ad_id_use_shared_preference:experiment_id", "");
    paramContext = new AdvertisingIdClient(paramContext, -1L, bool, ((zzb)localObject2).getBoolean("gads:ad_id_use_persistent_service:enabled", false));
    try
    {
      long l = SystemClock.elapsedRealtime();
      paramContext.start(false);
      localObject2 = paramContext.getInfo();
      paramContext.zza((Info)localObject2, bool, f, SystemClock.elapsedRealtime() - l, str, null);
      return (Info)localObject2;
    }
    catch (Throwable localThrowable)
    {
      paramContext.zza(null, bool, f, -1L, str, localThrowable);
      throw localThrowable;
    }
    finally
    {
      paramContext.finish();
    }
  }
  
  public static void setShouldSkipGmsCoreVersionCheck(boolean paramBoolean) {}
  
  private final void start(boolean paramBoolean)
    throws IOException, IllegalStateException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    zzbq.zzgn("Calling this from your main thread can lead to deadlock");
    try
    {
      if (this.zzamw) {
        finish();
      }
      this.zzamu = zzc(this.mContext, this.zzamz);
      this.zzamv = zza(this.mContext, this.zzamu);
      this.zzamw = true;
      if (paramBoolean) {
        zzbo();
      }
      return;
    }
    finally {}
  }
  
  private static zzfo zza(Context paramContext, com.google.android.gms.common.zza paramzza)
    throws IOException
  {
    try
    {
      paramContext = zzfp.zzc(paramzza.zza(10000L, TimeUnit.MILLISECONDS));
      return paramContext;
    }
    catch (InterruptedException paramContext)
    {
      throw new IOException("Interrupted exception");
    }
    catch (Throwable paramContext)
    {
      throw new IOException(paramContext);
    }
  }
  
  private final boolean zza(Info paramInfo, boolean paramBoolean, float paramFloat, long paramLong, String paramString, Throwable paramThrowable)
  {
    if (Math.random() > paramFloat) {
      return false;
    }
    HashMap localHashMap = new HashMap();
    if (paramBoolean)
    {
      str = "1";
      localHashMap.put("app_context", str);
      if (paramInfo != null) {
        if (!paramInfo.isLimitAdTrackingEnabled()) {
          break label195;
        }
      }
    }
    label195:
    for (String str = "1";; str = "0")
    {
      localHashMap.put("limit_ad_tracking", str);
      if ((paramInfo != null) && (paramInfo.getId() != null)) {
        localHashMap.put("ad_id_size", Integer.toString(paramInfo.getId().length()));
      }
      if (paramThrowable != null) {
        localHashMap.put("error", paramThrowable.getClass().getName());
      }
      if ((paramString != null) && (!paramString.isEmpty())) {
        localHashMap.put("experiment_id", paramString);
      }
      localHashMap.put("tag", "AdvertisingIdClient");
      localHashMap.put("time_spent", Long.toString(paramLong));
      new zza(this, localHashMap).start();
      return true;
      str = "0";
      break;
    }
  }
  
  private final void zzbo()
  {
    synchronized (this.zzamx)
    {
      if (this.zzamy != null) {
        this.zzamy.zzane.countDown();
      }
    }
    try
    {
      this.zzamy.join();
      if (this.zzana > 0L) {
        this.zzamy = new zza(this, this.zzana);
      }
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
  }
  
  private static com.google.android.gms.common.zza zzc(Context paramContext, boolean paramBoolean)
    throws IOException, GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException
  {
    try
    {
      paramContext.getPackageManager().getPackageInfo("com.android.vending", 0);
      switch (zzf.zzafy().isGooglePlayServicesAvailable(paramContext))
      {
      case 1: 
      default: 
        throw new IOException("Google Play services not available");
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      throw new GooglePlayServicesNotAvailableException(9);
    }
    if (paramBoolean) {}
    for (Object localObject = "com.google.android.gms.ads.identifier.service.PERSISTENT_START";; localObject = "com.google.android.gms.ads.identifier.service.START")
    {
      com.google.android.gms.common.zza localzza = new com.google.android.gms.common.zza();
      localObject = new Intent((String)localObject);
      ((Intent)localObject).setPackage("com.google.android.gms");
      try
      {
        paramBoolean = com.google.android.gms.common.stats.zza.zzamc().zza(paramContext, (Intent)localObject, localzza, 1);
        if (!paramBoolean) {
          break;
        }
        return localzza;
      }
      catch (Throwable paramContext)
      {
        throw new IOException(paramContext);
      }
    }
    throw new IOException("Connection failure");
  }
  
  protected void finalize()
    throws Throwable
  {
    finish();
    super.finalize();
  }
  
  /* Error */
  public void finish()
  {
    // Byte code:
    //   0: ldc 120
    //   2: invokestatic 124	com/google/android/gms/common/internal/zzbq:zzgn	(Ljava/lang/String;)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 46	com/google/android/gms/ads/identifier/AdvertisingIdClient:mContext	Landroid/content/Context;
    //   11: ifnull +10 -> 21
    //   14: aload_0
    //   15: getfield 130	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamu	Lcom/google/android/gms/common/zza;
    //   18: ifnonnull +6 -> 24
    //   21: aload_0
    //   22: monitorexit
    //   23: return
    //   24: aload_0
    //   25: getfield 48	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamw	Z
    //   28: ifeq +18 -> 46
    //   31: invokestatic 308	com/google/android/gms/common/stats/zza:zzamc	()Lcom/google/android/gms/common/stats/zza;
    //   34: pop
    //   35: aload_0
    //   36: getfield 46	com/google/android/gms/ads/identifier/AdvertisingIdClient:mContext	Landroid/content/Context;
    //   39: aload_0
    //   40: getfield 130	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamu	Lcom/google/android/gms/common/zza;
    //   43: invokevirtual 322	android/content/Context:unbindService	(Landroid/content/ServiceConnection;)V
    //   46: aload_0
    //   47: iconst_0
    //   48: putfield 48	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamw	Z
    //   51: aload_0
    //   52: aconst_null
    //   53: putfield 135	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamv	Lcom/google/android/gms/internal/zzfo;
    //   56: aload_0
    //   57: aconst_null
    //   58: putfield 130	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamu	Lcom/google/android/gms/common/zza;
    //   61: aload_0
    //   62: monitorexit
    //   63: return
    //   64: astore_1
    //   65: aload_0
    //   66: monitorexit
    //   67: aload_1
    //   68: athrow
    //   69: astore_1
    //   70: ldc -28
    //   72: ldc_w 324
    //   75: aload_1
    //   76: invokestatic 330	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   79: pop
    //   80: goto -34 -> 46
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	83	0	this	AdvertisingIdClient
    //   64	4	1	localObject	Object
    //   69	7	1	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   7	21	64	finally
    //   21	23	64	finally
    //   24	46	64	finally
    //   46	63	64	finally
    //   65	67	64	finally
    //   70	80	64	finally
    //   24	46	69	java/lang/Throwable
  }
  
  /* Error */
  public Info getInfo()
    throws IOException
  {
    // Byte code:
    //   0: ldc 120
    //   2: invokestatic 124	com/google/android/gms/common/internal/zzbq:zzgn	(Ljava/lang/String;)V
    //   5: aload_0
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 48	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamw	Z
    //   11: ifne +86 -> 97
    //   14: aload_0
    //   15: getfield 32	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamx	Ljava/lang/Object;
    //   18: astore_1
    //   19: aload_1
    //   20: monitorenter
    //   21: aload_0
    //   22: getfield 246	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamy	Lcom/google/android/gms/ads/identifier/AdvertisingIdClient$zza;
    //   25: ifnull +13 -> 38
    //   28: aload_0
    //   29: getfield 246	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamy	Lcom/google/android/gms/ads/identifier/AdvertisingIdClient$zza;
    //   32: getfield 337	com/google/android/gms/ads/identifier/AdvertisingIdClient$zza:zzanf	Z
    //   35: ifne +24 -> 59
    //   38: new 57	java/io/IOException
    //   41: dup
    //   42: ldc_w 339
    //   45: invokespecial 162	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   48: athrow
    //   49: astore_2
    //   50: aload_1
    //   51: monitorexit
    //   52: aload_2
    //   53: athrow
    //   54: astore_1
    //   55: aload_0
    //   56: monitorexit
    //   57: aload_1
    //   58: athrow
    //   59: aload_1
    //   60: monitorexit
    //   61: aload_0
    //   62: iconst_0
    //   63: invokespecial 106	com/google/android/gms/ads/identifier/AdvertisingIdClient:start	(Z)V
    //   66: aload_0
    //   67: getfield 48	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamw	Z
    //   70: ifne +27 -> 97
    //   73: new 57	java/io/IOException
    //   76: dup
    //   77: ldc_w 341
    //   80: invokespecial 162	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   83: athrow
    //   84: astore_1
    //   85: new 57	java/io/IOException
    //   88: dup
    //   89: ldc_w 341
    //   92: aload_1
    //   93: invokespecial 344	java/io/IOException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   96: athrow
    //   97: aload_0
    //   98: getfield 130	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamu	Lcom/google/android/gms/common/zza;
    //   101: invokestatic 38	com/google/android/gms/common/internal/zzbq:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   104: pop
    //   105: aload_0
    //   106: getfield 135	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamv	Lcom/google/android/gms/internal/zzfo;
    //   109: invokestatic 38	com/google/android/gms/common/internal/zzbq:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   112: pop
    //   113: new 6	com/google/android/gms/ads/identifier/AdvertisingIdClient$Info
    //   116: dup
    //   117: aload_0
    //   118: getfield 135	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamv	Lcom/google/android/gms/internal/zzfo;
    //   121: invokeinterface 347 1 0
    //   126: aload_0
    //   127: getfield 135	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzamv	Lcom/google/android/gms/internal/zzfo;
    //   130: iconst_1
    //   131: invokeinterface 351 2 0
    //   136: invokespecial 354	com/google/android/gms/ads/identifier/AdvertisingIdClient$Info:<init>	(Ljava/lang/String;Z)V
    //   139: astore_1
    //   140: aload_0
    //   141: monitorexit
    //   142: aload_0
    //   143: invokespecial 138	com/google/android/gms/ads/identifier/AdvertisingIdClient:zzbo	()V
    //   146: aload_1
    //   147: areturn
    //   148: astore_1
    //   149: ldc -28
    //   151: ldc_w 356
    //   154: aload_1
    //   155: invokestatic 330	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   158: pop
    //   159: new 57	java/io/IOException
    //   162: dup
    //   163: ldc_w 358
    //   166: invokespecial 162	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   169: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	AdvertisingIdClient
    //   54	6	1	localObject2	Object
    //   84	9	1	localException	Exception
    //   139	8	1	localInfo	Info
    //   148	7	1	localRemoteException	android.os.RemoteException
    //   49	4	2	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   21	38	49	finally
    //   38	49	49	finally
    //   50	52	49	finally
    //   59	61	49	finally
    //   7	21	54	finally
    //   52	54	54	finally
    //   55	57	54	finally
    //   61	66	54	finally
    //   66	84	54	finally
    //   85	97	54	finally
    //   97	113	54	finally
    //   113	140	54	finally
    //   140	142	54	finally
    //   149	170	54	finally
    //   61	66	84	java/lang/Exception
    //   113	140	148	android/os/RemoteException
  }
  
  public static final class Info
  {
    private final String zzang;
    private final boolean zzanh;
    
    public Info(String paramString, boolean paramBoolean)
    {
      this.zzang = paramString;
      this.zzanh = paramBoolean;
    }
    
    public final String getId()
    {
      return this.zzang;
    }
    
    public final boolean isLimitAdTrackingEnabled()
    {
      return this.zzanh;
    }
    
    public final String toString()
    {
      String str = this.zzang;
      boolean bool = this.zzanh;
      return String.valueOf(str).length() + 7 + "{" + str + "}" + bool;
    }
  }
  
  static final class zza
    extends Thread
  {
    private WeakReference<AdvertisingIdClient> zzanc;
    private long zzand;
    CountDownLatch zzane;
    boolean zzanf;
    
    public zza(AdvertisingIdClient paramAdvertisingIdClient, long paramLong)
    {
      this.zzanc = new WeakReference(paramAdvertisingIdClient);
      this.zzand = paramLong;
      this.zzane = new CountDownLatch(1);
      this.zzanf = false;
      start();
    }
    
    private final void disconnect()
    {
      AdvertisingIdClient localAdvertisingIdClient = (AdvertisingIdClient)this.zzanc.get();
      if (localAdvertisingIdClient != null)
      {
        localAdvertisingIdClient.finish();
        this.zzanf = true;
      }
    }
    
    public final void run()
    {
      try
      {
        if (!this.zzane.await(this.zzand, TimeUnit.MILLISECONDS)) {
          disconnect();
        }
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        disconnect();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/ads/identifier/AdvertisingIdClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */