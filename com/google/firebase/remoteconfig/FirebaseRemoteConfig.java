package com.google.firebase.remoteconfig;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import com.google.android.gms.internal.config.zzal;
import com.google.android.gms.internal.config.zzan;
import com.google.android.gms.internal.config.zzao;
import com.google.android.gms.internal.config.zzaq;
import com.google.android.gms.internal.config.zzar;
import com.google.android.gms.internal.config.zzas;
import com.google.android.gms.internal.config.zzat;
import com.google.android.gms.internal.config.zzau;
import com.google.android.gms.internal.config.zzav;
import com.google.android.gms.internal.config.zzaw;
import com.google.android.gms.internal.config.zzax;
import com.google.android.gms.internal.config.zzk;
import com.google.android.gms.internal.config.zzv;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.abt.FirebaseABTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.concurrent.GuardedBy;

public class FirebaseRemoteConfig
{
  public static final byte[] DEFAULT_VALUE_FOR_BYTE_ARRAY = new byte[0];
  @GuardedBy("FirebaseRemoteConfig.class")
  private static FirebaseRemoteConfig zzaf;
  private final Context mContext;
  private zzao zzag;
  private zzao zzah;
  private zzao zzai;
  private zzar zzaj;
  private final FirebaseApp zzak;
  private final ReadWriteLock zzal = new ReentrantReadWriteLock(true);
  private final FirebaseABTesting zzam;
  
  private FirebaseRemoteConfig(Context paramContext, zzao paramzzao1, zzao paramzzao2, zzao paramzzao3, zzar paramzzar)
  {
    this.mContext = paramContext;
    this.zzaj = paramzzar;
    this.zzaj.zzc(zzd(this.mContext));
    this.zzag = paramzzao1;
    this.zzah = paramzzao2;
    this.zzai = paramzzao3;
    this.zzak = FirebaseApp.initializeApp(this.mContext);
    this.zzam = zzf(this.mContext);
  }
  
  public static FirebaseRemoteConfig getInstance()
  {
    return zzc(FirebaseApp.getInstance().getApplicationContext());
  }
  
  private static zzao zza(zzas paramzzas)
  {
    int i = 0;
    if (paramzzas == null) {}
    HashMap localHashMap;
    for (paramzzas = null;; paramzzas = new zzao(localHashMap, paramzzas.timestamp, (List)???))
    {
      return paramzzas;
      localHashMap = new HashMap();
      for (Object localObject2 : paramzzas.zzbg)
      {
        String str = ((zzav)localObject2).namespace;
        localObject3 = new HashMap();
        for (localObject2 : ((zzav)localObject2).zzbp) {
          ((Map)localObject3).put(((zzat)localObject2).zzbj, ((zzat)localObject2).zzbk);
        }
        localHashMap.put(str, localObject3);
      }
      Object localObject3 = paramzzas.zzbh;
      ??? = new ArrayList();
      ??? = localObject3.length;
      for (??? = i; ??? < ???; ???++) {
        ((List)???).add(localObject3[???]);
      }
    }
  }
  
  /* Error */
  private final Task<Void> zza(long paramLong, zzv paramzzv)
  {
    // Byte code:
    //   0: ldc -110
    //   2: istore 4
    //   4: new 148	com/google/android/gms/tasks/TaskCompletionSource
    //   7: dup
    //   8: invokespecial 149	com/google/android/gms/tasks/TaskCompletionSource:<init>	()V
    //   11: astore 5
    //   13: aload_0
    //   14: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   17: invokeinterface 155 1 0
    //   22: invokeinterface 160 1 0
    //   27: new 162	com/google/android/gms/internal/config/zzj
    //   30: astore 6
    //   32: aload 6
    //   34: invokespecial 163	com/google/android/gms/internal/config/zzj:<init>	()V
    //   37: aload 6
    //   39: lload_1
    //   40: invokevirtual 166	com/google/android/gms/internal/config/zzj:zza	(J)Lcom/google/android/gms/internal/config/zzj;
    //   43: pop
    //   44: aload_0
    //   45: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzak	Lcom/google/firebase/FirebaseApp;
    //   48: ifnull +19 -> 67
    //   51: aload 6
    //   53: aload_0
    //   54: getfield 69	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzak	Lcom/google/firebase/FirebaseApp;
    //   57: invokevirtual 170	com/google/firebase/FirebaseApp:getOptions	()Lcom/google/firebase/FirebaseOptions;
    //   60: invokevirtual 176	com/google/firebase/FirebaseOptions:getApplicationId	()Ljava/lang/String;
    //   63: invokevirtual 179	com/google/android/gms/internal/config/zzj:zza	(Ljava/lang/String;)Lcom/google/android/gms/internal/config/zzj;
    //   66: pop
    //   67: aload_0
    //   68: getfield 45	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   71: invokevirtual 183	com/google/android/gms/internal/config/zzar:isDeveloperModeEnabled	()Z
    //   74: ifeq +13 -> 87
    //   77: aload 6
    //   79: ldc -71
    //   81: ldc -69
    //   83: invokevirtual 190	com/google/android/gms/internal/config/zzj:zza	(Ljava/lang/String;Ljava/lang/String;)Lcom/google/android/gms/internal/config/zzj;
    //   86: pop
    //   87: aload 6
    //   89: sipush 10300
    //   92: invokevirtual 193	com/google/android/gms/internal/config/zzj:zza	(I)Lcom/google/android/gms/internal/config/zzj;
    //   95: pop
    //   96: aload_0
    //   97: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   100: ifnull +64 -> 164
    //   103: aload_0
    //   104: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   107: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   110: ldc2_w 198
    //   113: lcmp
    //   114: ifeq +50 -> 164
    //   117: invokestatic 204	java/lang/System:currentTimeMillis	()J
    //   120: lstore 7
    //   122: aload_0
    //   123: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   126: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   129: lstore_1
    //   130: getstatic 210	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   133: lload 7
    //   135: lload_1
    //   136: lsub
    //   137: getstatic 213	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   140: invokevirtual 217	java/util/concurrent/TimeUnit:convert	(JLjava/util/concurrent/TimeUnit;)J
    //   143: lstore_1
    //   144: lload_1
    //   145: ldc2_w 218
    //   148: lcmp
    //   149: ifge +147 -> 296
    //   152: lload_1
    //   153: l2i
    //   154: istore 9
    //   156: aload 6
    //   158: iload 9
    //   160: invokevirtual 221	com/google/android/gms/internal/config/zzj:zzc	(I)Lcom/google/android/gms/internal/config/zzj;
    //   163: pop
    //   164: aload_0
    //   165: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   168: ifnull +68 -> 236
    //   171: aload_0
    //   172: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   175: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   178: ldc2_w 198
    //   181: lcmp
    //   182: ifeq +54 -> 236
    //   185: invokestatic 204	java/lang/System:currentTimeMillis	()J
    //   188: lstore_1
    //   189: aload_0
    //   190: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   193: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   196: lstore 7
    //   198: getstatic 210	java/util/concurrent/TimeUnit:SECONDS	Ljava/util/concurrent/TimeUnit;
    //   201: lload_1
    //   202: lload 7
    //   204: lsub
    //   205: getstatic 213	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   208: invokevirtual 217	java/util/concurrent/TimeUnit:convert	(JLjava/util/concurrent/TimeUnit;)J
    //   211: lstore_1
    //   212: iload 4
    //   214: istore 9
    //   216: lload_1
    //   217: ldc2_w 218
    //   220: lcmp
    //   221: ifge +7 -> 228
    //   224: lload_1
    //   225: l2i
    //   226: istore 9
    //   228: aload 6
    //   230: iload 9
    //   232: invokevirtual 224	com/google/android/gms/internal/config/zzj:zzb	(I)Lcom/google/android/gms/internal/config/zzj;
    //   235: pop
    //   236: aload 6
    //   238: invokevirtual 227	com/google/android/gms/internal/config/zzj:zzf	()Lcom/google/android/gms/internal/config/zzi;
    //   241: astore 6
    //   243: getstatic 233	com/google/android/gms/internal/config/zze:zze	Lcom/google/android/gms/internal/config/zzg;
    //   246: aload_3
    //   247: invokevirtual 239	com/google/android/gms/internal/config/zzv:asGoogleApiClient	()Lcom/google/android/gms/common/api/GoogleApiClient;
    //   250: aload 6
    //   252: invokeinterface 244 3 0
    //   257: astore 6
    //   259: new 246	com/google/firebase/remoteconfig/zza
    //   262: astore_3
    //   263: aload_3
    //   264: aload_0
    //   265: aload 5
    //   267: invokespecial 249	com/google/firebase/remoteconfig/zza:<init>	(Lcom/google/firebase/remoteconfig/FirebaseRemoteConfig;Lcom/google/android/gms/tasks/TaskCompletionSource;)V
    //   270: aload 6
    //   272: aload_3
    //   273: invokevirtual 255	com/google/android/gms/common/api/PendingResult:setResultCallback	(Lcom/google/android/gms/common/api/ResultCallback;)V
    //   276: aload_0
    //   277: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   280: invokeinterface 155 1 0
    //   285: invokeinterface 258 1 0
    //   290: aload 5
    //   292: invokevirtual 262	com/google/android/gms/tasks/TaskCompletionSource:getTask	()Lcom/google/android/gms/tasks/Task;
    //   295: areturn
    //   296: ldc -110
    //   298: istore 9
    //   300: goto -144 -> 156
    //   303: astore_3
    //   304: aload_0
    //   305: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   308: invokeinterface 155 1 0
    //   313: invokeinterface 258 1 0
    //   318: aload_3
    //   319: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	320	0	this	FirebaseRemoteConfig
    //   0	320	1	paramLong	long
    //   0	320	3	paramzzv	zzv
    //   2	211	4	i	int
    //   11	280	5	localTaskCompletionSource	TaskCompletionSource
    //   30	241	6	localObject	Object
    //   120	83	7	l	long
    //   154	145	9	j	int
    // Exception table:
    //   from	to	target	type
    //   27	67	303	finally
    //   67	87	303	finally
    //   87	144	303	finally
    //   156	164	303	finally
    //   164	212	303	finally
    //   228	236	303	finally
    //   236	276	303	finally
  }
  
  private final void zza(TaskCompletionSource<Void> paramTaskCompletionSource, Status paramStatus)
  {
    if (paramStatus == null) {
      Log.w("FirebaseRemoteConfig", "Received null IPC status for failure.");
    }
    for (;;)
    {
      this.zzal.writeLock().lock();
      try
      {
        this.zzaj.zzf(1);
        paramStatus = new com/google/firebase/remoteconfig/FirebaseRemoteConfigFetchException;
        paramStatus.<init>();
        paramTaskCompletionSource.setException(paramStatus);
        zzn();
        return;
      }
      finally
      {
        int i;
        this.zzal.writeLock().unlock();
      }
      i = paramStatus.getStatusCode();
      paramStatus = paramStatus.getStatusMessage();
      Log.w("FirebaseRemoteConfig", String.valueOf(paramStatus).length() + 25 + "IPC failure: " + i + ":" + paramStatus);
    }
  }
  
  private static void zza(Runnable paramRunnable)
  {
    AsyncTask.SERIAL_EXECUTOR.execute(paramRunnable);
  }
  
  private static FirebaseRemoteConfig zzc(Context paramContext)
  {
    for (;;)
    {
      Object localObject5;
      zzao localzzao1;
      zzao localzzao2;
      zzao localzzao3;
      try
      {
        zzaw localzzaw;
        if (zzaf == null)
        {
          localObject1 = new com/google/android/gms/internal/config/zzar;
          ((zzar)localObject1).<init>();
          localzzaw = zze(paramContext);
          if (localzzaw == null)
          {
            if (!Log.isLoggable("FirebaseRemoteConfig", 3)) {
              break label323;
            }
            Log.d("FirebaseRemoteConfig", "No persisted config was found. Initializing from scratch.");
            localObject2 = null;
            localObject3 = null;
            localObject4 = null;
            localObject5 = new com/google/firebase/remoteconfig/FirebaseRemoteConfig;
            ((FirebaseRemoteConfig)localObject5).<init>(paramContext, (zzao)localObject4, (zzao)localObject3, (zzao)localObject2, (zzar)localObject1);
            zzaf = (FirebaseRemoteConfig)localObject5;
          }
        }
        else
        {
          paramContext = zzaf;
          return paramContext;
        }
        if (Log.isLoggable("FirebaseRemoteConfig", 3)) {
          Log.d("FirebaseRemoteConfig", "Initializing from persisted config.");
        }
        localzzao1 = zza(localzzaw.zzbq);
        localzzao2 = zza(localzzaw.zzbr);
        localzzao3 = zza(localzzaw.zzbs);
        localObject4 = localzzaw.zzbt;
        if (localObject4 == null)
        {
          localObject5 = null;
          localObject4 = localzzao1;
          localObject3 = localzzao2;
          localObject2 = localzzao3;
          localObject1 = localObject5;
          if (localObject5 == null) {
            continue;
          }
          localObject2 = localzzaw.zzbu;
          localObject3 = new java/util/HashMap;
          ((HashMap)localObject3).<init>();
          if (localObject2 != null)
          {
            int i = localObject2.length;
            int j = 0;
            if (j < i)
            {
              localObject1 = localObject2[j];
              localObject4 = ((zzax)localObject1).namespace;
              int k = ((zzax)localObject1).resourceId;
              long l = ((zzax)localObject1).zzbw;
              localObject1 = new com/google/android/gms/internal/config/zzal;
              ((zzal)localObject1).<init>(k, l);
              ((Map)localObject3).put(localObject4, localObject1);
              j++;
              continue;
            }
          }
        }
        else
        {
          localObject5 = new com/google/android/gms/internal/config/zzar;
          ((zzar)localObject5).<init>();
          ((zzar)localObject5).zzf(((zzau)localObject4).zzbl);
          ((zzar)localObject5).zza(((zzau)localObject4).zzbm);
          ((zzar)localObject5).zzd(((zzau)localObject4).zzbn);
          continue;
        }
        ((zzar)localObject5).zza((Map)localObject3);
      }
      finally {}
      Object localObject4 = localzzao1;
      Object localObject3 = localzzao2;
      Object localObject2 = localzzao3;
      Object localObject1 = localObject5;
      continue;
      label323:
      localObject2 = null;
      localObject3 = null;
      localObject4 = null;
    }
  }
  
  private final long zzd(Context paramContext)
  {
    long l1 = 0L;
    try
    {
      l2 = Wrappers.packageManager(this.mContext).getPackageInfo(paramContext.getPackageName(), 0).lastUpdateTime;
      return l2;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        paramContext = paramContext.getPackageName();
        Log.e("FirebaseRemoteConfig", String.valueOf(paramContext).length() + 25 + "Package [" + paramContext + "] was not found!");
        long l2 = l1;
      }
    }
  }
  
  /* Error */
  private static zzaw zze(Context paramContext)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aload_0
    //   3: ifnonnull +7 -> 10
    //   6: aload_1
    //   7: astore_0
    //   8: aload_0
    //   9: areturn
    //   10: aload_0
    //   11: ldc_w 452
    //   14: invokevirtual 456	android/content/Context:openFileInput	(Ljava/lang/String;)Ljava/io/FileInputStream;
    //   17: astore_2
    //   18: aload_2
    //   19: astore_0
    //   20: new 458	java/io/ByteArrayOutputStream
    //   23: astore_3
    //   24: aload_2
    //   25: astore_0
    //   26: aload_3
    //   27: invokespecial 459	java/io/ByteArrayOutputStream:<init>	()V
    //   30: aload_2
    //   31: astore_0
    //   32: sipush 4096
    //   35: newarray <illegal type>
    //   37: astore 4
    //   39: aload_2
    //   40: astore_0
    //   41: aload_2
    //   42: aload 4
    //   44: invokevirtual 465	java/io/InputStream:read	([B)I
    //   47: istore 5
    //   49: iload 5
    //   51: iconst_m1
    //   52: if_icmpeq +77 -> 129
    //   55: aload_2
    //   56: astore_0
    //   57: aload_3
    //   58: aload 4
    //   60: iconst_0
    //   61: iload 5
    //   63: invokevirtual 471	java/io/OutputStream:write	([BII)V
    //   66: goto -27 -> 39
    //   69: astore 4
    //   71: aload_2
    //   72: astore_0
    //   73: ldc_w 267
    //   76: iconst_3
    //   77: invokestatic 350	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   80: ifeq +17 -> 97
    //   83: aload_2
    //   84: astore_0
    //   85: ldc_w 267
    //   88: ldc_w 473
    //   91: aload 4
    //   93: invokestatic 476	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   96: pop
    //   97: aload_1
    //   98: astore_0
    //   99: aload_2
    //   100: ifnull -92 -> 8
    //   103: aload_2
    //   104: invokevirtual 481	java/io/FileInputStream:close	()V
    //   107: aload_1
    //   108: astore_0
    //   109: goto -101 -> 8
    //   112: astore_0
    //   113: ldc_w 267
    //   116: ldc_w 483
    //   119: aload_0
    //   120: invokestatic 485	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   123: pop
    //   124: aload_1
    //   125: astore_0
    //   126: goto -118 -> 8
    //   129: aload_2
    //   130: astore_0
    //   131: aload_3
    //   132: invokevirtual 489	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   135: astore 4
    //   137: aload_2
    //   138: astore_0
    //   139: aload 4
    //   141: iconst_0
    //   142: aload 4
    //   144: arraylength
    //   145: invokestatic 494	com/google/android/gms/internal/config/zzay:zza	([BII)Lcom/google/android/gms/internal/config/zzay;
    //   148: astore_3
    //   149: aload_2
    //   150: astore_0
    //   151: new 361	com/google/android/gms/internal/config/zzaw
    //   154: astore 4
    //   156: aload_2
    //   157: astore_0
    //   158: aload 4
    //   160: invokespecial 495	com/google/android/gms/internal/config/zzaw:<init>	()V
    //   163: aload_2
    //   164: astore_0
    //   165: aload 4
    //   167: aload_3
    //   168: invokevirtual 500	com/google/android/gms/internal/config/zzbh:zza	(Lcom/google/android/gms/internal/config/zzay;)Lcom/google/android/gms/internal/config/zzbh;
    //   171: pop
    //   172: aload_2
    //   173: ifnull +7 -> 180
    //   176: aload_2
    //   177: invokevirtual 481	java/io/FileInputStream:close	()V
    //   180: aload 4
    //   182: astore_0
    //   183: goto -175 -> 8
    //   186: astore_0
    //   187: ldc_w 267
    //   190: ldc_w 483
    //   193: aload_0
    //   194: invokestatic 485	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   197: pop
    //   198: goto -18 -> 180
    //   201: astore 4
    //   203: aconst_null
    //   204: astore_2
    //   205: aload_2
    //   206: astore_0
    //   207: ldc_w 267
    //   210: ldc_w 502
    //   213: aload 4
    //   215: invokestatic 485	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   218: pop
    //   219: aload_1
    //   220: astore_0
    //   221: aload_2
    //   222: ifnull -214 -> 8
    //   225: aload_2
    //   226: invokevirtual 481	java/io/FileInputStream:close	()V
    //   229: aload_1
    //   230: astore_0
    //   231: goto -223 -> 8
    //   234: astore_0
    //   235: ldc_w 267
    //   238: ldc_w 483
    //   241: aload_0
    //   242: invokestatic 485	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   245: pop
    //   246: aload_1
    //   247: astore_0
    //   248: goto -240 -> 8
    //   251: astore_2
    //   252: aconst_null
    //   253: astore_0
    //   254: aload_0
    //   255: ifnull +7 -> 262
    //   258: aload_0
    //   259: invokevirtual 481	java/io/FileInputStream:close	()V
    //   262: aload_2
    //   263: athrow
    //   264: astore_0
    //   265: ldc_w 267
    //   268: ldc_w 483
    //   271: aload_0
    //   272: invokestatic 485	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   275: pop
    //   276: goto -14 -> 262
    //   279: astore_2
    //   280: goto -26 -> 254
    //   283: astore 4
    //   285: goto -80 -> 205
    //   288: astore 4
    //   290: aconst_null
    //   291: astore_2
    //   292: goto -221 -> 71
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	295	0	paramContext	Context
    //   1	246	1	localObject1	Object
    //   17	209	2	localFileInputStream	java.io.FileInputStream
    //   251	12	2	localObject2	Object
    //   279	1	2	localObject3	Object
    //   291	1	2	localObject4	Object
    //   23	145	3	localObject5	Object
    //   37	22	4	arrayOfByte	byte[]
    //   69	23	4	localFileNotFoundException1	java.io.FileNotFoundException
    //   135	46	4	localObject6	Object
    //   201	13	4	localIOException1	java.io.IOException
    //   283	1	4	localIOException2	java.io.IOException
    //   288	1	4	localFileNotFoundException2	java.io.FileNotFoundException
    //   47	15	5	i	int
    // Exception table:
    //   from	to	target	type
    //   20	24	69	java/io/FileNotFoundException
    //   26	30	69	java/io/FileNotFoundException
    //   32	39	69	java/io/FileNotFoundException
    //   41	49	69	java/io/FileNotFoundException
    //   57	66	69	java/io/FileNotFoundException
    //   131	137	69	java/io/FileNotFoundException
    //   139	149	69	java/io/FileNotFoundException
    //   151	156	69	java/io/FileNotFoundException
    //   158	163	69	java/io/FileNotFoundException
    //   165	172	69	java/io/FileNotFoundException
    //   103	107	112	java/io/IOException
    //   176	180	186	java/io/IOException
    //   10	18	201	java/io/IOException
    //   225	229	234	java/io/IOException
    //   10	18	251	finally
    //   258	262	264	java/io/IOException
    //   20	24	279	finally
    //   26	30	279	finally
    //   32	39	279	finally
    //   41	49	279	finally
    //   57	66	279	finally
    //   73	83	279	finally
    //   85	97	279	finally
    //   131	137	279	finally
    //   139	149	279	finally
    //   151	156	279	finally
    //   158	163	279	finally
    //   165	172	279	finally
    //   207	219	279	finally
    //   20	24	283	java/io/IOException
    //   26	30	283	java/io/IOException
    //   32	39	283	java/io/IOException
    //   41	49	283	java/io/IOException
    //   57	66	283	java/io/IOException
    //   131	137	283	java/io/IOException
    //   139	149	283	java/io/IOException
    //   151	156	283	java/io/IOException
    //   158	163	283	java/io/IOException
    //   165	172	283	java/io/IOException
    //   10	18	288	java/io/FileNotFoundException
  }
  
  private static FirebaseABTesting zzf(Context paramContext)
  {
    try
    {
      FirebaseABTesting localFirebaseABTesting = new com/google/firebase/abt/FirebaseABTesting;
      localFirebaseABTesting.<init>(paramContext, "frc", 1);
      paramContext = localFirebaseABTesting;
    }
    catch (NoClassDefFoundError paramContext)
    {
      for (;;)
      {
        Log.w("FirebaseRemoteConfig", "Unable to use ABT: Analytics library is missing.");
        paramContext = null;
      }
    }
    return paramContext;
  }
  
  private final void zzn()
  {
    this.zzal.readLock().lock();
    try
    {
      zzan localzzan = new com/google/android/gms/internal/config/zzan;
      localzzan.<init>(this.mContext, this.zzag, this.zzah, this.zzai, this.zzaj);
      zza(localzzan);
      return;
    }
    finally
    {
      this.zzal.readLock().unlock();
    }
  }
  
  /* Error */
  public boolean activateFetched()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   6: invokeinterface 278 1 0
    //   11: invokeinterface 160 1 0
    //   16: aload_0
    //   17: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   20: astore_2
    //   21: aload_2
    //   22: ifnonnull +19 -> 41
    //   25: aload_0
    //   26: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   29: invokeinterface 278 1 0
    //   34: invokeinterface 258 1 0
    //   39: iload_1
    //   40: ireturn
    //   41: aload_0
    //   42: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   45: ifnull +44 -> 89
    //   48: aload_0
    //   49: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   52: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   55: lstore_3
    //   56: aload_0
    //   57: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   60: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   63: lstore 5
    //   65: lload_3
    //   66: lload 5
    //   68: lcmp
    //   69: iflt +20 -> 89
    //   72: aload_0
    //   73: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   76: invokeinterface 278 1 0
    //   81: invokeinterface 258 1 0
    //   86: goto -47 -> 39
    //   89: aload_0
    //   90: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   93: invokevirtual 197	com/google/android/gms/internal/config/zzao:getTimestamp	()J
    //   96: lstore 5
    //   98: aload_0
    //   99: aload_0
    //   100: getfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   103: putfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   106: aload_0
    //   107: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   110: invokestatic 204	java/lang/System:currentTimeMillis	()J
    //   113: invokevirtual 522	com/google/android/gms/internal/config/zzao:setTimestamp	(J)V
    //   116: new 137	com/google/android/gms/internal/config/zzao
    //   119: astore_2
    //   120: aload_2
    //   121: aconst_null
    //   122: lload 5
    //   124: aconst_null
    //   125: invokespecial 144	com/google/android/gms/internal/config/zzao:<init>	(Ljava/util/Map;JLjava/util/List;)V
    //   128: aload_0
    //   129: aload_2
    //   130: putfield 57	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzag	Lcom/google/android/gms/internal/config/zzao;
    //   133: aload_0
    //   134: getfield 59	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzah	Lcom/google/android/gms/internal/config/zzao;
    //   137: invokevirtual 526	com/google/android/gms/internal/config/zzao:zzg	()Ljava/util/List;
    //   140: astore 7
    //   142: new 528	com/google/android/gms/internal/config/zzam
    //   145: astore_2
    //   146: aload_2
    //   147: aload_0
    //   148: getfield 75	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzam	Lcom/google/firebase/abt/FirebaseABTesting;
    //   151: aload 7
    //   153: invokespecial 531	com/google/android/gms/internal/config/zzam:<init>	(Lcom/google/firebase/abt/FirebaseABTesting;Ljava/util/List;)V
    //   156: aload_2
    //   157: invokestatic 518	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zza	(Ljava/lang/Runnable;)V
    //   160: aload_0
    //   161: invokespecial 291	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzn	()V
    //   164: aload_0
    //   165: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   168: invokeinterface 278 1 0
    //   173: invokeinterface 258 1 0
    //   178: iconst_1
    //   179: istore_1
    //   180: goto -141 -> 39
    //   183: astore_2
    //   184: aload_0
    //   185: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   188: invokeinterface 278 1 0
    //   193: invokeinterface 258 1 0
    //   198: aload_2
    //   199: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	200	0	this	FirebaseRemoteConfig
    //   1	179	1	bool	boolean
    //   20	137	2	localObject1	Object
    //   183	16	2	localObject2	Object
    //   55	11	3	l1	long
    //   63	60	5	l2	long
    //   140	12	7	localList	List
    // Exception table:
    //   from	to	target	type
    //   16	21	183	finally
    //   41	65	183	finally
    //   89	164	183	finally
  }
  
  public Task<Void> fetch(long paramLong)
  {
    return zza(paramLong, new zzv(this.mContext));
  }
  
  public String getString(String paramString)
  {
    return getString(paramString, "configns:firebase");
  }
  
  public String getString(String paramString1, String paramString2)
  {
    if (paramString2 == null) {
      paramString1 = "";
    }
    for (;;)
    {
      return paramString1;
      this.zzal.readLock().lock();
      try
      {
        if ((this.zzah != null) && (this.zzah.zzb(paramString1, paramString2)))
        {
          paramString1 = new String(this.zzah.zzc(paramString1, paramString2), zzaq.UTF_8);
          this.zzal.readLock().unlock();
          continue;
        }
        if ((this.zzai != null) && (this.zzai.zzb(paramString1, paramString2)))
        {
          paramString1 = new String(this.zzai.zzc(paramString1, paramString2), zzaq.UTF_8);
          this.zzal.readLock().unlock();
          continue;
        }
        paramString1 = "";
        this.zzal.readLock().unlock();
      }
      finally
      {
        this.zzal.readLock().unlock();
      }
    }
  }
  
  /* Error */
  public void setConfigSettings(FirebaseRemoteConfigSettings paramFirebaseRemoteConfigSettings)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   4: invokeinterface 278 1 0
    //   9: invokeinterface 160 1 0
    //   14: aload_0
    //   15: getfield 45	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   18: invokevirtual 183	com/google/android/gms/internal/config/zzar:isDeveloperModeEnabled	()Z
    //   21: istore_2
    //   22: aload_1
    //   23: ifnonnull +37 -> 60
    //   26: iconst_0
    //   27: istore_3
    //   28: aload_0
    //   29: getfield 45	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   32: iload_3
    //   33: invokevirtual 407	com/google/android/gms/internal/config/zzar:zza	(Z)V
    //   36: iload_2
    //   37: iload_3
    //   38: if_icmpeq +7 -> 45
    //   41: aload_0
    //   42: invokespecial 291	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzn	()V
    //   45: aload_0
    //   46: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   49: invokeinterface 278 1 0
    //   54: invokeinterface 258 1 0
    //   59: return
    //   60: aload_1
    //   61: invokevirtual 568	com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings:isDeveloperModeEnabled	()Z
    //   64: istore_3
    //   65: goto -37 -> 28
    //   68: astore_1
    //   69: aload_0
    //   70: getfield 41	com/google/firebase/remoteconfig/FirebaseRemoteConfig:zzal	Ljava/util/concurrent/locks/ReadWriteLock;
    //   73: invokeinterface 278 1 0
    //   78: invokeinterface 258 1 0
    //   83: aload_1
    //   84: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	85	0	this	FirebaseRemoteConfig
    //   0	85	1	paramFirebaseRemoteConfigSettings	FirebaseRemoteConfigSettings
    //   21	18	2	bool1	boolean
    //   27	38	3	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   14	22	68	finally
    //   28	36	68	finally
    //   41	45	68	finally
    //   60	65	68	finally
  }
  
  final void zza(TaskCompletionSource<Void> paramTaskCompletionSource, zzk paramzzk)
  {
    if ((paramzzk == null) || (paramzzk.getStatus() == null))
    {
      zza(paramTaskCompletionSource, null);
      return;
    }
    int i = paramzzk.getStatus().getStatusCode();
    this.zzal.writeLock().lock();
    switch (i)
    {
    }
    for (;;)
    {
      try
      {
        if (paramzzk.getStatus().isSuccess())
        {
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>(45);
          Log.w("FirebaseRemoteConfig", "Unknown (successful) status code: " + i);
        }
        zza(paramTaskCompletionSource, paramzzk.getStatus());
        this.zzal.writeLock().unlock();
        break;
      }
      finally
      {
        this.zzal.writeLock().unlock();
      }
      zza(paramTaskCompletionSource, paramzzk.getStatus());
      continue;
      this.zzaj.zzf(2);
      Object localObject1 = new com/google/firebase/remoteconfig/FirebaseRemoteConfigFetchThrottledException;
      ((FirebaseRemoteConfigFetchThrottledException)localObject1).<init>(paramzzk.getThrottleEndTimeMillis());
      paramTaskCompletionSource.setException((Exception)localObject1);
      zzn();
      continue;
      this.zzaj.zzf(-1);
      Object localObject2;
      Object localObject3;
      HashMap localHashMap;
      if ((this.zzag != null) && (!this.zzag.zzq()))
      {
        localMap = paramzzk.zzh();
        localObject1 = new java/util/HashMap;
        ((HashMap)localObject1).<init>();
        localObject2 = localMap.keySet().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (String)((Iterator)localObject2).next();
          localHashMap = new java/util/HashMap;
          localHashMap.<init>();
          localObject4 = ((Set)localMap.get(localObject3)).iterator();
          while (((Iterator)localObject4).hasNext())
          {
            localObject5 = (String)((Iterator)localObject4).next();
            localHashMap.put(localObject5, paramzzk.zza((String)localObject5, null, (String)localObject3));
          }
          ((Map)localObject1).put(localObject3, localHashMap);
        }
        localObject4 = new com/google/android/gms/internal/config/zzao;
        ((zzao)localObject4).<init>((Map)localObject1, this.zzag.getTimestamp(), paramzzk.zzg());
        this.zzag = ((zzao)localObject4);
      }
      paramTaskCompletionSource.setResult(null);
      zzn();
      continue;
      Map localMap = paramzzk.zzh();
      localObject1 = new java/util/HashMap;
      ((HashMap)localObject1).<init>();
      Object localObject5 = localMap.keySet().iterator();
      while (((Iterator)localObject5).hasNext())
      {
        localObject4 = (String)((Iterator)localObject5).next();
        localHashMap = new java/util/HashMap;
        localHashMap.<init>();
        localObject3 = ((Set)localMap.get(localObject4)).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject2 = (String)((Iterator)localObject3).next();
          localHashMap.put(localObject2, paramzzk.zza((String)localObject2, null, (String)localObject4));
        }
        ((Map)localObject1).put(localObject4, localHashMap);
      }
      Object localObject4 = new com/google/android/gms/internal/config/zzao;
      ((zzao)localObject4).<init>((Map)localObject1, System.currentTimeMillis(), paramzzk.zzg());
      this.zzag = ((zzao)localObject4);
      this.zzaj.zzf(-1);
      paramTaskCompletionSource.setResult(null);
      zzn();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */