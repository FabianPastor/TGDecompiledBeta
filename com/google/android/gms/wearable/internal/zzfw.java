package com.google.android.gms.wearable.internal;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzj;
import com.google.android.gms.common.internal.zzq;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.zze;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.internal.zzbdw;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.CapabilityApi.CapabilityListener;
import com.google.android.gms.wearable.ChannelApi.ChannelListener;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.DataApi.DataListener;
import com.google.android.gms.wearable.DataApi.GetFdForAssetResult;
import com.google.android.gms.wearable.MessageApi.MessageListener;
import com.google.android.gms.wearable.NodeApi.NodeListener;
import com.google.android.gms.wearable.PutDataRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public final class zzfw
  extends zzz<zzdn>
{
  private final zzdp<Object> zzbTg = new zzdp();
  private final zzdp<Object> zzbTh = new zzdp();
  private final zzdp<ChannelApi.ChannelListener> zzbTi = new zzdp();
  private final zzdp<DataApi.DataListener> zzbTj = new zzdp();
  private final zzdp<MessageApi.MessageListener> zzbTk = new zzdp();
  private final zzdp<NodeApi.NodeListener> zzbTl = new zzdp();
  private final zzdp<Object> zzbTm = new zzdp();
  private final zzdp<CapabilityApi.CapabilityListener> zzbTn = new zzdp();
  private final zzgh zzbTo;
  private final ExecutorService zzbrV;
  
  public zzfw(Context paramContext, Looper paramLooper, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, zzq paramzzq)
  {
    this(paramContext, paramLooper, paramConnectionCallbacks, paramOnConnectionFailedListener, paramzzq, Executors.newCachedThreadPool(), zzgh.zzbz(paramContext));
  }
  
  private zzfw(Context paramContext, Looper paramLooper, GoogleApiClient.ConnectionCallbacks paramConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener, zzq paramzzq, ExecutorService paramExecutorService, zzgh paramzzgh)
  {
    super(paramContext, paramLooper, 14, paramzzq, paramConnectionCallbacks, paramOnConnectionFailedListener);
    this.zzbrV = ((ExecutorService)zzbo.zzu(paramExecutorService));
    this.zzbTo = paramzzgh;
  }
  
  protected final void zza(int paramInt1, IBinder paramIBinder, Bundle paramBundle, int paramInt2)
  {
    if (Log.isLoggable("WearableClient", 2)) {
      Log.d("WearableClient", 41 + "onPostInitHandler: statusCode " + paramInt1);
    }
    if (paramInt1 == 0)
    {
      this.zzbTg.zzam(paramIBinder);
      this.zzbTh.zzam(paramIBinder);
      this.zzbTi.zzam(paramIBinder);
      this.zzbTj.zzam(paramIBinder);
      this.zzbTk.zzam(paramIBinder);
      this.zzbTl.zzam(paramIBinder);
      this.zzbTm.zzam(paramIBinder);
      this.zzbTn.zzam(paramIBinder);
    }
    super.zza(paramInt1, paramIBinder, paramBundle, paramInt2);
  }
  
  public final void zza(@NonNull zzj paramzzj)
  {
    int i = 0;
    if (!zzpe()) {
      try
      {
        Object localObject = getContext().getPackageManager().getApplicationInfo("com.google.android.wearable.app.cn", 128).metaData;
        if (localObject != null) {
          i = ((Bundle)localObject).getInt("com.google.android.wearable.api.version", 0);
        }
        if (i < zze.GOOGLE_PLAY_SERVICES_VERSION_CODE)
        {
          int j = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
          Log.w("WearableClient", 80 + "Android Wear out of date. Requires API version " + j + " but found " + i);
          Context localContext1 = getContext();
          Context localContext2 = getContext();
          localObject = new Intent("com.google.android.wearable.app.cn.UPDATE_ANDROID_WEAR").setPackage("com.google.android.wearable.app.cn");
          if (localContext2.getPackageManager().resolveActivity((Intent)localObject, 65536) != null) {}
          for (;;)
          {
            zza(paramzzj, 6, PendingIntent.getActivity(localContext1, 0, (Intent)localObject, 0));
            return;
            localObject = new Intent("android.intent.action.VIEW", Uri.parse("market://details").buildUpon().appendQueryParameter("id", "com.google.android.wearable.app.cn").build());
          }
        }
        super.zza(paramzzj);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        zza(paramzzj, 16, null);
        return;
      }
    }
  }
  
  public final void zza(zzbaz<DataApi.GetFdForAssetResult> paramzzbaz, Asset paramAsset)
    throws RemoteException
  {
    ((zzdn)zzrf()).zza(new zzfn(paramzzbaz), paramAsset);
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, CapabilityApi.CapabilityListener paramCapabilityListener)
    throws RemoteException
  {
    this.zzbTn.zza(this, paramzzbaz, paramCapabilityListener);
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, CapabilityApi.CapabilityListener paramCapabilityListener, zzbdw<CapabilityApi.CapabilityListener> paramzzbdw, IntentFilter[] paramArrayOfIntentFilter)
    throws RemoteException
  {
    this.zzbTn.zza(this, paramzzbaz, paramCapabilityListener, zzga.zze(paramzzbdw, paramArrayOfIntentFilter));
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, ChannelApi.ChannelListener paramChannelListener, zzbdw<ChannelApi.ChannelListener> paramzzbdw, String paramString, IntentFilter[] paramArrayOfIntentFilter)
    throws RemoteException
  {
    if (paramString == null)
    {
      this.zzbTi.zza(this, paramzzbaz, paramChannelListener, zzga.zzd(paramzzbdw, paramArrayOfIntentFilter));
      return;
    }
    paramChannelListener = new zzeu(paramString, paramChannelListener);
    this.zzbTi.zza(this, paramzzbaz, paramChannelListener, zzga.zza(paramzzbdw, paramString, paramArrayOfIntentFilter));
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, ChannelApi.ChannelListener paramChannelListener, String paramString)
    throws RemoteException
  {
    if (paramString == null)
    {
      this.zzbTi.zza(this, paramzzbaz, paramChannelListener);
      return;
    }
    paramChannelListener = new zzeu(paramString, paramChannelListener);
    this.zzbTi.zza(this, paramzzbaz, paramChannelListener);
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, DataApi.DataListener paramDataListener)
    throws RemoteException
  {
    this.zzbTj.zza(this, paramzzbaz, paramDataListener);
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, DataApi.DataListener paramDataListener, zzbdw<DataApi.DataListener> paramzzbdw, IntentFilter[] paramArrayOfIntentFilter)
    throws RemoteException
  {
    this.zzbTj.zza(this, paramzzbaz, paramDataListener, zzga.zza(paramzzbdw, paramArrayOfIntentFilter));
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, MessageApi.MessageListener paramMessageListener)
    throws RemoteException
  {
    this.zzbTk.zza(this, paramzzbaz, paramMessageListener);
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, MessageApi.MessageListener paramMessageListener, zzbdw<MessageApi.MessageListener> paramzzbdw, IntentFilter[] paramArrayOfIntentFilter)
    throws RemoteException
  {
    this.zzbTk.zza(this, paramzzbaz, paramMessageListener, zzga.zzb(paramzzbdw, paramArrayOfIntentFilter));
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, NodeApi.NodeListener paramNodeListener)
    throws RemoteException
  {
    this.zzbTl.zza(this, paramzzbaz, paramNodeListener);
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, NodeApi.NodeListener paramNodeListener, zzbdw<NodeApi.NodeListener> paramzzbdw, IntentFilter[] paramArrayOfIntentFilter)
    throws RemoteException
  {
    this.zzbTl.zza(this, paramzzbaz, paramNodeListener, zzga.zzc(paramzzbdw, paramArrayOfIntentFilter));
  }
  
  public final void zza(zzbaz<DataApi.DataItemResult> paramzzbaz, PutDataRequest paramPutDataRequest)
    throws RemoteException
  {
    Object localObject2 = paramPutDataRequest.getAssets().entrySet().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject1 = (Asset)((Map.Entry)((Iterator)localObject2).next()).getValue();
      if ((((Asset)localObject1).getData() == null) && (((Asset)localObject1).getDigest() == null) && (((Asset)localObject1).getFd() == null) && (((Asset)localObject1).getUri() == null))
      {
        paramzzbaz = String.valueOf(paramPutDataRequest.getUri());
        paramPutDataRequest = String.valueOf(localObject1);
        throw new IllegalArgumentException(String.valueOf(paramzzbaz).length() + 33 + String.valueOf(paramPutDataRequest).length() + "Put for " + paramzzbaz + " contains invalid asset: " + paramPutDataRequest);
      }
    }
    localObject2 = PutDataRequest.zzt(paramPutDataRequest.getUri());
    ((PutDataRequest)localObject2).setData(paramPutDataRequest.getData());
    if (paramPutDataRequest.isUrgent()) {
      ((PutDataRequest)localObject2).setUrgent();
    }
    Object localObject1 = new ArrayList();
    Iterator localIterator = paramPutDataRequest.getAssets().entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Object localObject3 = (Asset)localEntry.getValue();
      Object localObject4;
      if (((Asset)localObject3).getData() != null) {
        try
        {
          localObject4 = ParcelFileDescriptor.createPipe();
          if (Log.isLoggable("WearableClient", 3))
          {
            String str1 = String.valueOf(localObject3);
            String str2 = String.valueOf(localObject4[0]);
            String str3 = String.valueOf(localObject4[1]);
            Log.d("WearableClient", String.valueOf(str1).length() + 61 + String.valueOf(str2).length() + String.valueOf(str3).length() + "processAssets: replacing data with FD in asset: " + str1 + " read:" + str2 + " write:" + str3);
          }
          ((PutDataRequest)localObject2).putAsset((String)localEntry.getKey(), Asset.createFromFd(localObject4[0]));
          localObject3 = new FutureTask(new zzfx(this, localObject4[1], ((Asset)localObject3).getData()));
          ((List)localObject1).add(localObject3);
          this.zzbrV.submit((Runnable)localObject3);
        }
        catch (IOException paramzzbaz)
        {
          paramPutDataRequest = String.valueOf(paramPutDataRequest);
          throw new IllegalStateException(String.valueOf(paramPutDataRequest).length() + 60 + "Unable to create ParcelFileDescriptor for asset in request: " + paramPutDataRequest, paramzzbaz);
        }
      } else if (((Asset)localObject3).getUri() != null) {
        try
        {
          localObject4 = Asset.createFromFd(getContext().getContentResolver().openFileDescriptor(((Asset)localObject3).getUri(), "r"));
          ((PutDataRequest)localObject2).putAsset((String)localEntry.getKey(), (Asset)localObject4);
        }
        catch (FileNotFoundException paramPutDataRequest)
        {
          new zzfr(paramzzbaz, (List)localObject1).zza(new zzem(4005, null));
          paramzzbaz = String.valueOf(((Asset)localObject3).getUri());
          Log.w("WearableClient", String.valueOf(paramzzbaz).length() + 28 + "Couldn't resolve asset URI: " + paramzzbaz);
          return;
        }
      } else {
        ((PutDataRequest)localObject2).putAsset((String)localEntry.getKey(), (Asset)localObject3);
      }
    }
    ((zzdn)zzrf()).zza(new zzfr(paramzzbaz, (List)localObject1), (PutDataRequest)localObject2);
  }
  
  /* Error */
  public final void zza(zzbaz<Status> paramzzbaz, String paramString, Uri paramUri, long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 76	com/google/android/gms/wearable/internal/zzfw:zzbrV	Ljava/util/concurrent/ExecutorService;
    //   4: astore 9
    //   6: aload_1
    //   7: invokestatic 72	com/google/android/gms/common/internal/zzbo:zzu	(Ljava/lang/Object;)Ljava/lang/Object;
    //   10: pop
    //   11: aload_2
    //   12: invokestatic 72	com/google/android/gms/common/internal/zzbo:zzu	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_3
    //   17: invokestatic 72	com/google/android/gms/common/internal/zzbo:zzu	(Ljava/lang/Object;)Ljava/lang/Object;
    //   20: pop
    //   21: lload 4
    //   23: lconst_0
    //   24: lcmp
    //   25: iflt +81 -> 106
    //   28: iconst_1
    //   29: istore 8
    //   31: iload 8
    //   33: ldc_w 473
    //   36: iconst_1
    //   37: anewarray 475	java/lang/Object
    //   40: dup
    //   41: iconst_0
    //   42: lload 4
    //   44: invokestatic 480	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   47: aastore
    //   48: invokestatic 483	com/google/android/gms/common/internal/zzbo:zzb	(ZLjava/lang/String;[Ljava/lang/Object;)V
    //   51: lload 6
    //   53: ldc2_w 484
    //   56: lcmp
    //   57: iflt +55 -> 112
    //   60: iconst_1
    //   61: istore 8
    //   63: iload 8
    //   65: ldc_w 487
    //   68: iconst_1
    //   69: anewarray 475	java/lang/Object
    //   72: dup
    //   73: iconst_0
    //   74: lload 6
    //   76: invokestatic 480	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   79: aastore
    //   80: invokestatic 483	com/google/android/gms/common/internal/zzbo:zzb	(ZLjava/lang/String;[Ljava/lang/Object;)V
    //   83: aload 9
    //   85: new 489	com/google/android/gms/wearable/internal/zzfz
    //   88: dup
    //   89: aload_0
    //   90: aload_3
    //   91: aload_1
    //   92: aload_2
    //   93: lload 4
    //   95: lload 6
    //   97: invokespecial 492	com/google/android/gms/wearable/internal/zzfz:<init>	(Lcom/google/android/gms/wearable/internal/zzfw;Landroid/net/Uri;Lcom/google/android/gms/internal/zzbaz;Ljava/lang/String;JJ)V
    //   100: invokeinterface 496 2 0
    //   105: return
    //   106: iconst_0
    //   107: istore 8
    //   109: goto -78 -> 31
    //   112: iconst_0
    //   113: istore 8
    //   115: goto -52 -> 63
    //   118: astore_2
    //   119: aload_1
    //   120: new 498	com/google/android/gms/common/api/Status
    //   123: dup
    //   124: bipush 8
    //   126: invokespecial 499	com/google/android/gms/common/api/Status:<init>	(I)V
    //   129: invokeinterface 505 2 0
    //   134: aload_2
    //   135: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	zzfw
    //   0	136	1	paramzzbaz	zzbaz<Status>
    //   0	136	2	paramString	String
    //   0	136	3	paramUri	Uri
    //   0	136	4	paramLong1	long
    //   0	136	6	paramLong2	long
    //   29	85	8	bool	boolean
    //   4	80	9	localExecutorService	ExecutorService
    // Exception table:
    //   from	to	target	type
    //   0	21	118	java/lang/RuntimeException
    //   31	51	118	java/lang/RuntimeException
    //   63	105	118	java/lang/RuntimeException
  }
  
  public final void zza(zzbaz<Status> paramzzbaz, String paramString, Uri paramUri, boolean paramBoolean)
  {
    try
    {
      ExecutorService localExecutorService = this.zzbrV;
      zzbo.zzu(paramzzbaz);
      zzbo.zzu(paramString);
      zzbo.zzu(paramUri);
      localExecutorService.execute(new zzfy(this, paramUri, paramzzbaz, paramBoolean, paramString));
      return;
    }
    catch (RuntimeException paramString)
    {
      paramzzbaz.zzr(new Status(8));
      throw paramString;
    }
  }
  
  protected final String zzdb()
  {
    return "com.google.android.gms.wearable.BIND";
  }
  
  protected final String zzdc()
  {
    return "com.google.android.gms.wearable.internal.IWearableService";
  }
  
  public final boolean zzpe()
  {
    return !this.zzbTo.zzgm("com.google.android.wearable.app.cn");
  }
  
  protected final String zzqZ()
  {
    if (this.zzbTo.zzgm("com.google.android.wearable.app.cn")) {
      return "com.google.android.wearable.app.cn";
    }
    return "com.google.android.gms";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzfw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */