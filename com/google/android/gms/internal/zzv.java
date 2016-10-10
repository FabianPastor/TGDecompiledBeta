package com.google.android.gms.internal;

import android.os.SystemClock;
import java.io.EOFException;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class zzv
  implements zzb
{
  private final Map<String, zza> zzbw = new LinkedHashMap(16, 0.75F, true);
  private long zzbx = 0L;
  private final File zzby;
  private final int zzbz;
  
  public zzv(File paramFile)
  {
    this(paramFile, 5242880);
  }
  
  public zzv(File paramFile, int paramInt)
  {
    this.zzby = paramFile;
    this.zzbz = paramInt;
  }
  
  private void removeEntry(String paramString)
  {
    zza localzza = (zza)this.zzbw.get(paramString);
    if (localzza != null)
    {
      this.zzbx -= localzza.zzca;
      this.zzbw.remove(paramString);
    }
  }
  
  private static int zza(InputStream paramInputStream)
    throws IOException
  {
    int i = paramInputStream.read();
    if (i == -1) {
      throw new EOFException();
    }
    return i;
  }
  
  static void zza(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write(paramInt >> 0 & 0xFF);
    paramOutputStream.write(paramInt >> 8 & 0xFF);
    paramOutputStream.write(paramInt >> 16 & 0xFF);
    paramOutputStream.write(paramInt >> 24 & 0xFF);
  }
  
  static void zza(OutputStream paramOutputStream, long paramLong)
    throws IOException
  {
    paramOutputStream.write((byte)(int)(paramLong >>> 0));
    paramOutputStream.write((byte)(int)(paramLong >>> 8));
    paramOutputStream.write((byte)(int)(paramLong >>> 16));
    paramOutputStream.write((byte)(int)(paramLong >>> 24));
    paramOutputStream.write((byte)(int)(paramLong >>> 32));
    paramOutputStream.write((byte)(int)(paramLong >>> 40));
    paramOutputStream.write((byte)(int)(paramLong >>> 48));
    paramOutputStream.write((byte)(int)(paramLong >>> 56));
  }
  
  static void zza(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    paramString = paramString.getBytes("UTF-8");
    zza(paramOutputStream, paramString.length);
    paramOutputStream.write(paramString, 0, paramString.length);
  }
  
  private void zza(String paramString, zza paramzza)
  {
    if (!this.zzbw.containsKey(paramString)) {}
    zza localzza;
    long l;
    for (this.zzbx += paramzza.zzca;; this.zzbx = (paramzza.zzca - localzza.zzca + l))
    {
      this.zzbw.put(paramString, paramzza);
      return;
      localzza = (zza)this.zzbw.get(paramString);
      l = this.zzbx;
    }
  }
  
  static void zza(Map<String, String> paramMap, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramMap != null)
    {
      zza(paramOutputStream, paramMap.size());
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        zza(paramOutputStream, (String)localEntry.getKey());
        zza(paramOutputStream, (String)localEntry.getValue());
      }
    }
    zza(paramOutputStream, 0);
  }
  
  private static byte[] zza(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    int i = 0;
    while (i < paramInt)
    {
      int j = paramInputStream.read(arrayOfByte, i, paramInt - i);
      if (j == -1) {
        break;
      }
      i += j;
    }
    if (i != paramInt) {
      throw new IOException(50 + "Expected " + paramInt + " bytes, read " + i + " bytes");
    }
    return arrayOfByte;
  }
  
  static int zzb(InputStream paramInputStream)
    throws IOException
  {
    return zza(paramInputStream) << 0 | 0x0 | zza(paramInputStream) << 8 | zza(paramInputStream) << 16 | zza(paramInputStream) << 24;
  }
  
  static long zzc(InputStream paramInputStream)
    throws IOException
  {
    return 0L | (zza(paramInputStream) & 0xFF) << 0 | (zza(paramInputStream) & 0xFF) << 8 | (zza(paramInputStream) & 0xFF) << 16 | (zza(paramInputStream) & 0xFF) << 24 | (zza(paramInputStream) & 0xFF) << 32 | (zza(paramInputStream) & 0xFF) << 40 | (zza(paramInputStream) & 0xFF) << 48 | (zza(paramInputStream) & 0xFF) << 56;
  }
  
  private void zzc(int paramInt)
  {
    if (this.zzbx + paramInt < this.zzbz) {}
    label119:
    label229:
    label233:
    for (;;)
    {
      return;
      if (zzs.DEBUG) {
        zzs.zza("Pruning old cache entries.", new Object[0]);
      }
      long l1 = this.zzbx;
      long l2 = SystemClock.elapsedRealtime();
      Iterator localIterator = this.zzbw.entrySet().iterator();
      int i = 0;
      zza localzza;
      if (localIterator.hasNext())
      {
        localzza = (zza)((Map.Entry)localIterator.next()).getValue();
        if (zzf(localzza.zzcb).delete())
        {
          this.zzbx -= localzza.zzca;
          localIterator.remove();
          i += 1;
          if ((float)(this.zzbx + paramInt) >= this.zzbz * 0.9F) {
            break label229;
          }
        }
      }
      for (;;)
      {
        if (!zzs.DEBUG) {
          break label233;
        }
        zzs.zza("pruned %d files, %d bytes, %d ms", new Object[] { Integer.valueOf(i), Long.valueOf(this.zzbx - l1), Long.valueOf(SystemClock.elapsedRealtime() - l2) });
        return;
        zzs.zzb("Could not delete cache entry for key=%s, filename=%s", new Object[] { localzza.zzcb, zze(localzza.zzcb) });
        break label119;
        break;
      }
    }
  }
  
  static String zzd(InputStream paramInputStream)
    throws IOException
  {
    return new String(zza(paramInputStream, (int)zzc(paramInputStream)), "UTF-8");
  }
  
  private String zze(String paramString)
  {
    int i = paramString.length() / 2;
    String str = String.valueOf(String.valueOf(paramString.substring(0, i).hashCode()));
    paramString = String.valueOf(String.valueOf(paramString.substring(i).hashCode()));
    if (paramString.length() != 0) {
      return str.concat(paramString);
    }
    return new String(str);
  }
  
  static Map<String, String> zze(InputStream paramInputStream)
    throws IOException
  {
    int j = zzb(paramInputStream);
    if (j == 0) {}
    for (Object localObject = Collections.emptyMap();; localObject = new HashMap(j))
    {
      int i = 0;
      while (i < j)
      {
        ((Map)localObject).put(zzd(paramInputStream).intern(), zzd(paramInputStream).intern());
        i += 1;
      }
    }
    return (Map<String, String>)localObject;
  }
  
  /* Error */
  public void initialize()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 45	com/google/android/gms/internal/zzv:zzby	Ljava/io/File;
    //   6: invokevirtual 283	java/io/File:exists	()Z
    //   9: ifne +36 -> 45
    //   12: aload_0
    //   13: getfield 45	com/google/android/gms/internal/zzv:zzby	Ljava/io/File;
    //   16: invokevirtual 286	java/io/File:mkdirs	()Z
    //   19: ifne +23 -> 42
    //   22: ldc_w 288
    //   25: iconst_1
    //   26: anewarray 4	java/lang/Object
    //   29: dup
    //   30: iconst_0
    //   31: aload_0
    //   32: getfield 45	com/google/android/gms/internal/zzv:zzby	Ljava/io/File;
    //   35: invokevirtual 291	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   38: aastore
    //   39: invokestatic 293	com/google/android/gms/internal/zzs:zzc	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   42: aload_0
    //   43: monitorexit
    //   44: return
    //   45: aload_0
    //   46: getfield 45	com/google/android/gms/internal/zzv:zzby	Ljava/io/File;
    //   49: invokevirtual 297	java/io/File:listFiles	()[Ljava/io/File;
    //   52: astore 5
    //   54: aload 5
    //   56: ifnull -14 -> 42
    //   59: aload 5
    //   61: arraylength
    //   62: istore_2
    //   63: iconst_0
    //   64: istore_1
    //   65: iload_1
    //   66: iload_2
    //   67: if_icmpge -25 -> 42
    //   70: aload 5
    //   72: iload_1
    //   73: aaload
    //   74: astore 6
    //   76: aconst_null
    //   77: astore_3
    //   78: new 299	java/io/BufferedInputStream
    //   81: dup
    //   82: new 301	java/io/FileInputStream
    //   85: dup
    //   86: aload 6
    //   88: invokespecial 303	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   91: invokespecial 306	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   94: astore 4
    //   96: aload 4
    //   98: astore_3
    //   99: aload 4
    //   101: invokestatic 309	com/google/android/gms/internal/zzv$zza:zzf	(Ljava/io/InputStream;)Lcom/google/android/gms/internal/zzv$zza;
    //   104: astore 7
    //   106: aload 4
    //   108: astore_3
    //   109: aload 7
    //   111: aload 6
    //   113: invokevirtual 311	java/io/File:length	()J
    //   116: putfield 58	com/google/android/gms/internal/zzv$zza:zzca	J
    //   119: aload 4
    //   121: astore_3
    //   122: aload_0
    //   123: aload 7
    //   125: getfield 197	com/google/android/gms/internal/zzv$zza:zzcb	Ljava/lang/String;
    //   128: aload 7
    //   130: invokespecial 313	com/google/android/gms/internal/zzv:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzv$zza;)V
    //   133: aload 4
    //   135: ifnull +8 -> 143
    //   138: aload 4
    //   140: invokevirtual 316	java/io/BufferedInputStream:close	()V
    //   143: iload_1
    //   144: iconst_1
    //   145: iadd
    //   146: istore_1
    //   147: goto -82 -> 65
    //   150: astore_3
    //   151: aconst_null
    //   152: astore 4
    //   154: aload 6
    //   156: ifnull +12 -> 168
    //   159: aload 4
    //   161: astore_3
    //   162: aload 6
    //   164: invokevirtual 206	java/io/File:delete	()Z
    //   167: pop
    //   168: aload 4
    //   170: ifnull -27 -> 143
    //   173: aload 4
    //   175: invokevirtual 316	java/io/BufferedInputStream:close	()V
    //   178: goto -35 -> 143
    //   181: astore_3
    //   182: goto -39 -> 143
    //   185: astore 5
    //   187: aload_3
    //   188: astore 4
    //   190: aload 5
    //   192: astore_3
    //   193: aload 4
    //   195: ifnull +8 -> 203
    //   198: aload 4
    //   200: invokevirtual 316	java/io/BufferedInputStream:close	()V
    //   203: aload_3
    //   204: athrow
    //   205: astore_3
    //   206: aload_0
    //   207: monitorexit
    //   208: aload_3
    //   209: athrow
    //   210: astore_3
    //   211: goto -68 -> 143
    //   214: astore 4
    //   216: goto -13 -> 203
    //   219: astore 5
    //   221: aload_3
    //   222: astore 4
    //   224: aload 5
    //   226: astore_3
    //   227: goto -34 -> 193
    //   230: astore_3
    //   231: goto -77 -> 154
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	234	0	this	zzv
    //   64	83	1	i	int
    //   62	6	2	j	int
    //   77	45	3	localObject1	Object
    //   150	1	3	localIOException1	IOException
    //   161	1	3	localObject2	Object
    //   181	7	3	localIOException2	IOException
    //   192	12	3	localObject3	Object
    //   205	4	3	localObject4	Object
    //   210	12	3	localIOException3	IOException
    //   226	1	3	localObject5	Object
    //   230	1	3	localIOException4	IOException
    //   94	105	4	localObject6	Object
    //   214	1	4	localIOException5	IOException
    //   222	1	4	localIOException6	IOException
    //   52	19	5	arrayOfFile	File[]
    //   185	6	5	localObject7	Object
    //   219	6	5	localObject8	Object
    //   74	89	6	localFile	File
    //   104	25	7	localzza	zza
    // Exception table:
    //   from	to	target	type
    //   78	96	150	java/io/IOException
    //   173	178	181	java/io/IOException
    //   78	96	185	finally
    //   2	42	205	finally
    //   45	54	205	finally
    //   59	63	205	finally
    //   138	143	205	finally
    //   173	178	205	finally
    //   198	203	205	finally
    //   203	205	205	finally
    //   138	143	210	java/io/IOException
    //   198	203	214	java/io/IOException
    //   99	106	219	finally
    //   109	119	219	finally
    //   122	133	219	finally
    //   162	168	219	finally
    //   99	106	230	java/io/IOException
    //   109	119	230	java/io/IOException
    //   122	133	230	java/io/IOException
  }
  
  public void remove(String paramString)
  {
    try
    {
      boolean bool = zzf(paramString).delete();
      removeEntry(paramString);
      if (!bool) {
        zzs.zzb("Could not delete cache entry for key=%s, filename=%s", new Object[] { paramString, zze(paramString) });
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public zzb.zza zza(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 41	com/google/android/gms/internal/zzv:zzbw	Ljava/util/Map;
    //   6: aload_1
    //   7: invokeinterface 55 2 0
    //   12: checkcast 10	com/google/android/gms/internal/zzv$zza
    //   15: astore 4
    //   17: aload 4
    //   19: ifnonnull +9 -> 28
    //   22: aconst_null
    //   23: astore_1
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_1
    //   27: areturn
    //   28: aload_0
    //   29: aload_1
    //   30: invokevirtual 201	com/google/android/gms/internal/zzv:zzf	(Ljava/lang/String;)Ljava/io/File;
    //   33: astore 5
    //   35: new 13	com/google/android/gms/internal/zzv$zzb
    //   38: dup
    //   39: new 301	java/io/FileInputStream
    //   42: dup
    //   43: aload 5
    //   45: invokespecial 303	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   48: aconst_null
    //   49: invokespecial 322	com/google/android/gms/internal/zzv$zzb:<init>	(Ljava/io/InputStream;Lcom/google/android/gms/internal/zzv$1;)V
    //   52: astore_3
    //   53: aload_3
    //   54: astore_2
    //   55: aload_3
    //   56: invokestatic 309	com/google/android/gms/internal/zzv$zza:zzf	(Ljava/io/InputStream;)Lcom/google/android/gms/internal/zzv$zza;
    //   59: pop
    //   60: aload_3
    //   61: astore_2
    //   62: aload 4
    //   64: aload_3
    //   65: aload 5
    //   67: invokevirtual 311	java/io/File:length	()J
    //   70: aload_3
    //   71: invokestatic 325	com/google/android/gms/internal/zzv$zzb:zza	(Lcom/google/android/gms/internal/zzv$zzb;)I
    //   74: i2l
    //   75: lsub
    //   76: l2i
    //   77: invokestatic 236	com/google/android/gms/internal/zzv:zza	(Ljava/io/InputStream;I)[B
    //   80: invokevirtual 328	com/google/android/gms/internal/zzv$zza:zzb	([B)Lcom/google/android/gms/internal/zzb$zza;
    //   83: astore 4
    //   85: aload 4
    //   87: astore_2
    //   88: aload_2
    //   89: astore_1
    //   90: aload_3
    //   91: ifnull -67 -> 24
    //   94: aload_3
    //   95: invokevirtual 329	com/google/android/gms/internal/zzv$zzb:close	()V
    //   98: aload_2
    //   99: astore_1
    //   100: goto -76 -> 24
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_1
    //   106: goto -82 -> 24
    //   109: astore 4
    //   111: aconst_null
    //   112: astore_3
    //   113: aload_3
    //   114: astore_2
    //   115: ldc_w 331
    //   118: iconst_2
    //   119: anewarray 4	java/lang/Object
    //   122: dup
    //   123: iconst_0
    //   124: aload 5
    //   126: invokevirtual 291	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   129: aastore
    //   130: dup
    //   131: iconst_1
    //   132: aload 4
    //   134: invokevirtual 332	java/io/IOException:toString	()Ljava/lang/String;
    //   137: aastore
    //   138: invokestatic 230	com/google/android/gms/internal/zzs:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   141: aload_3
    //   142: astore_2
    //   143: aload_0
    //   144: aload_1
    //   145: invokevirtual 334	com/google/android/gms/internal/zzv:remove	(Ljava/lang/String;)V
    //   148: aload_3
    //   149: ifnull +7 -> 156
    //   152: aload_3
    //   153: invokevirtual 329	com/google/android/gms/internal/zzv$zzb:close	()V
    //   156: aconst_null
    //   157: astore_1
    //   158: goto -134 -> 24
    //   161: astore_1
    //   162: aconst_null
    //   163: astore_1
    //   164: goto -140 -> 24
    //   167: astore_1
    //   168: aconst_null
    //   169: astore_2
    //   170: aload_2
    //   171: ifnull +7 -> 178
    //   174: aload_2
    //   175: invokevirtual 329	com/google/android/gms/internal/zzv$zzb:close	()V
    //   178: aload_1
    //   179: athrow
    //   180: astore_1
    //   181: aload_0
    //   182: monitorexit
    //   183: aload_1
    //   184: athrow
    //   185: astore_1
    //   186: aconst_null
    //   187: astore_1
    //   188: goto -164 -> 24
    //   191: astore_1
    //   192: goto -22 -> 170
    //   195: astore 4
    //   197: goto -84 -> 113
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	200	0	this	zzv
    //   0	200	1	paramString	String
    //   54	121	2	localObject1	Object
    //   52	101	3	localzzb	zzb
    //   15	71	4	localObject2	Object
    //   109	24	4	localIOException1	IOException
    //   195	1	4	localIOException2	IOException
    //   33	92	5	localFile	File
    // Exception table:
    //   from	to	target	type
    //   94	98	103	java/io/IOException
    //   35	53	109	java/io/IOException
    //   152	156	161	java/io/IOException
    //   35	53	167	finally
    //   2	17	180	finally
    //   28	35	180	finally
    //   94	98	180	finally
    //   152	156	180	finally
    //   174	178	180	finally
    //   178	180	180	finally
    //   174	178	185	java/io/IOException
    //   55	60	191	finally
    //   62	85	191	finally
    //   115	141	191	finally
    //   143	148	191	finally
    //   55	60	195	java/io/IOException
    //   62	85	195	java/io/IOException
  }
  
  /* Error */
  public void zza(String paramString, zzb.zza paramzza)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_2
    //   4: getfield 341	com/google/android/gms/internal/zzb$zza:data	[B
    //   7: arraylength
    //   8: invokespecial 343	com/google/android/gms/internal/zzv:zzc	(I)V
    //   11: aload_0
    //   12: aload_1
    //   13: invokevirtual 201	com/google/android/gms/internal/zzv:zzf	(Ljava/lang/String;)Ljava/io/File;
    //   16: astore_3
    //   17: new 345	java/io/FileOutputStream
    //   20: dup
    //   21: aload_3
    //   22: invokespecial 346	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   25: astore 4
    //   27: new 10	com/google/android/gms/internal/zzv$zza
    //   30: dup
    //   31: aload_1
    //   32: aload_2
    //   33: invokespecial 348	com/google/android/gms/internal/zzv$zza:<init>	(Ljava/lang/String;Lcom/google/android/gms/internal/zzb$zza;)V
    //   36: astore 5
    //   38: aload 5
    //   40: aload 4
    //   42: invokevirtual 351	com/google/android/gms/internal/zzv$zza:zza	(Ljava/io/OutputStream;)Z
    //   45: ifne +61 -> 106
    //   48: aload 4
    //   50: invokevirtual 352	java/io/FileOutputStream:close	()V
    //   53: ldc_w 354
    //   56: iconst_1
    //   57: anewarray 4	java/lang/Object
    //   60: dup
    //   61: iconst_0
    //   62: aload_3
    //   63: invokevirtual 291	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   66: aastore
    //   67: invokestatic 230	com/google/android/gms/internal/zzs:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   70: new 64	java/io/IOException
    //   73: dup
    //   74: invokespecial 355	java/io/IOException:<init>	()V
    //   77: athrow
    //   78: astore_1
    //   79: aload_3
    //   80: invokevirtual 206	java/io/File:delete	()Z
    //   83: ifne +20 -> 103
    //   86: ldc_w 357
    //   89: iconst_1
    //   90: anewarray 4	java/lang/Object
    //   93: dup
    //   94: iconst_0
    //   95: aload_3
    //   96: invokevirtual 291	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   99: aastore
    //   100: invokestatic 230	com/google/android/gms/internal/zzs:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   103: aload_0
    //   104: monitorexit
    //   105: return
    //   106: aload 4
    //   108: aload_2
    //   109: getfield 341	com/google/android/gms/internal/zzb$zza:data	[B
    //   112: invokevirtual 360	java/io/FileOutputStream:write	([B)V
    //   115: aload 4
    //   117: invokevirtual 352	java/io/FileOutputStream:close	()V
    //   120: aload_0
    //   121: aload_1
    //   122: aload 5
    //   124: invokespecial 313	com/google/android/gms/internal/zzv:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzv$zza;)V
    //   127: goto -24 -> 103
    //   130: astore_1
    //   131: aload_0
    //   132: monitorexit
    //   133: aload_1
    //   134: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	zzv
    //   0	135	1	paramString	String
    //   0	135	2	paramzza	zzb.zza
    //   16	80	3	localFile	File
    //   25	91	4	localFileOutputStream	java.io.FileOutputStream
    //   36	87	5	localzza	zza
    // Exception table:
    //   from	to	target	type
    //   17	78	78	java/io/IOException
    //   106	127	78	java/io/IOException
    //   2	17	130	finally
    //   17	78	130	finally
    //   79	103	130	finally
    //   106	127	130	finally
  }
  
  public File zzf(String paramString)
  {
    return new File(this.zzby, zze(paramString));
  }
  
  static class zza
  {
    public String zza;
    public long zzb;
    public long zzc;
    public long zzca;
    public String zzcb;
    public long zzd;
    public long zze;
    public Map<String, String> zzf;
    
    private zza() {}
    
    public zza(String paramString, zzb.zza paramzza)
    {
      this.zzcb = paramString;
      this.zzca = paramzza.data.length;
      this.zza = paramzza.zza;
      this.zzb = paramzza.zzb;
      this.zzc = paramzza.zzc;
      this.zzd = paramzza.zzd;
      this.zze = paramzza.zze;
      this.zzf = paramzza.zzf;
    }
    
    public static zza zzf(InputStream paramInputStream)
      throws IOException
    {
      zza localzza = new zza();
      if (zzv.zzb(paramInputStream) != 538247942) {
        throw new IOException();
      }
      localzza.zzcb = zzv.zzd(paramInputStream);
      localzza.zza = zzv.zzd(paramInputStream);
      if (localzza.zza.equals("")) {
        localzza.zza = null;
      }
      localzza.zzb = zzv.zzc(paramInputStream);
      localzza.zzc = zzv.zzc(paramInputStream);
      localzza.zzd = zzv.zzc(paramInputStream);
      localzza.zze = zzv.zzc(paramInputStream);
      localzza.zzf = zzv.zze(paramInputStream);
      return localzza;
    }
    
    public boolean zza(OutputStream paramOutputStream)
    {
      try
      {
        zzv.zza(paramOutputStream, 538247942);
        zzv.zza(paramOutputStream, this.zzcb);
        if (this.zza == null) {}
        for (String str = "";; str = this.zza)
        {
          zzv.zza(paramOutputStream, str);
          zzv.zza(paramOutputStream, this.zzb);
          zzv.zza(paramOutputStream, this.zzc);
          zzv.zza(paramOutputStream, this.zzd);
          zzv.zza(paramOutputStream, this.zze);
          zzv.zza(this.zzf, paramOutputStream);
          paramOutputStream.flush();
          return true;
        }
        return false;
      }
      catch (IOException paramOutputStream)
      {
        zzs.zzb("%s", new Object[] { paramOutputStream.toString() });
      }
    }
    
    public zzb.zza zzb(byte[] paramArrayOfByte)
    {
      zzb.zza localzza = new zzb.zza();
      localzza.data = paramArrayOfByte;
      localzza.zza = this.zza;
      localzza.zzb = this.zzb;
      localzza.zzc = this.zzc;
      localzza.zzd = this.zzd;
      localzza.zze = this.zze;
      localzza.zzf = this.zzf;
      return localzza;
    }
  }
  
  private static class zzb
    extends FilterInputStream
  {
    private int zzcc = 0;
    
    private zzb(InputStream paramInputStream)
    {
      super();
    }
    
    public int read()
      throws IOException
    {
      int i = super.read();
      if (i != -1) {
        this.zzcc += 1;
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 != -1) {
        this.zzcc += paramInt1;
      }
      return paramInt1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */