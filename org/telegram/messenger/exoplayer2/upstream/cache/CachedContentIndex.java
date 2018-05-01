package org.telegram.messenger.exoplayer2.upstream.cache;

import android.util.SparseArray;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.AtomicFile;
import org.telegram.messenger.exoplayer2.util.ReusableBufferedOutputStream;
import org.telegram.messenger.exoplayer2.util.Util;

class CachedContentIndex
{
  public static final String FILE_NAME = "cached_content_index.exi";
  private static final int FLAG_ENCRYPTED_INDEX = 1;
  private static final String TAG = "CachedContentIndex";
  private static final int VERSION = 1;
  private final AtomicFile atomicFile;
  private ReusableBufferedOutputStream bufferedOutputStream;
  private boolean changed;
  private final Cipher cipher;
  private final boolean encrypt;
  private final SparseArray<String> idToKey;
  private final HashMap<String, CachedContent> keyToContent;
  private final SecretKeySpec secretKeySpec;
  
  public CachedContentIndex(File paramFile)
  {
    this(paramFile, null);
  }
  
  public CachedContentIndex(File paramFile, byte[] paramArrayOfByte) {}
  
  public CachedContentIndex(File paramFile, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    this.encrypt = paramBoolean;
    if (paramArrayOfByte != null) {
      if (paramArrayOfByte.length == 16) {
        paramBoolean = bool2;
      }
    }
    for (;;)
    {
      Assertions.checkArgument(paramBoolean);
      try
      {
        this.cipher = getCipher();
        SecretKeySpec localSecretKeySpec = new javax/crypto/spec/SecretKeySpec;
        localSecretKeySpec.<init>(paramArrayOfByte, "AES");
        this.secretKeySpec = localSecretKeySpec;
        this.keyToContent = new HashMap();
        this.idToKey = new SparseArray();
        this.atomicFile = new AtomicFile(new File(paramFile, "cached_content_index.exi"));
        return;
        paramBoolean = false;
      }
      catch (NoSuchAlgorithmException paramFile)
      {
        throw new IllegalStateException(paramFile);
        if (!paramBoolean) {}
        for (paramBoolean = bool1;; paramBoolean = false)
        {
          Assertions.checkState(paramBoolean);
          this.cipher = null;
          this.secretKeySpec = null;
          break;
        }
      }
      catch (NoSuchPaddingException paramFile)
      {
        for (;;) {}
      }
    }
  }
  
  private void add(CachedContent paramCachedContent)
  {
    this.keyToContent.put(paramCachedContent.key, paramCachedContent);
    this.idToKey.put(paramCachedContent.id, paramCachedContent.key);
  }
  
  private CachedContent addNew(String paramString, long paramLong)
  {
    paramString = new CachedContent(getNewId(this.idToKey), paramString, paramLong);
    addNew(paramString);
    return paramString;
  }
  
  private static Cipher getCipher()
    throws NoSuchPaddingException, NoSuchAlgorithmException
  {
    if (Util.SDK_INT == 18) {}
    for (;;)
    {
      try
      {
        Cipher localCipher1 = Cipher.getInstance("AES/CBC/PKCS5PADDING", "BC");
        return localCipher1;
      }
      catch (Throwable localThrowable) {}
      Cipher localCipher2 = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }
  }
  
  public static int getNewId(SparseArray<String> paramSparseArray)
  {
    int i = paramSparseArray.size();
    int k;
    if (i == 0)
    {
      j = 0;
      k = j;
      if (j >= 0) {}
    }
    for (int j = 0;; j++)
    {
      k = j;
      if (j < i)
      {
        if (j != paramSparseArray.keyAt(j)) {
          k = j;
        }
      }
      else
      {
        return k;
        j = paramSparseArray.keyAt(i - 1) + 1;
        break;
      }
    }
  }
  
  /* Error */
  private boolean readFile()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: aconst_null
    //   5: astore_3
    //   6: aload_3
    //   7: astore 4
    //   9: aload_1
    //   10: astore 5
    //   12: aload_2
    //   13: astore 6
    //   15: new 171	java/io/BufferedInputStream
    //   18: astore 7
    //   20: aload_3
    //   21: astore 4
    //   23: aload_1
    //   24: astore 5
    //   26: aload_2
    //   27: astore 6
    //   29: aload 7
    //   31: aload_0
    //   32: getfield 92	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lorg/telegram/messenger/exoplayer2/util/AtomicFile;
    //   35: invokevirtual 175	org/telegram/messenger/exoplayer2/util/AtomicFile:openRead	()Ljava/io/InputStream;
    //   38: invokespecial 178	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   41: aload_3
    //   42: astore 4
    //   44: aload_1
    //   45: astore 5
    //   47: aload_2
    //   48: astore 6
    //   50: new 180	java/io/DataInputStream
    //   53: astore 8
    //   55: aload_3
    //   56: astore 4
    //   58: aload_1
    //   59: astore 5
    //   61: aload_2
    //   62: astore 6
    //   64: aload 8
    //   66: aload 7
    //   68: invokespecial 181	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   71: aload 8
    //   73: invokevirtual 184	java/io/DataInputStream:readInt	()I
    //   76: istore 9
    //   78: iload 9
    //   80: iconst_1
    //   81: if_icmpeq +19 -> 100
    //   84: iconst_0
    //   85: istore 10
    //   87: aload 8
    //   89: ifnull +8 -> 97
    //   92: aload 8
    //   94: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   97: iload 10
    //   99: ireturn
    //   100: aload 8
    //   102: invokevirtual 184	java/io/DataInputStream:readInt	()I
    //   105: iconst_1
    //   106: iand
    //   107: ifeq +258 -> 365
    //   110: aload_0
    //   111: getfield 62	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   114: astore 5
    //   116: aload 5
    //   118: ifnonnull +19 -> 137
    //   121: iconst_0
    //   122: istore 10
    //   124: aload 8
    //   126: ifnull +8 -> 134
    //   129: aload 8
    //   131: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   134: goto -37 -> 97
    //   137: bipush 16
    //   139: newarray <illegal type>
    //   141: astore 4
    //   143: aload 8
    //   145: aload 4
    //   147: invokevirtual 192	java/io/DataInputStream:readFully	([B)V
    //   150: new 194	javax/crypto/spec/IvParameterSpec
    //   153: astore 5
    //   155: aload 5
    //   157: aload 4
    //   159: invokespecial 196	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   162: aload_0
    //   163: getfield 62	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   166: iconst_2
    //   167: aload_0
    //   168: getfield 71	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:secretKeySpec	Ljavax/crypto/spec/SecretKeySpec;
    //   171: aload 5
    //   173: invokevirtual 200	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   176: new 180	java/io/DataInputStream
    //   179: astore 5
    //   181: new 202	javax/crypto/CipherInputStream
    //   184: astore 4
    //   186: aload 4
    //   188: aload 7
    //   190: aload_0
    //   191: getfield 62	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   194: invokespecial 205	javax/crypto/CipherInputStream:<init>	(Ljava/io/InputStream;Ljavax/crypto/Cipher;)V
    //   197: aload 5
    //   199: aload 4
    //   201: invokespecial 181	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   204: aload 5
    //   206: astore 8
    //   208: aload 8
    //   210: astore 4
    //   212: aload 8
    //   214: astore 5
    //   216: aload 8
    //   218: astore 6
    //   220: aload 8
    //   222: invokevirtual 184	java/io/DataInputStream:readInt	()I
    //   225: istore 11
    //   227: iconst_0
    //   228: istore 9
    //   230: iconst_0
    //   231: istore 12
    //   233: iload 12
    //   235: iload 11
    //   237: if_icmpge +143 -> 380
    //   240: aload 8
    //   242: astore 4
    //   244: aload 8
    //   246: astore 5
    //   248: aload 8
    //   250: astore 6
    //   252: new 104	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent
    //   255: astore_3
    //   256: aload 8
    //   258: astore 4
    //   260: aload 8
    //   262: astore 5
    //   264: aload 8
    //   266: astore 6
    //   268: aload_3
    //   269: aload 8
    //   271: invokespecial 208	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:<init>	(Ljava/io/DataInputStream;)V
    //   274: aload 8
    //   276: astore 4
    //   278: aload 8
    //   280: astore 5
    //   282: aload 8
    //   284: astore 6
    //   286: aload_0
    //   287: aload_3
    //   288: invokespecial 210	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:add	(Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;)V
    //   291: aload 8
    //   293: astore 4
    //   295: aload 8
    //   297: astore 5
    //   299: aload 8
    //   301: astore 6
    //   303: aload_3
    //   304: invokevirtual 213	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:headerHashCode	()I
    //   307: istore 13
    //   309: iload 9
    //   311: iload 13
    //   313: iadd
    //   314: istore 9
    //   316: iinc 12 1
    //   319: goto -86 -> 233
    //   322: astore 5
    //   324: new 94	java/lang/IllegalStateException
    //   327: astore 4
    //   329: aload 4
    //   331: aload 5
    //   333: invokespecial 97	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
    //   336: aload 4
    //   338: athrow
    //   339: astore 5
    //   341: iconst_0
    //   342: istore 14
    //   344: iload 14
    //   346: istore 10
    //   348: aload 8
    //   350: ifnull -253 -> 97
    //   353: aload 8
    //   355: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   358: iload 14
    //   360: istore 10
    //   362: goto -265 -> 97
    //   365: aload_0
    //   366: getfield 50	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:encrypt	Z
    //   369: ifeq +8 -> 377
    //   372: aload_0
    //   373: iconst_1
    //   374: putfield 215	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:changed	Z
    //   377: goto -169 -> 208
    //   380: aload 8
    //   382: astore 4
    //   384: aload 8
    //   386: astore 5
    //   388: aload 8
    //   390: astore 6
    //   392: aload 8
    //   394: invokevirtual 184	java/io/DataInputStream:readInt	()I
    //   397: istore 12
    //   399: iload 12
    //   401: iload 9
    //   403: if_icmpeq +27 -> 430
    //   406: iconst_0
    //   407: istore 14
    //   409: iload 14
    //   411: istore 10
    //   413: aload 8
    //   415: ifnull -318 -> 97
    //   418: aload 8
    //   420: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   423: iload 14
    //   425: istore 10
    //   427: goto -330 -> 97
    //   430: aload 8
    //   432: ifnull +8 -> 440
    //   435: aload 8
    //   437: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   440: iconst_1
    //   441: istore 10
    //   443: goto -346 -> 97
    //   446: astore 8
    //   448: aload 4
    //   450: astore 5
    //   452: ldc 14
    //   454: ldc -39
    //   456: aload 8
    //   458: invokestatic 223	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   461: pop
    //   462: iconst_0
    //   463: istore 14
    //   465: iload 14
    //   467: istore 10
    //   469: aload 4
    //   471: ifnull -374 -> 97
    //   474: aload 4
    //   476: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   479: iload 14
    //   481: istore 10
    //   483: goto -386 -> 97
    //   486: astore 4
    //   488: aload 5
    //   490: astore 8
    //   492: aload 8
    //   494: ifnull +8 -> 502
    //   497: aload 8
    //   499: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   502: aload 4
    //   504: athrow
    //   505: astore 5
    //   507: goto -183 -> 324
    //   510: astore 5
    //   512: aload 5
    //   514: astore 4
    //   516: goto -24 -> 492
    //   519: astore 5
    //   521: aload 8
    //   523: astore 4
    //   525: aload 5
    //   527: astore 8
    //   529: goto -81 -> 448
    //   532: astore 8
    //   534: aload 6
    //   536: astore 8
    //   538: goto -197 -> 341
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	541	0	this	CachedContentIndex
    //   1	58	1	localObject1	Object
    //   3	59	2	localObject2	Object
    //   5	299	3	localCachedContent	CachedContent
    //   7	468	4	localObject3	Object
    //   486	17	4	localObject4	Object
    //   514	10	4	localObject5	Object
    //   10	288	5	localObject6	Object
    //   322	10	5	localInvalidKeyException	java.security.InvalidKeyException
    //   339	1	5	localFileNotFoundException1	java.io.FileNotFoundException
    //   386	103	5	localObject7	Object
    //   505	1	5	localInvalidAlgorithmParameterException	java.security.InvalidAlgorithmParameterException
    //   510	3	5	localObject8	Object
    //   519	7	5	localIOException1	java.io.IOException
    //   13	522	6	localObject9	Object
    //   18	171	7	localBufferedInputStream	java.io.BufferedInputStream
    //   53	383	8	localObject10	Object
    //   446	11	8	localIOException2	java.io.IOException
    //   490	38	8	localObject11	Object
    //   532	1	8	localFileNotFoundException2	java.io.FileNotFoundException
    //   536	1	8	localObject12	Object
    //   76	328	9	i	int
    //   85	397	10	bool1	boolean
    //   225	13	11	j	int
    //   231	173	12	k	int
    //   307	7	13	m	int
    //   342	138	14	bool2	boolean
    // Exception table:
    //   from	to	target	type
    //   162	176	322	java/security/InvalidKeyException
    //   71	78	339	java/io/FileNotFoundException
    //   100	116	339	java/io/FileNotFoundException
    //   137	162	339	java/io/FileNotFoundException
    //   162	176	339	java/io/FileNotFoundException
    //   176	204	339	java/io/FileNotFoundException
    //   324	339	339	java/io/FileNotFoundException
    //   365	377	339	java/io/FileNotFoundException
    //   15	20	446	java/io/IOException
    //   29	41	446	java/io/IOException
    //   50	55	446	java/io/IOException
    //   64	71	446	java/io/IOException
    //   220	227	446	java/io/IOException
    //   252	256	446	java/io/IOException
    //   268	274	446	java/io/IOException
    //   286	291	446	java/io/IOException
    //   303	309	446	java/io/IOException
    //   392	399	446	java/io/IOException
    //   15	20	486	finally
    //   29	41	486	finally
    //   50	55	486	finally
    //   64	71	486	finally
    //   220	227	486	finally
    //   252	256	486	finally
    //   268	274	486	finally
    //   286	291	486	finally
    //   303	309	486	finally
    //   392	399	486	finally
    //   452	462	486	finally
    //   162	176	505	java/security/InvalidAlgorithmParameterException
    //   71	78	510	finally
    //   100	116	510	finally
    //   137	162	510	finally
    //   162	176	510	finally
    //   176	204	510	finally
    //   324	339	510	finally
    //   365	377	510	finally
    //   71	78	519	java/io/IOException
    //   100	116	519	java/io/IOException
    //   137	162	519	java/io/IOException
    //   162	176	519	java/io/IOException
    //   176	204	519	java/io/IOException
    //   324	339	519	java/io/IOException
    //   365	377	519	java/io/IOException
    //   15	20	532	java/io/FileNotFoundException
    //   29	41	532	java/io/FileNotFoundException
    //   50	55	532	java/io/FileNotFoundException
    //   64	71	532	java/io/FileNotFoundException
    //   220	227	532	java/io/FileNotFoundException
    //   252	256	532	java/io/FileNotFoundException
    //   268	274	532	java/io/FileNotFoundException
    //   286	291	532	java/io/FileNotFoundException
    //   303	309	532	java/io/FileNotFoundException
    //   392	399	532	java/io/FileNotFoundException
  }
  
  /* Error */
  private void writeFile()
    throws Cache.CacheException
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: aconst_null
    //   5: astore_3
    //   6: aload_3
    //   7: astore 4
    //   9: aload_2
    //   10: astore 5
    //   12: aload_0
    //   13: getfield 92	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lorg/telegram/messenger/exoplayer2/util/AtomicFile;
    //   16: invokevirtual 230	org/telegram/messenger/exoplayer2/util/AtomicFile:startWrite	()Ljava/io/OutputStream;
    //   19: astore 6
    //   21: aload_3
    //   22: astore 4
    //   24: aload_2
    //   25: astore 5
    //   27: aload_0
    //   28: getfield 232	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   31: ifnonnull +296 -> 327
    //   34: aload_3
    //   35: astore 4
    //   37: aload_2
    //   38: astore 5
    //   40: new 234	org/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream
    //   43: astore 7
    //   45: aload_3
    //   46: astore 4
    //   48: aload_2
    //   49: astore 5
    //   51: aload 7
    //   53: aload 6
    //   55: invokespecial 237	org/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   58: aload_3
    //   59: astore 4
    //   61: aload_2
    //   62: astore 5
    //   64: aload_0
    //   65: aload 7
    //   67: putfield 232	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   70: aload_3
    //   71: astore 4
    //   73: aload_2
    //   74: astore 5
    //   76: new 239	java/io/DataOutputStream
    //   79: astore 7
    //   81: aload_3
    //   82: astore 4
    //   84: aload_2
    //   85: astore 5
    //   87: aload 7
    //   89: aload_0
    //   90: getfield 232	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   93: invokespecial 240	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   96: aload 7
    //   98: iconst_1
    //   99: invokevirtual 244	java/io/DataOutputStream:writeInt	(I)V
    //   102: aload_0
    //   103: getfield 50	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:encrypt	Z
    //   106: ifeq +279 -> 385
    //   109: aload 7
    //   111: iload_1
    //   112: invokevirtual 244	java/io/DataOutputStream:writeInt	(I)V
    //   115: aload_0
    //   116: getfield 50	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:encrypt	Z
    //   119: ifeq +351 -> 470
    //   122: bipush 16
    //   124: newarray <illegal type>
    //   126: astore 5
    //   128: new 246	java/util/Random
    //   131: astore 4
    //   133: aload 4
    //   135: invokespecial 247	java/util/Random:<init>	()V
    //   138: aload 4
    //   140: aload 5
    //   142: invokevirtual 250	java/util/Random:nextBytes	([B)V
    //   145: aload 7
    //   147: aload 5
    //   149: invokevirtual 253	java/io/DataOutputStream:write	([B)V
    //   152: new 194	javax/crypto/spec/IvParameterSpec
    //   155: astore 4
    //   157: aload 4
    //   159: aload 5
    //   161: invokespecial 196	javax/crypto/spec/IvParameterSpec:<init>	([B)V
    //   164: aload_0
    //   165: getfield 62	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   168: iconst_1
    //   169: aload_0
    //   170: getfield 71	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:secretKeySpec	Ljavax/crypto/spec/SecretKeySpec;
    //   173: aload 4
    //   175: invokevirtual 200	javax/crypto/Cipher:init	(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V
    //   178: aload 7
    //   180: invokevirtual 256	java/io/DataOutputStream:flush	()V
    //   183: new 239	java/io/DataOutputStream
    //   186: astore 5
    //   188: new 258	javax/crypto/CipherOutputStream
    //   191: astore 4
    //   193: aload 4
    //   195: aload_0
    //   196: getfield 232	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   199: aload_0
    //   200: getfield 62	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:cipher	Ljavax/crypto/Cipher;
    //   203: invokespecial 261	javax/crypto/CipherOutputStream:<init>	(Ljava/io/OutputStream;Ljavax/crypto/Cipher;)V
    //   206: aload 5
    //   208: aload 4
    //   210: invokespecial 240	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   213: aload 5
    //   215: astore 7
    //   217: aload 7
    //   219: astore 4
    //   221: aload 7
    //   223: astore 5
    //   225: aload 7
    //   227: aload_0
    //   228: getfield 76	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:keyToContent	Ljava/util/HashMap;
    //   231: invokevirtual 262	java/util/HashMap:size	()I
    //   234: invokevirtual 244	java/io/DataOutputStream:writeInt	(I)V
    //   237: iconst_0
    //   238: istore_1
    //   239: aload 7
    //   241: astore 4
    //   243: aload 7
    //   245: astore 5
    //   247: aload_0
    //   248: getfield 76	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:keyToContent	Ljava/util/HashMap;
    //   251: invokevirtual 266	java/util/HashMap:values	()Ljava/util/Collection;
    //   254: invokeinterface 272 1 0
    //   259: astore_3
    //   260: aload 7
    //   262: astore 4
    //   264: aload 7
    //   266: astore 5
    //   268: aload_3
    //   269: invokeinterface 277 1 0
    //   274: ifeq +146 -> 420
    //   277: aload 7
    //   279: astore 4
    //   281: aload 7
    //   283: astore 5
    //   285: aload_3
    //   286: invokeinterface 281 1 0
    //   291: checkcast 104	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent
    //   294: astore_2
    //   295: aload 7
    //   297: astore 4
    //   299: aload 7
    //   301: astore 5
    //   303: aload_2
    //   304: aload 7
    //   306: invokevirtual 285	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:writeToStream	(Ljava/io/DataOutputStream;)V
    //   309: aload 7
    //   311: astore 4
    //   313: aload 7
    //   315: astore 5
    //   317: iload_1
    //   318: aload_2
    //   319: invokevirtual 213	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:headerHashCode	()I
    //   322: iadd
    //   323: istore_1
    //   324: goto -64 -> 260
    //   327: aload_3
    //   328: astore 4
    //   330: aload_2
    //   331: astore 5
    //   333: aload_0
    //   334: getfield 232	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:bufferedOutputStream	Lorg/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream;
    //   337: aload 6
    //   339: invokevirtual 288	org/telegram/messenger/exoplayer2/util/ReusableBufferedOutputStream:reset	(Ljava/io/OutputStream;)V
    //   342: goto -272 -> 70
    //   345: astore 7
    //   347: aload 4
    //   349: astore 5
    //   351: new 226	org/telegram/messenger/exoplayer2/upstream/cache/Cache$CacheException
    //   354: astore_3
    //   355: aload 4
    //   357: astore 5
    //   359: aload_3
    //   360: aload 7
    //   362: invokespecial 289	org/telegram/messenger/exoplayer2/upstream/cache/Cache$CacheException:<init>	(Ljava/lang/Throwable;)V
    //   365: aload 4
    //   367: astore 5
    //   369: aload_3
    //   370: athrow
    //   371: astore 4
    //   373: aload 5
    //   375: astore 7
    //   377: aload 7
    //   379: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   382: aload 4
    //   384: athrow
    //   385: iconst_0
    //   386: istore_1
    //   387: goto -278 -> 109
    //   390: astore 5
    //   392: new 94	java/lang/IllegalStateException
    //   395: astore 4
    //   397: aload 4
    //   399: aload 5
    //   401: invokespecial 97	java/lang/IllegalStateException:<init>	(Ljava/lang/Throwable;)V
    //   404: aload 4
    //   406: athrow
    //   407: astore 5
    //   409: aload 7
    //   411: astore 4
    //   413: aload 5
    //   415: astore 7
    //   417: goto -70 -> 347
    //   420: aload 7
    //   422: astore 4
    //   424: aload 7
    //   426: astore 5
    //   428: aload 7
    //   430: iload_1
    //   431: invokevirtual 244	java/io/DataOutputStream:writeInt	(I)V
    //   434: aload 7
    //   436: astore 4
    //   438: aload 7
    //   440: astore 5
    //   442: aload_0
    //   443: getfield 92	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:atomicFile	Lorg/telegram/messenger/exoplayer2/util/AtomicFile;
    //   446: aload 7
    //   448: invokevirtual 292	org/telegram/messenger/exoplayer2/util/AtomicFile:endWrite	(Ljava/io/OutputStream;)V
    //   451: aconst_null
    //   452: invokestatic 188	org/telegram/messenger/exoplayer2/util/Util:closeQuietly	(Ljava/io/Closeable;)V
    //   455: return
    //   456: astore 5
    //   458: goto -66 -> 392
    //   461: astore 5
    //   463: aload 5
    //   465: astore 4
    //   467: goto -90 -> 377
    //   470: goto -253 -> 217
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	473	0	this	CachedContentIndex
    //   1	430	1	i	int
    //   3	328	2	localCachedContent	CachedContent
    //   5	365	3	localObject1	Object
    //   7	359	4	localObject2	Object
    //   371	12	4	localObject3	Object
    //   395	71	4	localObject4	Object
    //   10	364	5	localObject5	Object
    //   390	10	5	localInvalidKeyException	java.security.InvalidKeyException
    //   407	7	5	localIOException1	java.io.IOException
    //   426	15	5	localObject6	Object
    //   456	1	5	localInvalidAlgorithmParameterException	java.security.InvalidAlgorithmParameterException
    //   461	3	5	localObject7	Object
    //   19	319	6	localOutputStream	java.io.OutputStream
    //   43	271	7	localObject8	Object
    //   345	16	7	localIOException2	java.io.IOException
    //   375	72	7	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   12	21	345	java/io/IOException
    //   27	34	345	java/io/IOException
    //   40	45	345	java/io/IOException
    //   51	58	345	java/io/IOException
    //   64	70	345	java/io/IOException
    //   76	81	345	java/io/IOException
    //   87	96	345	java/io/IOException
    //   225	237	345	java/io/IOException
    //   247	260	345	java/io/IOException
    //   268	277	345	java/io/IOException
    //   285	295	345	java/io/IOException
    //   303	309	345	java/io/IOException
    //   317	324	345	java/io/IOException
    //   333	342	345	java/io/IOException
    //   428	434	345	java/io/IOException
    //   442	451	345	java/io/IOException
    //   12	21	371	finally
    //   27	34	371	finally
    //   40	45	371	finally
    //   51	58	371	finally
    //   64	70	371	finally
    //   76	81	371	finally
    //   87	96	371	finally
    //   225	237	371	finally
    //   247	260	371	finally
    //   268	277	371	finally
    //   285	295	371	finally
    //   303	309	371	finally
    //   317	324	371	finally
    //   333	342	371	finally
    //   351	355	371	finally
    //   359	365	371	finally
    //   369	371	371	finally
    //   428	434	371	finally
    //   442	451	371	finally
    //   164	178	390	java/security/InvalidKeyException
    //   96	109	407	java/io/IOException
    //   109	164	407	java/io/IOException
    //   164	178	407	java/io/IOException
    //   178	213	407	java/io/IOException
    //   392	407	407	java/io/IOException
    //   164	178	456	java/security/InvalidAlgorithmParameterException
    //   96	109	461	finally
    //   109	164	461	finally
    //   164	178	461	finally
    //   178	213	461	finally
    //   392	407	461	finally
  }
  
  void addNew(CachedContent paramCachedContent)
  {
    add(paramCachedContent);
    this.changed = true;
  }
  
  public int assignIdForKey(String paramString)
  {
    return getOrAdd(paramString).id;
  }
  
  public CachedContent get(String paramString)
  {
    return (CachedContent)this.keyToContent.get(paramString);
  }
  
  public Collection<CachedContent> getAll()
  {
    return this.keyToContent.values();
  }
  
  public long getContentLength(String paramString)
  {
    paramString = get(paramString);
    if (paramString == null) {}
    for (long l = -1L;; l = paramString.getLength()) {
      return l;
    }
  }
  
  public String getKeyForId(int paramInt)
  {
    return (String)this.idToKey.get(paramInt);
  }
  
  public Set<String> getKeys()
  {
    return this.keyToContent.keySet();
  }
  
  public CachedContent getOrAdd(String paramString)
  {
    CachedContent localCachedContent1 = (CachedContent)this.keyToContent.get(paramString);
    CachedContent localCachedContent2 = localCachedContent1;
    if (localCachedContent1 == null) {
      localCachedContent2 = addNew(paramString, -1L);
    }
    return localCachedContent2;
  }
  
  public void load()
  {
    if (!this.changed) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (!readFile())
      {
        this.atomicFile.delete();
        this.keyToContent.clear();
        this.idToKey.clear();
      }
      return;
    }
  }
  
  public void maybeRemove(String paramString)
  {
    CachedContent localCachedContent = (CachedContent)this.keyToContent.get(paramString);
    if ((localCachedContent != null) && (localCachedContent.isEmpty()) && (!localCachedContent.isLocked()))
    {
      this.keyToContent.remove(paramString);
      this.idToKey.remove(localCachedContent.id);
      this.changed = true;
    }
  }
  
  public void removeEmpty()
  {
    String[] arrayOfString = new String[this.keyToContent.size()];
    this.keyToContent.keySet().toArray(arrayOfString);
    int i = arrayOfString.length;
    for (int j = 0; j < i; j++) {
      maybeRemove(arrayOfString[j]);
    }
  }
  
  public void setContentLength(String paramString, long paramLong)
  {
    CachedContent localCachedContent = get(paramString);
    if (localCachedContent != null) {
      if (localCachedContent.getLength() != paramLong)
      {
        localCachedContent.setLength(paramLong);
        this.changed = true;
      }
    }
    for (;;)
    {
      return;
      addNew(paramString, paramLong);
    }
  }
  
  public void store()
    throws Cache.CacheException
  {
    if (!this.changed) {}
    for (;;)
    {
      return;
      writeFile();
      this.changed = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */