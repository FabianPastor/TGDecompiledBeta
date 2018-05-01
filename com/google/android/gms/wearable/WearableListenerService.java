package com.google.android.gms.wearable;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.util.UidVerifier;
import com.google.android.gms.wearable.internal.zzah;
import com.google.android.gms.wearable.internal.zzas;
import com.google.android.gms.wearable.internal.zzaw;
import com.google.android.gms.wearable.internal.zzen;
import com.google.android.gms.wearable.internal.zzfe;
import com.google.android.gms.wearable.internal.zzfo;
import com.google.android.gms.wearable.internal.zzhp;
import com.google.android.gms.wearable.internal.zzi;
import java.util.List;

public class WearableListenerService
  extends Service
  implements CapabilityApi.CapabilityListener, ChannelApi.ChannelListener, DataApi.DataListener, MessageApi.MessageListener
{
  public static final String BIND_LISTENER_INTENT_ACTION = "com.google.android.gms.wearable.BIND_LISTENER";
  private ComponentName service;
  private zzc zzad;
  private IBinder zzae;
  private Intent zzaf;
  private Looper zzag;
  private final Object zzah = new Object();
  private boolean zzai;
  private zzas zzaj = new zzas(new zza(null));
  
  public Looper getLooper()
  {
    if (this.zzag == null)
    {
      HandlerThread localHandlerThread = new HandlerThread("WearableListenerService");
      localHandlerThread.start();
      this.zzag = localHandlerThread.getLooper();
    }
    return this.zzag;
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    if ("com.google.android.gms.wearable.BIND_LISTENER".equals(paramIntent.getAction())) {}
    for (paramIntent = this.zzae;; paramIntent = null) {
      return paramIntent;
    }
  }
  
  public void onCapabilityChanged(CapabilityInfo paramCapabilityInfo) {}
  
  public void onChannelClosed(Channel paramChannel, int paramInt1, int paramInt2) {}
  
  public void onChannelClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2) {}
  
  public void onChannelOpened(Channel paramChannel) {}
  
  public void onChannelOpened(ChannelClient.Channel paramChannel) {}
  
  public void onConnectedNodes(List<Node> paramList) {}
  
  public void onCreate()
  {
    super.onCreate();
    this.service = new ComponentName(this, getClass().getName());
    if (Log.isLoggable("WearableLS", 3))
    {
      String str = String.valueOf(this.service);
      Log.d("WearableLS", String.valueOf(str).length() + 10 + "onCreate: " + str);
    }
    this.zzad = new zzc(getLooper());
    this.zzaf = new Intent("com.google.android.gms.wearable.BIND_LISTENER");
    this.zzaf.setComponent(this.service);
    this.zzae = new zzd(null);
  }
  
  public void onDataChanged(DataEventBuffer paramDataEventBuffer) {}
  
  public void onDestroy()
  {
    if (Log.isLoggable("WearableLS", 3))
    {
      ??? = String.valueOf(this.service);
      Log.d("WearableLS", String.valueOf(???).length() + 11 + "onDestroy: " + (String)???);
    }
    synchronized (this.zzah)
    {
      this.zzai = true;
      if (this.zzad == null)
      {
        IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
        String str = String.valueOf(this.service);
        int i = String.valueOf(str).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 111);
        localIllegalStateException.<init>("onDestroy: mServiceHandler not set, did you override onCreate() but forget to call super.onCreate()? component=" + str);
        throw localIllegalStateException;
      }
    }
    this.zzad.quit();
    super.onDestroy();
  }
  
  public void onEntityUpdate(zzb paramzzb) {}
  
  public void onInputClosed(Channel paramChannel, int paramInt1, int paramInt2) {}
  
  public void onInputClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2) {}
  
  public void onMessageReceived(MessageEvent paramMessageEvent) {}
  
  public void onNotificationReceived(zzd paramzzd) {}
  
  public void onOutputClosed(Channel paramChannel, int paramInt1, int paramInt2) {}
  
  public void onOutputClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2) {}
  
  public void onPeerConnected(Node paramNode) {}
  
  public void onPeerDisconnected(Node paramNode) {}
  
  final class zza
    extends ChannelClient.ChannelCallback
  {
    private zza() {}
    
    public final void onChannelClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2)
    {
      WearableListenerService.this.onChannelClosed(paramChannel, paramInt1, paramInt2);
    }
    
    public final void onChannelOpened(ChannelClient.Channel paramChannel)
    {
      WearableListenerService.this.onChannelOpened(paramChannel);
    }
    
    public final void onInputClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2)
    {
      WearableListenerService.this.onInputClosed(paramChannel, paramInt1, paramInt2);
    }
    
    public final void onOutputClosed(ChannelClient.Channel paramChannel, int paramInt1, int paramInt2)
    {
      WearableListenerService.this.onOutputClosed(paramChannel, paramInt1, paramInt2);
    }
  }
  
  final class zzb
    implements ServiceConnection
  {
    private zzb() {}
    
    public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder) {}
    
    public final void onServiceDisconnected(ComponentName paramComponentName) {}
  }
  
  final class zzc
    extends Handler
  {
    private boolean started;
    private final WearableListenerService.zzb zzal = new WearableListenerService.zzb(WearableListenerService.this, null);
    
    zzc(Looper paramLooper)
    {
      super();
    }
    
    /* Error */
    @android.annotation.SuppressLint({"UntrackedBindService"})
    private final void zzb()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 35	com/google/android/gms/wearable/WearableListenerService$zzc:started	Z
      //   6: istore_1
      //   7: iload_1
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: ldc 37
      //   16: iconst_2
      //   17: invokestatic 43	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
      //   20: ifeq +56 -> 76
      //   23: aload_0
      //   24: getfield 17	com/google/android/gms/wearable/WearableListenerService$zzc:zzak	Lcom/google/android/gms/wearable/WearableListenerService;
      //   27: invokestatic 47	com/google/android/gms/wearable/WearableListenerService:zza	(Lcom/google/android/gms/wearable/WearableListenerService;)Landroid/content/ComponentName;
      //   30: invokestatic 53	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   33: astore_2
      //   34: aload_2
      //   35: invokestatic 53	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   38: invokevirtual 57	java/lang/String:length	()I
      //   41: istore_3
      //   42: new 59	java/lang/StringBuilder
      //   45: astore 4
      //   47: aload 4
      //   49: iload_3
      //   50: bipush 13
      //   52: iadd
      //   53: invokespecial 62	java/lang/StringBuilder:<init>	(I)V
      //   56: ldc 37
      //   58: aload 4
      //   60: ldc 64
      //   62: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   65: aload_2
      //   66: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   69: invokevirtual 72	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   72: invokestatic 76	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   75: pop
      //   76: aload_0
      //   77: getfield 17	com/google/android/gms/wearable/WearableListenerService$zzc:zzak	Lcom/google/android/gms/wearable/WearableListenerService;
      //   80: aload_0
      //   81: getfield 17	com/google/android/gms/wearable/WearableListenerService$zzc:zzak	Lcom/google/android/gms/wearable/WearableListenerService;
      //   84: invokestatic 79	com/google/android/gms/wearable/WearableListenerService:zzb	(Lcom/google/android/gms/wearable/WearableListenerService;)Landroid/content/Intent;
      //   87: aload_0
      //   88: getfield 27	com/google/android/gms/wearable/WearableListenerService$zzc:zzal	Lcom/google/android/gms/wearable/WearableListenerService$zzb;
      //   91: iconst_1
      //   92: invokevirtual 83	com/google/android/gms/wearable/WearableListenerService:bindService	(Landroid/content/Intent;Landroid/content/ServiceConnection;I)Z
      //   95: pop
      //   96: aload_0
      //   97: iconst_1
      //   98: putfield 35	com/google/android/gms/wearable/WearableListenerService$zzc:started	Z
      //   101: goto -90 -> 11
      //   104: astore 4
      //   106: aload_0
      //   107: monitorexit
      //   108: aload 4
      //   110: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	111	0	this	zzc
      //   6	2	1	bool	boolean
      //   33	33	2	str	String
      //   41	12	3	i	int
      //   45	14	4	localStringBuilder	StringBuilder
      //   104	5	4	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	104	finally
      //   14	76	104	finally
      //   76	101	104	finally
    }
    
    /* Error */
    @android.annotation.SuppressLint({"UntrackedBindService"})
    private final void zzb(String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 35	com/google/android/gms/wearable/WearableListenerService$zzc:started	Z
      //   6: istore_2
      //   7: iload_2
      //   8: ifne +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: ldc 37
      //   16: iconst_2
      //   17: invokestatic 43	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
      //   20: ifeq +79 -> 99
      //   23: aload_0
      //   24: getfield 17	com/google/android/gms/wearable/WearableListenerService$zzc:zzak	Lcom/google/android/gms/wearable/WearableListenerService;
      //   27: invokestatic 47	com/google/android/gms/wearable/WearableListenerService:zza	(Lcom/google/android/gms/wearable/WearableListenerService;)Landroid/content/ComponentName;
      //   30: invokestatic 53	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   33: astore_3
      //   34: aload_1
      //   35: invokestatic 53	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   38: invokevirtual 57	java/lang/String:length	()I
      //   41: istore 4
      //   43: aload_3
      //   44: invokestatic 53	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
      //   47: invokevirtual 57	java/lang/String:length	()I
      //   50: istore 5
      //   52: new 59	java/lang/StringBuilder
      //   55: astore 6
      //   57: aload 6
      //   59: iload 4
      //   61: bipush 17
      //   63: iadd
      //   64: iload 5
      //   66: iadd
      //   67: invokespecial 62	java/lang/StringBuilder:<init>	(I)V
      //   70: ldc 37
      //   72: aload 6
      //   74: ldc 89
      //   76: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   79: aload_1
      //   80: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   83: ldc 91
      //   85: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   88: aload_3
      //   89: invokevirtual 68	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   92: invokevirtual 72	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   95: invokestatic 76	android/util/Log:v	(Ljava/lang/String;Ljava/lang/String;)I
      //   98: pop
      //   99: aload_0
      //   100: getfield 17	com/google/android/gms/wearable/WearableListenerService$zzc:zzak	Lcom/google/android/gms/wearable/WearableListenerService;
      //   103: aload_0
      //   104: getfield 27	com/google/android/gms/wearable/WearableListenerService$zzc:zzal	Lcom/google/android/gms/wearable/WearableListenerService$zzb;
      //   107: invokevirtual 95	com/google/android/gms/wearable/WearableListenerService:unbindService	(Landroid/content/ServiceConnection;)V
      //   110: aload_0
      //   111: iconst_0
      //   112: putfield 35	com/google/android/gms/wearable/WearableListenerService$zzc:started	Z
      //   115: goto -104 -> 11
      //   118: astore_1
      //   119: aload_0
      //   120: monitorexit
      //   121: aload_1
      //   122: athrow
      //   123: astore_1
      //   124: ldc 37
      //   126: ldc 97
      //   128: aload_1
      //   129: invokestatic 101	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   132: pop
      //   133: goto -23 -> 110
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	136	0	this	zzc
      //   0	136	1	paramString	String
      //   6	2	2	bool	boolean
      //   33	56	3	str	String
      //   41	23	4	i	int
      //   50	17	5	j	int
      //   55	18	6	localStringBuilder	StringBuilder
      // Exception table:
      //   from	to	target	type
      //   2	7	118	finally
      //   14	99	118	finally
      //   99	110	118	finally
      //   110	115	118	finally
      //   124	133	118	finally
      //   99	110	123	java/lang/RuntimeException
    }
    
    public final void dispatchMessage(Message paramMessage)
    {
      zzb();
      try
      {
        super.dispatchMessage(paramMessage);
        return;
      }
      finally
      {
        if (!hasMessages(0)) {
          zzb("dispatch");
        }
      }
    }
    
    final void quit()
    {
      getLooper().quit();
      zzb("quit");
    }
  }
  
  final class zzd
    extends zzen
  {
    private volatile int zzam = -1;
    
    private zzd() {}
    
    private final boolean zza(Runnable paramRunnable, String arg2, Object paramObject)
    {
      boolean bool = false;
      if (Log.isLoggable("WearableLS", 3)) {
        Log.d("WearableLS", String.format("%s: %s %s", new Object[] { ???, WearableListenerService.zza(WearableListenerService.this).toString(), paramObject }));
      }
      int i = Binder.getCallingUid();
      if (i == this.zzam)
      {
        i = 1;
        if (i != 0) {
          break label172;
        }
      }
      for (;;)
      {
        return bool;
        if ((zzhp.zza(WearableListenerService.this).zze("com.google.android.wearable.app.cn")) && (UidVerifier.uidHasPackageName(WearableListenerService.this, i, "com.google.android.wearable.app.cn")))
        {
          this.zzam = i;
          i = 1;
          break;
        }
        if (UidVerifier.isGooglePlayServicesUid(WearableListenerService.this, i))
        {
          this.zzam = i;
          i = 1;
          break;
        }
        Log.e("WearableLS", 57 + "Caller is not GooglePlayServices; caller UID: " + i);
        i = 0;
        break;
        label172:
        synchronized (WearableListenerService.zzd(WearableListenerService.this))
        {
          if (!WearableListenerService.zze(WearableListenerService.this)) {}
        }
        WearableListenerService.zzf(WearableListenerService.this).post(paramRunnable);
        bool = true;
      }
    }
    
    public final void onConnectedNodes(List<zzfo> paramList)
    {
      zza(new zzp(this, paramList), "onConnectedNodes", paramList);
    }
    
    public final void zza(DataHolder paramDataHolder)
    {
      zzl localzzl = new zzl(this, paramDataHolder);
      try
      {
        String str = String.valueOf(paramDataHolder);
        int i = paramDataHolder.getCount();
        int j = String.valueOf(str).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(j + 18);
        boolean bool = zza(localzzl, "onDataItemChanged", str + ", rows=" + i);
        if (!bool) {}
        return;
      }
      finally
      {
        paramDataHolder.close();
      }
    }
    
    public final void zza(zzah paramzzah)
    {
      zza(new zzq(this, paramzzah), "onConnectedCapabilityChanged", paramzzah);
    }
    
    public final void zza(zzaw paramzzaw)
    {
      zza(new zzt(this, paramzzaw), "onChannelEvent", paramzzaw);
    }
    
    public final void zza(zzfe paramzzfe)
    {
      zza(new zzm(this, paramzzfe), "onMessageReceived", paramzzfe);
    }
    
    public final void zza(zzfo paramzzfo)
    {
      zza(new zzn(this, paramzzfo), "onPeerConnected", paramzzfo);
    }
    
    public final void zza(zzi paramzzi)
    {
      zza(new zzs(this, paramzzi), "onEntityUpdate", paramzzi);
    }
    
    public final void zza(com.google.android.gms.wearable.internal.zzl paramzzl)
    {
      zza(new zzr(this, paramzzl), "onNotificationReceived", paramzzl);
    }
    
    public final void zzb(zzfo paramzzfo)
    {
      zza(new zzo(this, paramzzfo), "onPeerDisconnected", paramzzfo);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/WearableListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */