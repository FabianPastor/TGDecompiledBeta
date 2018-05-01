package org.telegram.messenger.volley.toolbox;

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
import org.telegram.messenger.volley.Cache;
import org.telegram.messenger.volley.Cache.Entry;
import org.telegram.messenger.volley.VolleyLog;

public class DiskBasedCache
  implements Cache
{
  private static final int CACHE_MAGIC = 538247942;
  private static final int DEFAULT_DISK_USAGE_BYTES = 5242880;
  private static final float HYSTERESIS_FACTOR = 0.9F;
  private final Map<String, CacheHeader> mEntries = new LinkedHashMap(16, 0.75F, true);
  private final int mMaxCacheSizeInBytes;
  private final File mRootDirectory;
  private long mTotalSize = 0L;
  
  public DiskBasedCache(File paramFile)
  {
    this(paramFile, 5242880);
  }
  
  public DiskBasedCache(File paramFile, int paramInt)
  {
    this.mRootDirectory = paramFile;
    this.mMaxCacheSizeInBytes = paramInt;
  }
  
  private String getFilenameForKey(String paramString)
  {
    int i = paramString.length() / 2;
    int j = paramString.substring(0, i).hashCode();
    return String.valueOf(j) + String.valueOf(paramString.substring(i).hashCode());
  }
  
  private void pruneIfNeeded(int paramInt)
  {
    if (this.mTotalSize + paramInt < this.mMaxCacheSizeInBytes) {
      return;
    }
    if (VolleyLog.DEBUG) {
      VolleyLog.v("Pruning old cache entries.", new Object[0]);
    }
    long l1 = this.mTotalSize;
    int i = 0;
    long l2 = SystemClock.elapsedRealtime();
    Iterator localIterator = this.mEntries.entrySet().iterator();
    label61:
    int j = i;
    CacheHeader localCacheHeader;
    if (localIterator.hasNext())
    {
      localCacheHeader = (CacheHeader)((Map.Entry)localIterator.next()).getValue();
      if (!getFileForKey(localCacheHeader.key).delete()) {
        break label203;
      }
      this.mTotalSize -= localCacheHeader.size;
    }
    for (;;)
    {
      localIterator.remove();
      j = i + 1;
      i = j;
      if ((float)(this.mTotalSize + paramInt) >= this.mMaxCacheSizeInBytes * 0.9F) {
        break label61;
      }
      if (!VolleyLog.DEBUG) {
        break;
      }
      VolleyLog.v("pruned %d files, %d bytes, %d ms", new Object[] { Integer.valueOf(j), Long.valueOf(this.mTotalSize - l1), Long.valueOf(SystemClock.elapsedRealtime() - l2) });
      return;
      label203:
      VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", new Object[] { localCacheHeader.key, getFilenameForKey(localCacheHeader.key) });
    }
  }
  
  private void putEntry(String paramString, CacheHeader paramCacheHeader)
  {
    if (!this.mEntries.containsKey(paramString)) {}
    CacheHeader localCacheHeader;
    for (this.mTotalSize += paramCacheHeader.size;; this.mTotalSize += paramCacheHeader.size - localCacheHeader.size)
    {
      this.mEntries.put(paramString, paramCacheHeader);
      return;
      localCacheHeader = (CacheHeader)this.mEntries.get(paramString);
    }
  }
  
  private static int read(InputStream paramInputStream)
    throws IOException
  {
    int i = paramInputStream.read();
    if (i == -1) {
      throw new EOFException();
    }
    return i;
  }
  
  static int readInt(InputStream paramInputStream)
    throws IOException
  {
    return 0x0 | read(paramInputStream) << 0 | read(paramInputStream) << 8 | read(paramInputStream) << 16 | read(paramInputStream) << 24;
  }
  
  static long readLong(InputStream paramInputStream)
    throws IOException
  {
    return 0L | (read(paramInputStream) & 0xFF) << 0 | (read(paramInputStream) & 0xFF) << 8 | (read(paramInputStream) & 0xFF) << 16 | (read(paramInputStream) & 0xFF) << 24 | (read(paramInputStream) & 0xFF) << 32 | (read(paramInputStream) & 0xFF) << 40 | (read(paramInputStream) & 0xFF) << 48 | (read(paramInputStream) & 0xFF) << 56;
  }
  
  static String readString(InputStream paramInputStream)
    throws IOException
  {
    return new String(streamToBytes(paramInputStream, (int)readLong(paramInputStream)), "UTF-8");
  }
  
  static Map<String, String> readStringStringMap(InputStream paramInputStream)
    throws IOException
  {
    int j = readInt(paramInputStream);
    if (j == 0) {}
    for (Object localObject = Collections.emptyMap();; localObject = new HashMap(j))
    {
      int i = 0;
      while (i < j)
      {
        ((Map)localObject).put(readString(paramInputStream).intern(), readString(paramInputStream).intern());
        i += 1;
      }
    }
    return (Map<String, String>)localObject;
  }
  
  private void removeEntry(String paramString)
  {
    CacheHeader localCacheHeader = (CacheHeader)this.mEntries.get(paramString);
    if (localCacheHeader != null)
    {
      this.mTotalSize -= localCacheHeader.size;
      this.mEntries.remove(paramString);
    }
  }
  
  private static byte[] streamToBytes(InputStream paramInputStream, int paramInt)
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
      throw new IOException("Expected " + paramInt + " bytes, read " + i + " bytes");
    }
    return arrayOfByte;
  }
  
  static void writeInt(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write(paramInt >> 0 & 0xFF);
    paramOutputStream.write(paramInt >> 8 & 0xFF);
    paramOutputStream.write(paramInt >> 16 & 0xFF);
    paramOutputStream.write(paramInt >> 24 & 0xFF);
  }
  
  static void writeLong(OutputStream paramOutputStream, long paramLong)
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
  
  static void writeString(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    paramString = paramString.getBytes("UTF-8");
    writeLong(paramOutputStream, paramString.length);
    paramOutputStream.write(paramString, 0, paramString.length);
  }
  
  static void writeStringStringMap(Map<String, String> paramMap, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramMap != null)
    {
      writeInt(paramOutputStream, paramMap.size());
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        writeString(paramOutputStream, (String)localEntry.getKey());
        writeString(paramOutputStream, (String)localEntry.getValue());
      }
    }
    writeInt(paramOutputStream, 0);
  }
  
  public void clear()
  {
    int i = 0;
    try
    {
      File[] arrayOfFile = this.mRootDirectory.listFiles();
      if (arrayOfFile != null)
      {
        int j = arrayOfFile.length;
        while (i < j)
        {
          arrayOfFile[i].delete();
          i += 1;
        }
      }
      this.mEntries.clear();
      this.mTotalSize = 0L;
      VolleyLog.d("Cache cleared.", new Object[0]);
      return;
    }
    finally {}
  }
  
  /* Error */
  public Cache.Entry get(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_0
    //   6: getfield 47	org/telegram/messenger/volley/toolbox/DiskBasedCache:mEntries	Ljava/util/Map;
    //   9: aload_1
    //   10: invokeinterface 184 2 0
    //   15: checkcast 10	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader
    //   18: astore 4
    //   20: aload 4
    //   22: ifnonnull +10 -> 32
    //   25: aload 5
    //   27: astore_1
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_1
    //   31: areturn
    //   32: aload_0
    //   33: aload_1
    //   34: invokevirtual 140	org/telegram/messenger/volley/toolbox/DiskBasedCache:getFileForKey	(Ljava/lang/String;)Ljava/io/File;
    //   37: astore 7
    //   39: aconst_null
    //   40: astore_2
    //   41: aconst_null
    //   42: astore 6
    //   44: new 13	org/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream
    //   47: dup
    //   48: new 299	java/io/BufferedInputStream
    //   51: dup
    //   52: new 301	java/io/FileInputStream
    //   55: dup
    //   56: aload 7
    //   58: invokespecial 303	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   61: invokespecial 306	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   64: aconst_null
    //   65: invokespecial 309	org/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream:<init>	(Ljava/io/InputStream;Lorg/telegram/messenger/volley/toolbox/DiskBasedCache$1;)V
    //   68: astore_3
    //   69: aload_3
    //   70: invokestatic 313	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:readHeader	(Ljava/io/InputStream;)Lorg/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader;
    //   73: pop
    //   74: aload 4
    //   76: aload_3
    //   77: aload 7
    //   79: invokevirtual 315	java/io/File:length	()J
    //   82: aload_3
    //   83: invokestatic 319	org/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream:access$100	(Lorg/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream;)I
    //   86: i2l
    //   87: lsub
    //   88: l2i
    //   89: invokestatic 211	org/telegram/messenger/volley/toolbox/DiskBasedCache:streamToBytes	(Ljava/io/InputStream;I)[B
    //   92: invokevirtual 323	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:toCacheEntry	([B)Lorg/telegram/messenger/volley/Cache$Entry;
    //   95: astore_2
    //   96: aload_3
    //   97: ifnull +7 -> 104
    //   100: aload_3
    //   101: invokevirtual 326	org/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream:close	()V
    //   104: aload_2
    //   105: astore_1
    //   106: goto -78 -> 28
    //   109: astore_1
    //   110: aload 5
    //   112: astore_1
    //   113: goto -85 -> 28
    //   116: astore 4
    //   118: aload 6
    //   120: astore_3
    //   121: aload_3
    //   122: astore_2
    //   123: ldc_w 328
    //   126: iconst_2
    //   127: anewarray 4	java/lang/Object
    //   130: dup
    //   131: iconst_0
    //   132: aload 7
    //   134: invokevirtual 331	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   137: aastore
    //   138: dup
    //   139: iconst_1
    //   140: aload 4
    //   142: invokevirtual 332	java/io/IOException:toString	()Ljava/lang/String;
    //   145: aastore
    //   146: invokestatic 170	org/telegram/messenger/volley/VolleyLog:d	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   149: aload_3
    //   150: astore_2
    //   151: aload_0
    //   152: aload_1
    //   153: invokevirtual 334	org/telegram/messenger/volley/toolbox/DiskBasedCache:remove	(Ljava/lang/String;)V
    //   156: aload 5
    //   158: astore_1
    //   159: aload_3
    //   160: ifnull -132 -> 28
    //   163: aload_3
    //   164: invokevirtual 326	org/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream:close	()V
    //   167: aload 5
    //   169: astore_1
    //   170: goto -142 -> 28
    //   173: astore_1
    //   174: aload 5
    //   176: astore_1
    //   177: goto -149 -> 28
    //   180: astore_1
    //   181: aload_2
    //   182: ifnull +7 -> 189
    //   185: aload_2
    //   186: invokevirtual 326	org/telegram/messenger/volley/toolbox/DiskBasedCache$CountingInputStream:close	()V
    //   189: aload_1
    //   190: athrow
    //   191: astore_1
    //   192: aload_0
    //   193: monitorexit
    //   194: aload_1
    //   195: athrow
    //   196: astore_1
    //   197: aload 5
    //   199: astore_1
    //   200: goto -172 -> 28
    //   203: astore_1
    //   204: aload_3
    //   205: astore_2
    //   206: goto -25 -> 181
    //   209: astore 4
    //   211: goto -90 -> 121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	this	DiskBasedCache
    //   0	214	1	paramString	String
    //   40	166	2	localObject1	Object
    //   68	137	3	localObject2	Object
    //   18	57	4	localCacheHeader	CacheHeader
    //   116	25	4	localIOException1	IOException
    //   209	1	4	localIOException2	IOException
    //   1	197	5	localObject3	Object
    //   42	77	6	localObject4	Object
    //   37	96	7	localFile	File
    // Exception table:
    //   from	to	target	type
    //   100	104	109	java/io/IOException
    //   44	69	116	java/io/IOException
    //   163	167	173	java/io/IOException
    //   44	69	180	finally
    //   123	149	180	finally
    //   151	156	180	finally
    //   5	20	191	finally
    //   32	39	191	finally
    //   100	104	191	finally
    //   163	167	191	finally
    //   185	189	191	finally
    //   189	191	191	finally
    //   185	189	196	java/io/IOException
    //   69	96	203	finally
    //   69	96	209	java/io/IOException
  }
  
  public File getFileForKey(String paramString)
  {
    return new File(this.mRootDirectory, getFilenameForKey(paramString));
  }
  
  /* Error */
  public void initialize()
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_1
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 51	org/telegram/messenger/volley/toolbox/DiskBasedCache:mRootDirectory	Ljava/io/File;
    //   8: invokevirtual 341	java/io/File:exists	()Z
    //   11: ifne +36 -> 47
    //   14: aload_0
    //   15: getfield 51	org/telegram/messenger/volley/toolbox/DiskBasedCache:mRootDirectory	Ljava/io/File;
    //   18: invokevirtual 344	java/io/File:mkdirs	()Z
    //   21: ifne +23 -> 44
    //   24: ldc_w 346
    //   27: iconst_1
    //   28: anewarray 4	java/lang/Object
    //   31: dup
    //   32: iconst_0
    //   33: aload_0
    //   34: getfield 51	org/telegram/messenger/volley/toolbox/DiskBasedCache:mRootDirectory	Ljava/io/File;
    //   37: invokevirtual 331	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   40: aastore
    //   41: invokestatic 349	org/telegram/messenger/volley/VolleyLog:e	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   44: aload_0
    //   45: monitorexit
    //   46: return
    //   47: aload_0
    //   48: getfield 51	org/telegram/messenger/volley/toolbox/DiskBasedCache:mRootDirectory	Ljava/io/File;
    //   51: invokevirtual 292	java/io/File:listFiles	()[Ljava/io/File;
    //   54: astore 6
    //   56: aload 6
    //   58: ifnull -14 -> 44
    //   61: aload 6
    //   63: arraylength
    //   64: istore_2
    //   65: iload_1
    //   66: iload_2
    //   67: if_icmpge -23 -> 44
    //   70: aload 6
    //   72: iload_1
    //   73: aaload
    //   74: astore 7
    //   76: aconst_null
    //   77: astore_3
    //   78: aconst_null
    //   79: astore 5
    //   81: new 299	java/io/BufferedInputStream
    //   84: dup
    //   85: new 301	java/io/FileInputStream
    //   88: dup
    //   89: aload 7
    //   91: invokespecial 303	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   94: invokespecial 306	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   97: astore 4
    //   99: aload 4
    //   101: invokestatic 313	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:readHeader	(Ljava/io/InputStream;)Lorg/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader;
    //   104: astore_3
    //   105: aload_3
    //   106: aload 7
    //   108: invokevirtual 315	java/io/File:length	()J
    //   111: putfield 148	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:size	J
    //   114: aload_0
    //   115: aload_3
    //   116: getfield 136	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:key	Ljava/lang/String;
    //   119: aload_3
    //   120: invokespecial 351	org/telegram/messenger/volley/toolbox/DiskBasedCache:putEntry	(Ljava/lang/String;Lorg/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader;)V
    //   123: aload 4
    //   125: ifnull +8 -> 133
    //   128: aload 4
    //   130: invokevirtual 352	java/io/BufferedInputStream:close	()V
    //   133: iload_1
    //   134: iconst_1
    //   135: iadd
    //   136: istore_1
    //   137: goto -72 -> 65
    //   140: astore_3
    //   141: goto -8 -> 133
    //   144: astore_3
    //   145: aload 5
    //   147: astore 4
    //   149: aload 7
    //   151: ifnull +12 -> 163
    //   154: aload 4
    //   156: astore_3
    //   157: aload 7
    //   159: invokevirtual 145	java/io/File:delete	()Z
    //   162: pop
    //   163: aload 4
    //   165: ifnull -32 -> 133
    //   168: aload 4
    //   170: invokevirtual 352	java/io/BufferedInputStream:close	()V
    //   173: goto -40 -> 133
    //   176: astore_3
    //   177: goto -44 -> 133
    //   180: astore 4
    //   182: aload_3
    //   183: ifnull +7 -> 190
    //   186: aload_3
    //   187: invokevirtual 352	java/io/BufferedInputStream:close	()V
    //   190: aload 4
    //   192: athrow
    //   193: astore_3
    //   194: aload_0
    //   195: monitorexit
    //   196: aload_3
    //   197: athrow
    //   198: astore_3
    //   199: goto -9 -> 190
    //   202: astore 5
    //   204: aload 4
    //   206: astore_3
    //   207: aload 5
    //   209: astore 4
    //   211: goto -29 -> 182
    //   214: astore_3
    //   215: goto -66 -> 149
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	218	0	this	DiskBasedCache
    //   1	136	1	i	int
    //   64	4	2	j	int
    //   77	43	3	localCacheHeader	CacheHeader
    //   140	1	3	localIOException1	IOException
    //   144	1	3	localIOException2	IOException
    //   156	1	3	localObject1	Object
    //   176	11	3	localIOException3	IOException
    //   193	4	3	localObject2	Object
    //   198	1	3	localIOException4	IOException
    //   206	1	3	localObject3	Object
    //   214	1	3	localIOException5	IOException
    //   97	72	4	localObject4	Object
    //   180	25	4	localObject5	Object
    //   209	1	4	localObject6	Object
    //   79	67	5	localObject7	Object
    //   202	6	5	localObject8	Object
    //   54	17	6	arrayOfFile	File[]
    //   74	84	7	localFile	File
    // Exception table:
    //   from	to	target	type
    //   128	133	140	java/io/IOException
    //   81	99	144	java/io/IOException
    //   168	173	176	java/io/IOException
    //   81	99	180	finally
    //   157	163	180	finally
    //   4	44	193	finally
    //   47	56	193	finally
    //   61	65	193	finally
    //   128	133	193	finally
    //   168	173	193	finally
    //   186	190	193	finally
    //   190	193	193	finally
    //   186	190	198	java/io/IOException
    //   99	123	202	finally
    //   99	123	214	java/io/IOException
  }
  
  public void invalidate(String paramString, boolean paramBoolean)
  {
    try
    {
      Cache.Entry localEntry = get(paramString);
      if (localEntry != null)
      {
        localEntry.softTtl = 0L;
        if (paramBoolean) {
          localEntry.ttl = 0L;
        }
        put(paramString, localEntry);
      }
      return;
    }
    finally {}
  }
  
  /* Error */
  public void put(String paramString, Cache.Entry paramEntry)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_2
    //   4: getfield 371	org/telegram/messenger/volley/Cache$Entry:data	[B
    //   7: arraylength
    //   8: invokespecial 373	org/telegram/messenger/volley/toolbox/DiskBasedCache:pruneIfNeeded	(I)V
    //   11: aload_0
    //   12: aload_1
    //   13: invokevirtual 140	org/telegram/messenger/volley/toolbox/DiskBasedCache:getFileForKey	(Ljava/lang/String;)Ljava/io/File;
    //   16: astore_3
    //   17: new 375	java/io/BufferedOutputStream
    //   20: dup
    //   21: new 377	java/io/FileOutputStream
    //   24: dup
    //   25: aload_3
    //   26: invokespecial 378	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   29: invokespecial 381	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   32: astore 4
    //   34: new 10	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader
    //   37: dup
    //   38: aload_1
    //   39: aload_2
    //   40: invokespecial 383	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:<init>	(Ljava/lang/String;Lorg/telegram/messenger/volley/Cache$Entry;)V
    //   43: astore 5
    //   45: aload 5
    //   47: aload 4
    //   49: invokevirtual 387	org/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader:writeHeader	(Ljava/io/OutputStream;)Z
    //   52: ifne +61 -> 113
    //   55: aload 4
    //   57: invokevirtual 388	java/io/BufferedOutputStream:close	()V
    //   60: ldc_w 390
    //   63: iconst_1
    //   64: anewarray 4	java/lang/Object
    //   67: dup
    //   68: iconst_0
    //   69: aload_3
    //   70: invokevirtual 331	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   73: aastore
    //   74: invokestatic 170	org/telegram/messenger/volley/VolleyLog:d	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   77: new 188	java/io/IOException
    //   80: dup
    //   81: invokespecial 391	java/io/IOException:<init>	()V
    //   84: athrow
    //   85: astore_1
    //   86: aload_3
    //   87: invokevirtual 145	java/io/File:delete	()Z
    //   90: ifne +20 -> 110
    //   93: ldc_w 393
    //   96: iconst_1
    //   97: anewarray 4	java/lang/Object
    //   100: dup
    //   101: iconst_0
    //   102: aload_3
    //   103: invokevirtual 331	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   106: aastore
    //   107: invokestatic 170	org/telegram/messenger/volley/VolleyLog:d	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   110: aload_0
    //   111: monitorexit
    //   112: return
    //   113: aload 4
    //   115: aload_2
    //   116: getfield 371	org/telegram/messenger/volley/Cache$Entry:data	[B
    //   119: invokevirtual 396	java/io/BufferedOutputStream:write	([B)V
    //   122: aload 4
    //   124: invokevirtual 388	java/io/BufferedOutputStream:close	()V
    //   127: aload_0
    //   128: aload_1
    //   129: aload 5
    //   131: invokespecial 351	org/telegram/messenger/volley/toolbox/DiskBasedCache:putEntry	(Ljava/lang/String;Lorg/telegram/messenger/volley/toolbox/DiskBasedCache$CacheHeader;)V
    //   134: goto -24 -> 110
    //   137: astore_1
    //   138: aload_0
    //   139: monitorexit
    //   140: aload_1
    //   141: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	this	DiskBasedCache
    //   0	142	1	paramString	String
    //   0	142	2	paramEntry	Cache.Entry
    //   16	87	3	localFile	File
    //   32	91	4	localBufferedOutputStream	java.io.BufferedOutputStream
    //   43	87	5	localCacheHeader	CacheHeader
    // Exception table:
    //   from	to	target	type
    //   17	85	85	java/io/IOException
    //   113	134	85	java/io/IOException
    //   2	17	137	finally
    //   17	85	137	finally
    //   86	110	137	finally
    //   113	134	137	finally
  }
  
  public void remove(String paramString)
  {
    try
    {
      boolean bool = getFileForKey(paramString).delete();
      removeEntry(paramString);
      if (!bool) {
        VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", new Object[] { paramString, getFilenameForKey(paramString) });
      }
      return;
    }
    finally {}
  }
  
  static class CacheHeader
  {
    public String etag;
    public String key;
    public long lastModified;
    public Map<String, String> responseHeaders;
    public long serverDate;
    public long size;
    public long softTtl;
    public long ttl;
    
    private CacheHeader() {}
    
    public CacheHeader(String paramString, Cache.Entry paramEntry)
    {
      this.key = paramString;
      this.size = paramEntry.data.length;
      this.etag = paramEntry.etag;
      this.serverDate = paramEntry.serverDate;
      this.lastModified = paramEntry.lastModified;
      this.ttl = paramEntry.ttl;
      this.softTtl = paramEntry.softTtl;
      this.responseHeaders = paramEntry.responseHeaders;
    }
    
    public static CacheHeader readHeader(InputStream paramInputStream)
      throws IOException
    {
      CacheHeader localCacheHeader = new CacheHeader();
      if (DiskBasedCache.readInt(paramInputStream) != 538247942) {
        throw new IOException();
      }
      localCacheHeader.key = DiskBasedCache.readString(paramInputStream);
      localCacheHeader.etag = DiskBasedCache.readString(paramInputStream);
      if (localCacheHeader.etag.equals("")) {
        localCacheHeader.etag = null;
      }
      localCacheHeader.serverDate = DiskBasedCache.readLong(paramInputStream);
      localCacheHeader.lastModified = DiskBasedCache.readLong(paramInputStream);
      localCacheHeader.ttl = DiskBasedCache.readLong(paramInputStream);
      localCacheHeader.softTtl = DiskBasedCache.readLong(paramInputStream);
      localCacheHeader.responseHeaders = DiskBasedCache.readStringStringMap(paramInputStream);
      return localCacheHeader;
    }
    
    public Cache.Entry toCacheEntry(byte[] paramArrayOfByte)
    {
      Cache.Entry localEntry = new Cache.Entry();
      localEntry.data = paramArrayOfByte;
      localEntry.etag = this.etag;
      localEntry.serverDate = this.serverDate;
      localEntry.lastModified = this.lastModified;
      localEntry.ttl = this.ttl;
      localEntry.softTtl = this.softTtl;
      localEntry.responseHeaders = this.responseHeaders;
      return localEntry;
    }
    
    public boolean writeHeader(OutputStream paramOutputStream)
    {
      try
      {
        DiskBasedCache.writeInt(paramOutputStream, 538247942);
        DiskBasedCache.writeString(paramOutputStream, this.key);
        if (this.etag == null) {}
        for (String str = "";; str = this.etag)
        {
          DiskBasedCache.writeString(paramOutputStream, str);
          DiskBasedCache.writeLong(paramOutputStream, this.serverDate);
          DiskBasedCache.writeLong(paramOutputStream, this.lastModified);
          DiskBasedCache.writeLong(paramOutputStream, this.ttl);
          DiskBasedCache.writeLong(paramOutputStream, this.softTtl);
          DiskBasedCache.writeStringStringMap(this.responseHeaders, paramOutputStream);
          paramOutputStream.flush();
          return true;
        }
        return false;
      }
      catch (IOException paramOutputStream)
      {
        VolleyLog.d("%s", new Object[] { paramOutputStream.toString() });
      }
    }
  }
  
  private static class CountingInputStream
    extends FilterInputStream
  {
    private int bytesRead = 0;
    
    private CountingInputStream(InputStream paramInputStream)
    {
      super();
    }
    
    public int read()
      throws IOException
    {
      int i = super.read();
      if (i != -1) {
        this.bytesRead += 1;
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 != -1) {
        this.bytesRead += paramInt1;
      }
      return paramInt1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/DiskBasedCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */