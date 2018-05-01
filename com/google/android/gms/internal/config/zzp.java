package com.google.android.gms.internal.config;

import com.google.android.gms.common.api.GoogleApiClient;

final class zzp
  extends zzs
{
  zzp(zzo paramzzo, GoogleApiClient paramGoogleApiClient, zzi paramzzi)
  {
    super(paramGoogleApiClient);
  }
  
  /* Error */
  protected final void zza(android.content.Context paramContext, zzah paramzzah)
    throws android.os.RemoteException
  {
    // Byte code:
    //   0: invokestatic 38	com/google/android/gms/common/data/DataBufferSafeParcelable:buildDataHolder	()Lcom/google/android/gms/common/data/DataHolder$Builder;
    //   3: astore_3
    //   4: aload_0
    //   5: getfield 10	com/google/android/gms/internal/config/zzp:zzn	Lcom/google/android/gms/internal/config/zzi;
    //   8: invokevirtual 44	com/google/android/gms/internal/config/zzi:zzb	()Ljava/util/Map;
    //   11: invokeinterface 50 1 0
    //   16: invokeinterface 56 1 0
    //   21: astore 4
    //   23: aload 4
    //   25: invokeinterface 62 1 0
    //   30: ifeq +49 -> 79
    //   33: aload 4
    //   35: invokeinterface 66 1 0
    //   40: checkcast 68	java/util/Map$Entry
    //   43: astore 5
    //   45: aload_3
    //   46: new 70	com/google/android/gms/internal/config/zzz
    //   49: dup
    //   50: aload 5
    //   52: invokeinterface 73 1 0
    //   57: checkcast 75	java/lang/String
    //   60: aload 5
    //   62: invokeinterface 78 1 0
    //   67: checkcast 75	java/lang/String
    //   70: invokespecial 81	com/google/android/gms/internal/config/zzz:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   73: invokestatic 85	com/google/android/gms/common/data/DataBufferSafeParcelable:addValue	(Lcom/google/android/gms/common/data/DataHolder$Builder;Lcom/google/android/gms/common/internal/safeparcel/SafeParcelable;)V
    //   76: goto -53 -> 23
    //   79: aload_3
    //   80: iconst_0
    //   81: invokevirtual 91	com/google/android/gms/common/data/DataHolder$Builder:build	(I)Lcom/google/android/gms/common/data/DataHolder;
    //   84: astore_3
    //   85: invokestatic 97	com/google/firebase/iid/FirebaseInstanceId:getInstance	()Lcom/google/firebase/iid/FirebaseInstanceId;
    //   88: invokevirtual 101	com/google/firebase/iid/FirebaseInstanceId:getId	()Ljava/lang/String;
    //   91: astore 4
    //   93: invokestatic 97	com/google/firebase/iid/FirebaseInstanceId:getInstance	()Lcom/google/firebase/iid/FirebaseInstanceId;
    //   96: invokevirtual 104	com/google/firebase/iid/FirebaseInstanceId:getToken	()Ljava/lang/String;
    //   99: astore 5
    //   101: aload_1
    //   102: invokestatic 109	com/google/android/gms/internal/config/zzn:zzb	(Landroid/content/Context;)Ljava/util/List;
    //   105: astore 6
    //   107: new 111	com/google/android/gms/internal/config/zzab
    //   110: dup
    //   111: aload_1
    //   112: invokevirtual 116	android/content/Context:getPackageName	()Ljava/lang/String;
    //   115: aload_0
    //   116: getfield 10	com/google/android/gms/internal/config/zzp:zzn	Lcom/google/android/gms/internal/config/zzi;
    //   119: invokevirtual 119	com/google/android/gms/internal/config/zzi:zza	()J
    //   122: aload_3
    //   123: aload_0
    //   124: getfield 10	com/google/android/gms/internal/config/zzp:zzn	Lcom/google/android/gms/internal/config/zzi;
    //   127: invokevirtual 122	com/google/android/gms/internal/config/zzi:getGmpAppId	()Ljava/lang/String;
    //   130: aload 4
    //   132: aload 5
    //   134: aconst_null
    //   135: aload_0
    //   136: getfield 10	com/google/android/gms/internal/config/zzp:zzn	Lcom/google/android/gms/internal/config/zzi;
    //   139: invokevirtual 126	com/google/android/gms/internal/config/zzi:zzc	()I
    //   142: aload 6
    //   144: aload_0
    //   145: getfield 10	com/google/android/gms/internal/config/zzp:zzn	Lcom/google/android/gms/internal/config/zzi;
    //   148: invokevirtual 129	com/google/android/gms/internal/config/zzi:zzd	()I
    //   151: aload_0
    //   152: getfield 10	com/google/android/gms/internal/config/zzp:zzn	Lcom/google/android/gms/internal/config/zzi;
    //   155: invokevirtual 132	com/google/android/gms/internal/config/zzi:zze	()I
    //   158: invokespecial 135	com/google/android/gms/internal/config/zzab:<init>	(Ljava/lang/String;JLcom/google/android/gms/common/data/DataHolder;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;ILjava/util/List;II)V
    //   161: astore_1
    //   162: aload_2
    //   163: aload_0
    //   164: getfield 139	com/google/android/gms/internal/config/zzp:zzo	Lcom/google/android/gms/internal/config/zzaf;
    //   167: aload_1
    //   168: invokeinterface 144 3 0
    //   173: aload_3
    //   174: invokevirtual 149	com/google/android/gms/common/data/DataHolder:close	()V
    //   177: return
    //   178: astore 5
    //   180: aconst_null
    //   181: astore 4
    //   183: ldc -105
    //   185: iconst_3
    //   186: invokestatic 157	android/util/Log:isLoggable	(Ljava/lang/String;I)Z
    //   189: ifeq +13 -> 202
    //   192: ldc -105
    //   194: ldc -97
    //   196: aload 5
    //   198: invokestatic 163	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   201: pop
    //   202: aconst_null
    //   203: astore 5
    //   205: goto -104 -> 101
    //   208: astore_1
    //   209: aload_3
    //   210: invokevirtual 149	com/google/android/gms/common/data/DataHolder:close	()V
    //   213: aload_1
    //   214: athrow
    //   215: astore 5
    //   217: goto -34 -> 183
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	220	0	this	zzp
    //   0	220	1	paramContext	android.content.Context
    //   0	220	2	paramzzah	zzah
    //   3	207	3	localObject1	Object
    //   21	161	4	localObject2	Object
    //   43	90	5	localObject3	Object
    //   178	19	5	localIllegalStateException1	IllegalStateException
    //   203	1	5	localObject4	Object
    //   215	1	5	localIllegalStateException2	IllegalStateException
    //   105	38	6	localList	java.util.List
    // Exception table:
    //   from	to	target	type
    //   85	93	178	java/lang/IllegalStateException
    //   162	173	208	finally
    //   93	101	215	java/lang/IllegalStateException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */