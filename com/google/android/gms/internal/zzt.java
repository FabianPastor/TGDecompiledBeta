package com.google.android.gms.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.impl.cookie.DateUtils;

public class zzt
  implements zzf
{
  protected static final boolean DEBUG = zzs.DEBUG;
  private static int zzbn = 3000;
  private static int zzbo = 4096;
  protected final zzy zzbp;
  protected final zzu zzbq;
  
  public zzt(zzy paramzzy)
  {
    this(paramzzy, new zzu(zzbo));
  }
  
  public zzt(zzy paramzzy, zzu paramzzu)
  {
    this.zzbp = paramzzy;
    this.zzbq = paramzzu;
  }
  
  protected static Map<String, String> zza(Header[] paramArrayOfHeader)
  {
    TreeMap localTreeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    int i = 0;
    while (i < paramArrayOfHeader.length)
    {
      localTreeMap.put(paramArrayOfHeader[i].getName(), paramArrayOfHeader[i].getValue());
      i += 1;
    }
    return localTreeMap;
  }
  
  private void zza(long paramLong, zzk<?> paramzzk, byte[] paramArrayOfByte, StatusLine paramStatusLine)
  {
    if ((DEBUG) || (paramLong > zzbn)) {
      if (paramArrayOfByte == null) {
        break label82;
      }
    }
    label82:
    for (paramArrayOfByte = Integer.valueOf(paramArrayOfByte.length);; paramArrayOfByte = "null")
    {
      zzs.zzb("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", new Object[] { paramzzk, Long.valueOf(paramLong), paramArrayOfByte, Integer.valueOf(paramStatusLine.getStatusCode()), Integer.valueOf(paramzzk.zzt().zzd()) });
      return;
    }
  }
  
  private static void zza(String paramString, zzk<?> paramzzk, zzr paramzzr)
    throws zzr
  {
    zzo localzzo = paramzzk.zzt();
    int i = paramzzk.zzs();
    try
    {
      localzzo.zza(paramzzr);
      paramzzk.zzc(String.format("%s-retry [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      return;
    }
    catch (zzr paramzzr)
    {
      paramzzk.zzc(String.format("%s-timeout-giveup [timeout=%s]", new Object[] { paramString, Integer.valueOf(i) }));
      throw paramzzr;
    }
  }
  
  private void zza(Map<String, String> paramMap, zzb.zza paramzza)
  {
    if (paramzza == null) {}
    do
    {
      return;
      if (paramzza.zza != null) {
        paramMap.put("If-None-Match", paramzza.zza);
      }
    } while (paramzza.zzc <= 0L);
    paramMap.put("If-Modified-Since", DateUtils.formatDate(new Date(paramzza.zzc)));
  }
  
  private byte[] zza(HttpEntity paramHttpEntity)
    throws IOException, zzp
  {
    zzaa localzzaa = new zzaa(this.zzbq, (int)paramHttpEntity.getContentLength());
    Object localObject2 = null;
    Object localObject1 = localObject2;
    Object localObject4;
    try
    {
      localObject4 = paramHttpEntity.getContent();
      if (localObject4 == null)
      {
        localObject1 = localObject2;
        throw new zzp();
      }
    }
    finally {}
    try
    {
      paramHttpEntity.consumeContent();
      this.zzbq.zza((byte[])localObject1);
      localzzaa.close();
      throw ((Throwable)localObject3);
      localObject1 = localObject3;
      byte[] arrayOfByte = this.zzbq.zzb(1024);
      for (;;)
      {
        localObject1 = arrayOfByte;
        int i = ((InputStream)localObject4).read(arrayOfByte);
        if (i == -1) {
          break;
        }
        localObject1 = arrayOfByte;
        localzzaa.write(arrayOfByte, 0, i);
      }
      localObject1 = arrayOfByte;
      localObject4 = localzzaa.toByteArray();
      try
      {
        paramHttpEntity.consumeContent();
        this.zzbq.zza(arrayOfByte);
        localzzaa.close();
        return (byte[])localObject4;
      }
      catch (IOException paramHttpEntity)
      {
        for (;;)
        {
          zzs.zza("Error occured when calling consumingContent", new Object[0]);
        }
      }
    }
    catch (IOException paramHttpEntity)
    {
      for (;;)
      {
        zzs.zza("Error occured when calling consumingContent", new Object[0]);
      }
    }
  }
  
  /* Error */
  public zzi zza(zzk<?> paramzzk)
    throws zzr
  {
    // Byte code:
    //   0: invokestatic 222	android/os/SystemClock:elapsedRealtime	()J
    //   3: lstore_3
    //   4: aconst_null
    //   5: astore 9
    //   7: invokestatic 228	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   10: astore 7
    //   12: new 230	java/util/HashMap
    //   15: dup
    //   16: invokespecial 231	java/util/HashMap:<init>	()V
    //   19: astore 5
    //   21: aload_0
    //   22: aload 5
    //   24: aload_1
    //   25: invokevirtual 235	com/google/android/gms/internal/zzk:zzh	()Lcom/google/android/gms/internal/zzb$zza;
    //   28: invokespecial 237	com/google/android/gms/internal/zzt:zza	(Ljava/util/Map;Lcom/google/android/gms/internal/zzb$zza;)V
    //   31: aload_0
    //   32: getfield 41	com/google/android/gms/internal/zzt:zzbp	Lcom/google/android/gms/internal/zzy;
    //   35: aload_1
    //   36: aload 5
    //   38: invokeinterface 242 3 0
    //   43: astore 6
    //   45: aload 7
    //   47: astore 5
    //   49: aload 6
    //   51: invokeinterface 248 1 0
    //   56: astore 8
    //   58: aload 7
    //   60: astore 5
    //   62: aload 8
    //   64: invokeinterface 93 1 0
    //   69: istore_2
    //   70: aload 7
    //   72: astore 5
    //   74: aload 6
    //   76: invokeinterface 252 1 0
    //   81: invokestatic 254	com/google/android/gms/internal/zzt:zza	([Lorg/apache/http/Header;)Ljava/util/Map;
    //   84: astore 9
    //   86: iload_2
    //   87: sipush 304
    //   90: if_icmpne +89 -> 179
    //   93: aload 9
    //   95: astore 5
    //   97: aload_1
    //   98: invokevirtual 235	com/google/android/gms/internal/zzk:zzh	()Lcom/google/android/gms/internal/zzb$zza;
    //   101: astore 7
    //   103: aload 7
    //   105: ifnonnull +27 -> 132
    //   108: aload 9
    //   110: astore 5
    //   112: new 256	com/google/android/gms/internal/zzi
    //   115: dup
    //   116: sipush 304
    //   119: aconst_null
    //   120: aload 9
    //   122: iconst_1
    //   123: invokestatic 222	android/os/SystemClock:elapsedRealtime	()J
    //   126: lload_3
    //   127: lsub
    //   128: invokespecial 259	com/google/android/gms/internal/zzi:<init>	(I[BLjava/util/Map;ZJ)V
    //   131: areturn
    //   132: aload 9
    //   134: astore 5
    //   136: aload 7
    //   138: getfield 263	com/google/android/gms/internal/zzb$zza:zzf	Ljava/util/Map;
    //   141: aload 9
    //   143: invokeinterface 267 2 0
    //   148: aload 9
    //   150: astore 5
    //   152: new 256	com/google/android/gms/internal/zzi
    //   155: dup
    //   156: sipush 304
    //   159: aload 7
    //   161: getfield 271	com/google/android/gms/internal/zzb$zza:data	[B
    //   164: aload 7
    //   166: getfield 263	com/google/android/gms/internal/zzb$zza:zzf	Ljava/util/Map;
    //   169: iconst_1
    //   170: invokestatic 222	android/os/SystemClock:elapsedRealtime	()J
    //   173: lload_3
    //   174: lsub
    //   175: invokespecial 259	com/google/android/gms/internal/zzi:<init>	(I[BLjava/util/Map;ZJ)V
    //   178: areturn
    //   179: aload 9
    //   181: astore 5
    //   183: aload 6
    //   185: invokeinterface 275 1 0
    //   190: ifnull +79 -> 269
    //   193: aload 9
    //   195: astore 5
    //   197: aload_0
    //   198: aload 6
    //   200: invokeinterface 275 1 0
    //   205: invokespecial 277	com/google/android/gms/internal/zzt:zza	(Lorg/apache/http/HttpEntity;)[B
    //   208: astore 7
    //   210: aload 7
    //   212: astore 5
    //   214: aload_0
    //   215: invokestatic 222	android/os/SystemClock:elapsedRealtime	()J
    //   218: lload_3
    //   219: lsub
    //   220: aload_1
    //   221: aload 5
    //   223: aload 8
    //   225: invokespecial 279	com/google/android/gms/internal/zzt:zza	(JLcom/google/android/gms/internal/zzk;[BLorg/apache/http/StatusLine;)V
    //   228: iload_2
    //   229: sipush 200
    //   232: if_icmplt +10 -> 242
    //   235: iload_2
    //   236: sipush 299
    //   239: if_icmple +46 -> 285
    //   242: new 162	java/io/IOException
    //   245: dup
    //   246: invokespecial 280	java/io/IOException:<init>	()V
    //   249: athrow
    //   250: astore 5
    //   252: ldc_w 282
    //   255: aload_1
    //   256: new 284	com/google/android/gms/internal/zzq
    //   259: dup
    //   260: invokespecial 285	com/google/android/gms/internal/zzq:<init>	()V
    //   263: invokestatic 287	com/google/android/gms/internal/zzt:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzk;Lcom/google/android/gms/internal/zzr;)V
    //   266: goto -262 -> 4
    //   269: aload 9
    //   271: astore 5
    //   273: iconst_0
    //   274: newarray <illegal type>
    //   276: astore 7
    //   278: aload 7
    //   280: astore 5
    //   282: goto -68 -> 214
    //   285: new 256	com/google/android/gms/internal/zzi
    //   288: dup
    //   289: iload_2
    //   290: aload 5
    //   292: aload 9
    //   294: iconst_0
    //   295: invokestatic 222	android/os/SystemClock:elapsedRealtime	()J
    //   298: lload_3
    //   299: lsub
    //   300: invokespecial 259	com/google/android/gms/internal/zzi:<init>	(I[BLjava/util/Map;ZJ)V
    //   303: astore 7
    //   305: aload 7
    //   307: areturn
    //   308: astore 5
    //   310: ldc_w 289
    //   313: aload_1
    //   314: new 284	com/google/android/gms/internal/zzq
    //   317: dup
    //   318: invokespecial 285	com/google/android/gms/internal/zzq:<init>	()V
    //   321: invokestatic 287	com/google/android/gms/internal/zzt:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzk;Lcom/google/android/gms/internal/zzr;)V
    //   324: goto -320 -> 4
    //   327: astore 5
    //   329: aload_1
    //   330: invokevirtual 292	com/google/android/gms/internal/zzk:getUrl	()Ljava/lang/String;
    //   333: invokestatic 295	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   336: astore_1
    //   337: aload_1
    //   338: invokevirtual 298	java/lang/String:length	()I
    //   341: ifeq +22 -> 363
    //   344: ldc_w 300
    //   347: aload_1
    //   348: invokevirtual 304	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   351: astore_1
    //   352: new 306	java/lang/RuntimeException
    //   355: dup
    //   356: aload_1
    //   357: aload 5
    //   359: invokespecial 309	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   362: athrow
    //   363: new 49	java/lang/String
    //   366: dup
    //   367: ldc_w 300
    //   370: invokespecial 311	java/lang/String:<init>	(Ljava/lang/String;)V
    //   373: astore_1
    //   374: goto -22 -> 352
    //   377: astore 6
    //   379: aconst_null
    //   380: astore 8
    //   382: aload 7
    //   384: astore 5
    //   386: aload 9
    //   388: astore 7
    //   390: aload 7
    //   392: ifnull +98 -> 490
    //   395: aload 7
    //   397: invokeinterface 248 1 0
    //   402: invokeinterface 93 1 0
    //   407: istore_2
    //   408: ldc_w 313
    //   411: iconst_2
    //   412: anewarray 4	java/lang/Object
    //   415: dup
    //   416: iconst_0
    //   417: iload_2
    //   418: invokestatic 80	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   421: aastore
    //   422: dup
    //   423: iconst_1
    //   424: aload_1
    //   425: invokevirtual 292	com/google/android/gms/internal/zzk:getUrl	()Ljava/lang/String;
    //   428: aastore
    //   429: invokestatic 315	com/google/android/gms/internal/zzs:zzc	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   432: aload 8
    //   434: ifnull +76 -> 510
    //   437: new 256	com/google/android/gms/internal/zzi
    //   440: dup
    //   441: iload_2
    //   442: aload 8
    //   444: aload 5
    //   446: iconst_0
    //   447: invokestatic 222	android/os/SystemClock:elapsedRealtime	()J
    //   450: lload_3
    //   451: lsub
    //   452: invokespecial 259	com/google/android/gms/internal/zzi:<init>	(I[BLjava/util/Map;ZJ)V
    //   455: astore 5
    //   457: iload_2
    //   458: sipush 401
    //   461: if_icmpeq +10 -> 471
    //   464: iload_2
    //   465: sipush 403
    //   468: if_icmpne +32 -> 500
    //   471: ldc_w 317
    //   474: aload_1
    //   475: new 319	com/google/android/gms/internal/zza
    //   478: dup
    //   479: aload 5
    //   481: invokespecial 322	com/google/android/gms/internal/zza:<init>	(Lcom/google/android/gms/internal/zzi;)V
    //   484: invokestatic 287	com/google/android/gms/internal/zzt:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzk;Lcom/google/android/gms/internal/zzr;)V
    //   487: goto -483 -> 4
    //   490: new 324	com/google/android/gms/internal/zzj
    //   493: dup
    //   494: aload 6
    //   496: invokespecial 327	com/google/android/gms/internal/zzj:<init>	(Ljava/lang/Throwable;)V
    //   499: athrow
    //   500: new 164	com/google/android/gms/internal/zzp
    //   503: dup
    //   504: aload 5
    //   506: invokespecial 328	com/google/android/gms/internal/zzp:<init>	(Lcom/google/android/gms/internal/zzi;)V
    //   509: athrow
    //   510: new 330	com/google/android/gms/internal/zzh
    //   513: dup
    //   514: aconst_null
    //   515: invokespecial 331	com/google/android/gms/internal/zzh:<init>	(Lcom/google/android/gms/internal/zzi;)V
    //   518: athrow
    //   519: astore 9
    //   521: aconst_null
    //   522: astore 8
    //   524: aload 6
    //   526: astore 7
    //   528: aload 9
    //   530: astore 6
    //   532: goto -142 -> 390
    //   535: astore 8
    //   537: aload 6
    //   539: astore 7
    //   541: aload 8
    //   543: astore 6
    //   545: aload 5
    //   547: astore 8
    //   549: aload 9
    //   551: astore 5
    //   553: goto -163 -> 390
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	556	0	this	zzt
    //   0	556	1	paramzzk	zzk<?>
    //   69	400	2	i	int
    //   3	448	3	l	long
    //   19	203	5	localObject1	Object
    //   250	1	5	localSocketTimeoutException	java.net.SocketTimeoutException
    //   271	20	5	localObject2	Object
    //   308	1	5	localConnectTimeoutException	org.apache.http.conn.ConnectTimeoutException
    //   327	31	5	localMalformedURLException	java.net.MalformedURLException
    //   384	168	5	localObject3	Object
    //   43	156	6	localHttpResponse	org.apache.http.HttpResponse
    //   377	148	6	localIOException1	IOException
    //   530	14	6	localObject4	Object
    //   10	530	7	localObject5	Object
    //   56	467	8	localStatusLine	StatusLine
    //   535	7	8	localIOException2	IOException
    //   547	1	8	localObject6	Object
    //   5	382	9	localMap	Map
    //   519	31	9	localIOException3	IOException
    // Exception table:
    //   from	to	target	type
    //   12	45	250	java/net/SocketTimeoutException
    //   49	58	250	java/net/SocketTimeoutException
    //   62	70	250	java/net/SocketTimeoutException
    //   74	86	250	java/net/SocketTimeoutException
    //   97	103	250	java/net/SocketTimeoutException
    //   112	132	250	java/net/SocketTimeoutException
    //   136	148	250	java/net/SocketTimeoutException
    //   152	179	250	java/net/SocketTimeoutException
    //   183	193	250	java/net/SocketTimeoutException
    //   197	210	250	java/net/SocketTimeoutException
    //   214	228	250	java/net/SocketTimeoutException
    //   242	250	250	java/net/SocketTimeoutException
    //   273	278	250	java/net/SocketTimeoutException
    //   285	305	250	java/net/SocketTimeoutException
    //   12	45	308	org/apache/http/conn/ConnectTimeoutException
    //   49	58	308	org/apache/http/conn/ConnectTimeoutException
    //   62	70	308	org/apache/http/conn/ConnectTimeoutException
    //   74	86	308	org/apache/http/conn/ConnectTimeoutException
    //   97	103	308	org/apache/http/conn/ConnectTimeoutException
    //   112	132	308	org/apache/http/conn/ConnectTimeoutException
    //   136	148	308	org/apache/http/conn/ConnectTimeoutException
    //   152	179	308	org/apache/http/conn/ConnectTimeoutException
    //   183	193	308	org/apache/http/conn/ConnectTimeoutException
    //   197	210	308	org/apache/http/conn/ConnectTimeoutException
    //   214	228	308	org/apache/http/conn/ConnectTimeoutException
    //   242	250	308	org/apache/http/conn/ConnectTimeoutException
    //   273	278	308	org/apache/http/conn/ConnectTimeoutException
    //   285	305	308	org/apache/http/conn/ConnectTimeoutException
    //   12	45	327	java/net/MalformedURLException
    //   49	58	327	java/net/MalformedURLException
    //   62	70	327	java/net/MalformedURLException
    //   74	86	327	java/net/MalformedURLException
    //   97	103	327	java/net/MalformedURLException
    //   112	132	327	java/net/MalformedURLException
    //   136	148	327	java/net/MalformedURLException
    //   152	179	327	java/net/MalformedURLException
    //   183	193	327	java/net/MalformedURLException
    //   197	210	327	java/net/MalformedURLException
    //   214	228	327	java/net/MalformedURLException
    //   242	250	327	java/net/MalformedURLException
    //   273	278	327	java/net/MalformedURLException
    //   285	305	327	java/net/MalformedURLException
    //   12	45	377	java/io/IOException
    //   49	58	519	java/io/IOException
    //   62	70	519	java/io/IOException
    //   74	86	519	java/io/IOException
    //   97	103	519	java/io/IOException
    //   112	132	519	java/io/IOException
    //   136	148	519	java/io/IOException
    //   152	179	519	java/io/IOException
    //   183	193	519	java/io/IOException
    //   197	210	519	java/io/IOException
    //   273	278	519	java/io/IOException
    //   214	228	535	java/io/IOException
    //   242	250	535	java/io/IOException
    //   285	305	535	java/io/IOException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */