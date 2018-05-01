package com.google.android.gms.internal;

import android.os.SystemClock;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
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

public final class zzag
  implements zzb
{
  private final Map<String, zzai> zzav = new LinkedHashMap(16, 0.75F, true);
  private long zzaw = 0L;
  private final File zzax;
  private final int zzay;
  
  public zzag(File paramFile)
  {
    this(paramFile, 5242880);
  }
  
  private zzag(File paramFile, int paramInt)
  {
    this.zzax = paramFile;
    this.zzay = 5242880;
  }
  
  private final void remove(String paramString)
  {
    try
    {
      boolean bool = zze(paramString).delete();
      zzai localzzai = (zzai)this.zzav.get(paramString);
      if (localzzai != null)
      {
        this.zzaw -= localzzai.size;
        this.zzav.remove(paramString);
      }
      if (!bool) {
        zzab.zzb("Could not delete cache entry for key=%s, filename=%s", new Object[] { paramString, zzd(paramString) });
      }
      return;
    }
    finally {}
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
    paramOutputStream.write(paramInt & 0xFF);
    paramOutputStream.write(paramInt >> 8 & 0xFF);
    paramOutputStream.write(paramInt >> 16 & 0xFF);
    paramOutputStream.write(paramInt >>> 24);
  }
  
  static void zza(OutputStream paramOutputStream, long paramLong)
    throws IOException
  {
    paramOutputStream.write((byte)(int)paramLong);
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
  
  private final void zza(String paramString, zzai paramzzai)
  {
    if (!this.zzav.containsKey(paramString)) {}
    zzai localzzai;
    long l;
    for (this.zzaw += paramzzai.size;; this.zzaw = (paramzzai.size - localzzai.size + l))
    {
      this.zzav.put(paramString, paramzzai);
      return;
      localzzai = (zzai)this.zzav.get(paramString);
      l = this.zzaw;
    }
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
    return zza(paramInputStream) | 0x0 | zza(paramInputStream) << 8 | zza(paramInputStream) << 16 | zza(paramInputStream) << 24;
  }
  
  static long zzc(InputStream paramInputStream)
    throws IOException
  {
    return 0L | zza(paramInputStream) & 0xFF | (zza(paramInputStream) & 0xFF) << 8 | (zza(paramInputStream) & 0xFF) << 16 | (zza(paramInputStream) & 0xFF) << 24 | (zza(paramInputStream) & 0xFF) << 32 | (zza(paramInputStream) & 0xFF) << 40 | (zza(paramInputStream) & 0xFF) << 48 | (zza(paramInputStream) & 0xFF) << 56;
  }
  
  static String zzd(InputStream paramInputStream)
    throws IOException
  {
    return new String(zza(paramInputStream, (int)zzc(paramInputStream)), "UTF-8");
  }
  
  private static String zzd(String paramString)
  {
    int i = paramString.length() / 2;
    String str = String.valueOf(String.valueOf(paramString.substring(0, i).hashCode()));
    paramString = String.valueOf(String.valueOf(paramString.substring(i).hashCode()));
    if (paramString.length() != 0) {
      return str.concat(paramString);
    }
    return new String(str);
  }
  
  private final File zze(String paramString)
  {
    return new File(this.zzax, zzd(paramString));
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
  public final void initialize()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 37	com/google/android/gms/internal/zzag:zzax	Ljava/io/File;
    //   6: invokevirtual 211	java/io/File:exists	()Z
    //   9: ifne +35 -> 44
    //   12: aload_0
    //   13: getfield 37	com/google/android/gms/internal/zzag:zzax	Ljava/io/File;
    //   16: invokevirtual 214	java/io/File:mkdirs	()Z
    //   19: ifne +22 -> 41
    //   22: ldc -40
    //   24: iconst_1
    //   25: anewarray 4	java/lang/Object
    //   28: dup
    //   29: iconst_0
    //   30: aload_0
    //   31: getfield 37	com/google/android/gms/internal/zzag:zzax	Ljava/io/File;
    //   34: invokevirtual 219	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   37: aastore
    //   38: invokestatic 221	com/google/android/gms/internal/zzab:zzc	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   41: aload_0
    //   42: monitorexit
    //   43: return
    //   44: aload_0
    //   45: getfield 37	com/google/android/gms/internal/zzag:zzax	Ljava/io/File;
    //   48: invokevirtual 225	java/io/File:listFiles	()[Ljava/io/File;
    //   51: astore 5
    //   53: aload 5
    //   55: ifnull -14 -> 41
    //   58: aload 5
    //   60: arraylength
    //   61: istore_2
    //   62: iconst_0
    //   63: istore_1
    //   64: iload_1
    //   65: iload_2
    //   66: if_icmpge -25 -> 41
    //   69: aload 5
    //   71: iload_1
    //   72: aaload
    //   73: astore 6
    //   75: aconst_null
    //   76: astore_3
    //   77: new 227	java/io/BufferedInputStream
    //   80: dup
    //   81: new 229	java/io/FileInputStream
    //   84: dup
    //   85: aload 6
    //   87: invokespecial 231	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   90: invokespecial 234	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   93: astore 4
    //   95: aload 4
    //   97: astore_3
    //   98: aload 4
    //   100: invokestatic 238	com/google/android/gms/internal/zzai:zzf	(Ljava/io/InputStream;)Lcom/google/android/gms/internal/zzai;
    //   103: astore 7
    //   105: aload 4
    //   107: astore_3
    //   108: aload 7
    //   110: aload 6
    //   112: invokevirtual 241	java/io/File:length	()J
    //   115: putfield 62	com/google/android/gms/internal/zzai:size	J
    //   118: aload 4
    //   120: astore_3
    //   121: aload_0
    //   122: aload 7
    //   124: getfield 245	com/google/android/gms/internal/zzai:key	Ljava/lang/String;
    //   127: aload 7
    //   129: invokespecial 247	com/google/android/gms/internal/zzag:zza	(Ljava/lang/String;Lcom/google/android/gms/internal/zzai;)V
    //   132: aload 4
    //   134: invokevirtual 250	java/io/BufferedInputStream:close	()V
    //   137: iload_1
    //   138: iconst_1
    //   139: iadd
    //   140: istore_1
    //   141: goto -77 -> 64
    //   144: astore_3
    //   145: aconst_null
    //   146: astore 4
    //   148: aload 6
    //   150: ifnull +12 -> 162
    //   153: aload 4
    //   155: astore_3
    //   156: aload 6
    //   158: invokevirtual 51	java/io/File:delete	()Z
    //   161: pop
    //   162: aload 4
    //   164: ifnull -27 -> 137
    //   167: aload 4
    //   169: invokevirtual 250	java/io/BufferedInputStream:close	()V
    //   172: goto -35 -> 137
    //   175: astore_3
    //   176: goto -39 -> 137
    //   179: astore 5
    //   181: aload_3
    //   182: astore 4
    //   184: aload 5
    //   186: astore_3
    //   187: aload 4
    //   189: ifnull +8 -> 197
    //   192: aload 4
    //   194: invokevirtual 250	java/io/BufferedInputStream:close	()V
    //   197: aload_3
    //   198: athrow
    //   199: astore_3
    //   200: aload_0
    //   201: monitorexit
    //   202: aload_3
    //   203: athrow
    //   204: astore_3
    //   205: goto -68 -> 137
    //   208: astore 4
    //   210: goto -13 -> 197
    //   213: astore 5
    //   215: aload_3
    //   216: astore 4
    //   218: aload 5
    //   220: astore_3
    //   221: goto -34 -> 187
    //   224: astore_3
    //   225: goto -77 -> 148
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	228	0	this	zzag
    //   63	78	1	i	int
    //   61	6	2	j	int
    //   76	45	3	localObject1	Object
    //   144	1	3	localIOException1	IOException
    //   155	1	3	localObject2	Object
    //   175	7	3	localIOException2	IOException
    //   186	12	3	localObject3	Object
    //   199	4	3	localObject4	Object
    //   204	12	3	localIOException3	IOException
    //   220	1	3	localObject5	Object
    //   224	1	3	localIOException4	IOException
    //   93	100	4	localObject6	Object
    //   208	1	4	localIOException5	IOException
    //   216	1	4	localIOException6	IOException
    //   51	19	5	arrayOfFile	File[]
    //   179	6	5	localObject7	Object
    //   213	6	5	localObject8	Object
    //   73	84	6	localFile	File
    //   103	25	7	localzzai	zzai
    // Exception table:
    //   from	to	target	type
    //   77	95	144	java/io/IOException
    //   167	172	175	java/io/IOException
    //   77	95	179	finally
    //   2	41	199	finally
    //   44	53	199	finally
    //   58	62	199	finally
    //   132	137	199	finally
    //   167	172	199	finally
    //   192	197	199	finally
    //   197	199	199	finally
    //   132	137	204	java/io/IOException
    //   192	197	208	java/io/IOException
    //   98	105	213	finally
    //   108	118	213	finally
    //   121	132	213	finally
    //   156	162	213	finally
    //   98	105	224	java/io/IOException
    //   108	118	224	java/io/IOException
    //   121	132	224	java/io/IOException
  }
  
  /* Error */
  public final zzc zza(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 33	com/google/android/gms/internal/zzag:zzav	Ljava/util/Map;
    //   6: aload_1
    //   7: invokeinterface 57 2 0
    //   12: checkcast 59	com/google/android/gms/internal/zzai
    //   15: astore 6
    //   17: aload 6
    //   19: ifnonnull +9 -> 28
    //   22: aconst_null
    //   23: astore_1
    //   24: aload_0
    //   25: monitorexit
    //   26: aload_1
    //   27: areturn
    //   28: aload_0
    //   29: aload_1
    //   30: invokespecial 45	com/google/android/gms/internal/zzag:zze	(Ljava/lang/String;)Ljava/io/File;
    //   33: astore 5
    //   35: new 255	com/google/android/gms/internal/zzaj
    //   38: dup
    //   39: new 227	java/io/BufferedInputStream
    //   42: dup
    //   43: new 229	java/io/FileInputStream
    //   46: dup
    //   47: aload 5
    //   49: invokespecial 231	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   52: invokespecial 234	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   55: aconst_null
    //   56: invokespecial 258	com/google/android/gms/internal/zzaj:<init>	(Ljava/io/InputStream;Lcom/google/android/gms/internal/zzah;)V
    //   59: astore_3
    //   60: aload_3
    //   61: astore_2
    //   62: aload_3
    //   63: invokestatic 238	com/google/android/gms/internal/zzai:zzf	(Ljava/io/InputStream;)Lcom/google/android/gms/internal/zzai;
    //   66: pop
    //   67: aload_3
    //   68: astore_2
    //   69: aload_3
    //   70: aload 5
    //   72: invokevirtual 241	java/io/File:length	()J
    //   75: aload_3
    //   76: invokestatic 261	com/google/android/gms/internal/zzaj:zza	(Lcom/google/android/gms/internal/zzaj;)I
    //   79: i2l
    //   80: lsub
    //   81: l2i
    //   82: invokestatic 159	com/google/android/gms/internal/zzag:zza	(Ljava/io/InputStream;I)[B
    //   85: astore 7
    //   87: aload_3
    //   88: astore_2
    //   89: new 263	com/google/android/gms/internal/zzc
    //   92: dup
    //   93: invokespecial 264	com/google/android/gms/internal/zzc:<init>	()V
    //   96: astore 4
    //   98: aload_3
    //   99: astore_2
    //   100: aload 4
    //   102: aload 7
    //   104: putfield 268	com/google/android/gms/internal/zzc:data	[B
    //   107: aload_3
    //   108: astore_2
    //   109: aload 4
    //   111: aload 6
    //   113: getfield 270	com/google/android/gms/internal/zzai:zza	Ljava/lang/String;
    //   116: putfield 271	com/google/android/gms/internal/zzc:zza	Ljava/lang/String;
    //   119: aload_3
    //   120: astore_2
    //   121: aload 4
    //   123: aload 6
    //   125: getfield 273	com/google/android/gms/internal/zzai:zzb	J
    //   128: putfield 274	com/google/android/gms/internal/zzc:zzb	J
    //   131: aload_3
    //   132: astore_2
    //   133: aload 4
    //   135: aload 6
    //   137: getfield 276	com/google/android/gms/internal/zzai:zzc	J
    //   140: putfield 277	com/google/android/gms/internal/zzc:zzc	J
    //   143: aload_3
    //   144: astore_2
    //   145: aload 4
    //   147: aload 6
    //   149: getfield 279	com/google/android/gms/internal/zzai:zzd	J
    //   152: putfield 280	com/google/android/gms/internal/zzc:zzd	J
    //   155: aload_3
    //   156: astore_2
    //   157: aload 4
    //   159: aload 6
    //   161: getfield 282	com/google/android/gms/internal/zzai:zze	J
    //   164: putfield 283	com/google/android/gms/internal/zzc:zze	J
    //   167: aload_3
    //   168: astore_2
    //   169: aload 4
    //   171: aload 6
    //   173: getfield 285	com/google/android/gms/internal/zzai:zzf	Ljava/util/Map;
    //   176: putfield 286	com/google/android/gms/internal/zzc:zzf	Ljava/util/Map;
    //   179: aload_3
    //   180: invokevirtual 287	com/google/android/gms/internal/zzaj:close	()V
    //   183: aload 4
    //   185: astore_1
    //   186: goto -162 -> 24
    //   189: astore_1
    //   190: aconst_null
    //   191: astore_1
    //   192: goto -168 -> 24
    //   195: astore_3
    //   196: aconst_null
    //   197: astore_2
    //   198: ldc_w 289
    //   201: iconst_2
    //   202: anewarray 4	java/lang/Object
    //   205: dup
    //   206: iconst_0
    //   207: aload 5
    //   209: invokevirtual 219	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   212: aastore
    //   213: dup
    //   214: iconst_1
    //   215: aload_3
    //   216: invokevirtual 290	java/io/IOException:toString	()Ljava/lang/String;
    //   219: aastore
    //   220: invokestatic 76	com/google/android/gms/internal/zzab:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   223: aload_0
    //   224: aload_1
    //   225: invokespecial 292	com/google/android/gms/internal/zzag:remove	(Ljava/lang/String;)V
    //   228: aload_2
    //   229: ifnull +7 -> 236
    //   232: aload_2
    //   233: invokevirtual 287	com/google/android/gms/internal/zzaj:close	()V
    //   236: aconst_null
    //   237: astore_1
    //   238: goto -214 -> 24
    //   241: astore_1
    //   242: aconst_null
    //   243: astore_1
    //   244: goto -220 -> 24
    //   247: astore 4
    //   249: aconst_null
    //   250: astore_3
    //   251: aload_3
    //   252: astore_2
    //   253: ldc_w 289
    //   256: iconst_2
    //   257: anewarray 4	java/lang/Object
    //   260: dup
    //   261: iconst_0
    //   262: aload 5
    //   264: invokevirtual 219	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   267: aastore
    //   268: dup
    //   269: iconst_1
    //   270: aload 4
    //   272: invokevirtual 293	java/lang/NegativeArraySizeException:toString	()Ljava/lang/String;
    //   275: aastore
    //   276: invokestatic 76	com/google/android/gms/internal/zzab:zzb	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   279: aload_3
    //   280: astore_2
    //   281: aload_0
    //   282: aload_1
    //   283: invokespecial 292	com/google/android/gms/internal/zzag:remove	(Ljava/lang/String;)V
    //   286: aload_3
    //   287: ifnull +7 -> 294
    //   290: aload_3
    //   291: invokevirtual 287	com/google/android/gms/internal/zzaj:close	()V
    //   294: aconst_null
    //   295: astore_1
    //   296: goto -272 -> 24
    //   299: astore_1
    //   300: aconst_null
    //   301: astore_1
    //   302: goto -278 -> 24
    //   305: astore_1
    //   306: aconst_null
    //   307: astore_2
    //   308: aload_2
    //   309: ifnull +7 -> 316
    //   312: aload_2
    //   313: invokevirtual 287	com/google/android/gms/internal/zzaj:close	()V
    //   316: aload_1
    //   317: athrow
    //   318: astore_1
    //   319: aload_0
    //   320: monitorexit
    //   321: aload_1
    //   322: athrow
    //   323: astore_1
    //   324: aconst_null
    //   325: astore_1
    //   326: goto -302 -> 24
    //   329: astore_1
    //   330: goto -22 -> 308
    //   333: astore_1
    //   334: goto -26 -> 308
    //   337: astore 4
    //   339: goto -88 -> 251
    //   342: astore 4
    //   344: aload_3
    //   345: astore_2
    //   346: aload 4
    //   348: astore_3
    //   349: goto -151 -> 198
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	352	0	this	zzag
    //   0	352	1	paramString	String
    //   61	285	2	localObject1	Object
    //   59	121	3	localzzaj	zzaj
    //   195	21	3	localIOException1	IOException
    //   250	99	3	localObject2	Object
    //   96	88	4	localzzc	zzc
    //   247	24	4	localNegativeArraySizeException1	NegativeArraySizeException
    //   337	1	4	localNegativeArraySizeException2	NegativeArraySizeException
    //   342	5	4	localIOException2	IOException
    //   33	230	5	localFile	File
    //   15	157	6	localzzai	zzai
    //   85	18	7	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   179	183	189	java/io/IOException
    //   35	60	195	java/io/IOException
    //   232	236	241	java/io/IOException
    //   35	60	247	java/lang/NegativeArraySizeException
    //   290	294	299	java/io/IOException
    //   35	60	305	finally
    //   2	17	318	finally
    //   28	35	318	finally
    //   179	183	318	finally
    //   232	236	318	finally
    //   290	294	318	finally
    //   312	316	318	finally
    //   316	318	318	finally
    //   312	316	323	java/io/IOException
    //   62	67	329	finally
    //   69	87	329	finally
    //   89	98	329	finally
    //   100	107	329	finally
    //   109	119	329	finally
    //   121	131	329	finally
    //   133	143	329	finally
    //   145	155	329	finally
    //   157	167	329	finally
    //   169	179	329	finally
    //   253	279	329	finally
    //   281	286	329	finally
    //   198	228	333	finally
    //   62	67	337	java/lang/NegativeArraySizeException
    //   69	87	337	java/lang/NegativeArraySizeException
    //   89	98	337	java/lang/NegativeArraySizeException
    //   100	107	337	java/lang/NegativeArraySizeException
    //   109	119	337	java/lang/NegativeArraySizeException
    //   121	131	337	java/lang/NegativeArraySizeException
    //   133	143	337	java/lang/NegativeArraySizeException
    //   145	155	337	java/lang/NegativeArraySizeException
    //   157	167	337	java/lang/NegativeArraySizeException
    //   169	179	337	java/lang/NegativeArraySizeException
    //   62	67	342	java/io/IOException
    //   69	87	342	java/io/IOException
    //   89	98	342	java/io/IOException
    //   100	107	342	java/io/IOException
    //   109	119	342	java/io/IOException
    //   121	131	342	java/io/IOException
    //   133	143	342	java/io/IOException
    //   145	155	342	java/io/IOException
    //   157	167	342	java/io/IOException
    //   169	179	342	java/io/IOException
  }
  
  public final void zza(String paramString, zzc paramzzc)
  {
    int i = 0;
    label354:
    label378:
    label381:
    for (;;)
    {
      Object localObject2;
      zzai localzzai;
      try
      {
        int j = paramzzc.data.length;
        if (this.zzaw + j >= this.zzay)
        {
          if (zzab.DEBUG) {
            zzab.zza("Pruning old cache entries.", new Object[0]);
          }
          long l1 = this.zzaw;
          long l2 = SystemClock.elapsedRealtime();
          localObject1 = this.zzav.entrySet().iterator();
          if (!((Iterator)localObject1).hasNext()) {
            break label381;
          }
          localObject2 = (zzai)((Map.Entry)((Iterator)localObject1).next()).getValue();
          if (!zze(((zzai)localObject2).key).delete()) {
            continue;
          }
          this.zzaw -= ((zzai)localObject2).size;
          ((Iterator)localObject1).remove();
          i += 1;
          if ((float)(this.zzaw + j) >= this.zzay * 0.9F) {
            break label378;
          }
          if (zzab.DEBUG) {
            zzab.zza("pruned %d files, %d bytes, %d ms", new Object[] { Integer.valueOf(i), Long.valueOf(this.zzaw - l1), Long.valueOf(SystemClock.elapsedRealtime() - l2) });
          }
        }
        Object localObject1 = zze(paramString);
        try
        {
          localObject2 = new BufferedOutputStream(new FileOutputStream((File)localObject1));
          localzzai = new zzai(paramString, paramzzc);
          if (localzzai.zza((OutputStream)localObject2)) {
            break label354;
          }
          ((BufferedOutputStream)localObject2).close();
          zzab.zzb("Failed to write header for %s", new Object[] { ((File)localObject1).getAbsolutePath() });
          throw new IOException();
        }
        catch (IOException paramString)
        {
          if (!((File)localObject1).delete()) {
            zzab.zzb("Could not clean up file %s", new Object[] { ((File)localObject1).getAbsolutePath() });
          }
        }
        return;
        zzab.zzb("Could not delete cache entry for key=%s, filename=%s", new Object[] { ((zzai)localObject2).key, zzd(((zzai)localObject2).key) });
        continue;
        ((BufferedOutputStream)localObject2).write(paramzzc.data);
      }
      finally {}
      ((BufferedOutputStream)localObject2).close();
      zza(paramString, localzzai);
      continue;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */