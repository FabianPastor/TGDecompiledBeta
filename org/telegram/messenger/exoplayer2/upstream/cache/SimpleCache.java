package org.telegram.messenger.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SimpleCache
  implements Cache
{
  private static final String TAG = "SimpleCache";
  private final File cacheDir;
  private final CacheEvictor evictor;
  private final CachedContentIndex index;
  private final HashMap<String, ArrayList<Cache.Listener>> listeners;
  private long totalSpace = 0L;
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor)
  {
    this(paramFile, paramCacheEvictor, null, false);
  }
  
  SimpleCache(final File paramFile, CacheEvictor paramCacheEvictor, CachedContentIndex paramCachedContentIndex)
  {
    this.cacheDir = paramFile;
    this.evictor = paramCacheEvictor;
    this.index = paramCachedContentIndex;
    this.listeners = new HashMap();
    paramFile = new ConditionVariable();
    new Thread("SimpleCache.initialize()")
    {
      public void run()
      {
        synchronized (SimpleCache.this)
        {
          paramFile.open();
          SimpleCache.this.initialize();
          SimpleCache.this.evictor.onCacheInitialized();
          return;
        }
      }
    }.start();
    paramFile.block();
  }
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor, byte[] paramArrayOfByte) {}
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    this(paramFile, paramCacheEvictor, new CachedContentIndex(paramFile, paramArrayOfByte, paramBoolean));
  }
  
  private void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    this.index.getOrAdd(paramSimpleCacheSpan.key).addSpan(paramSimpleCacheSpan);
    this.totalSpace += paramSimpleCacheSpan.length;
    notifySpanAdded(paramSimpleCacheSpan);
  }
  
  private SimpleCacheSpan getSpan(String paramString, long paramLong)
    throws Cache.CacheException
  {
    CachedContent localCachedContent = this.index.get(paramString);
    if (localCachedContent == null)
    {
      paramString = SimpleCacheSpan.createOpenHole(paramString, paramLong);
      return paramString;
    }
    for (;;)
    {
      SimpleCacheSpan localSimpleCacheSpan = localCachedContent.getSpan(paramLong);
      paramString = localSimpleCacheSpan;
      if (!localSimpleCacheSpan.isCached) {
        break;
      }
      paramString = localSimpleCacheSpan;
      if (localSimpleCacheSpan.file.exists()) {
        break;
      }
      removeStaleSpansAndCachedContents();
    }
  }
  
  private void initialize()
  {
    if (!this.cacheDir.exists()) {
      this.cacheDir.mkdirs();
    }
    for (;;)
    {
      return;
      this.index.load();
      File[] arrayOfFile = this.cacheDir.listFiles();
      if (arrayOfFile != null)
      {
        int i = arrayOfFile.length;
        int j = 0;
        if (j < i)
        {
          File localFile = arrayOfFile[j];
          if (localFile.getName().equals("cached_content_index.exi")) {}
          for (;;)
          {
            j++;
            break;
            if (localFile.length() > 0L) {}
            for (SimpleCacheSpan localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(localFile, this.index);; localSimpleCacheSpan = null)
            {
              if (localSimpleCacheSpan == null) {
                break label113;
              }
              addSpan(localSimpleCacheSpan);
              break;
            }
            label113:
            localFile.delete();
          }
        }
        this.index.removeEmpty();
        try
        {
          this.index.store();
        }
        catch (Cache.CacheException localCacheException)
        {
          Log.e("SimpleCache", "Storing index file failed", localCacheException);
        }
      }
    }
  }
  
  private void notifySpanAdded(SimpleCacheSpan paramSimpleCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramSimpleCacheSpan.key);
    if (localArrayList != null) {
      for (int i = localArrayList.size() - 1; i >= 0; i--) {
        ((Cache.Listener)localArrayList.get(i)).onSpanAdded(this, paramSimpleCacheSpan);
      }
    }
    this.evictor.onSpanAdded(this, paramSimpleCacheSpan);
  }
  
  private void notifySpanRemoved(CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramCacheSpan.key);
    if (localArrayList != null) {
      for (int i = localArrayList.size() - 1; i >= 0; i--) {
        ((Cache.Listener)localArrayList.get(i)).onSpanRemoved(this, paramCacheSpan);
      }
    }
    this.evictor.onSpanRemoved(this, paramCacheSpan);
  }
  
  private void notifySpanTouched(SimpleCacheSpan paramSimpleCacheSpan, CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramSimpleCacheSpan.key);
    if (localArrayList != null) {
      for (int i = localArrayList.size() - 1; i >= 0; i--) {
        ((Cache.Listener)localArrayList.get(i)).onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
      }
    }
    this.evictor.onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
  }
  
  /* Error */
  private void removeSpan(CacheSpan paramCacheSpan, boolean paramBoolean)
    throws Cache.CacheException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   4: aload_1
    //   5: getfield 199	org/telegram/messenger/exoplayer2/upstream/cache/CacheSpan:key	Ljava/lang/String;
    //   8: invokevirtual 103	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;
    //   11: astore_3
    //   12: aload_3
    //   13: ifnull +11 -> 24
    //   16: aload_3
    //   17: aload_1
    //   18: invokevirtual 215	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:removeSpan	(Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;)Z
    //   21: ifne +4 -> 25
    //   24: return
    //   25: aload_0
    //   26: aload_0
    //   27: getfield 35	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:totalSpace	J
    //   30: aload_1
    //   31: getfield 216	org/telegram/messenger/exoplayer2/upstream/cache/CacheSpan:length	J
    //   34: lsub
    //   35: putfield 35	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:totalSpace	J
    //   38: iload_2
    //   39: ifeq +21 -> 60
    //   42: aload_0
    //   43: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   46: aload_3
    //   47: getfield 217	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:key	Ljava/lang/String;
    //   50: invokevirtual 221	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:maybeRemove	(Ljava/lang/String;)V
    //   53: aload_0
    //   54: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   57: invokevirtual 165	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:store	()V
    //   60: aload_0
    //   61: aload_1
    //   62: invokespecial 223	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:notifySpanRemoved	(Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;)V
    //   65: goto -41 -> 24
    //   68: astore_3
    //   69: aload_0
    //   70: aload_1
    //   71: invokespecial 223	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:notifySpanRemoved	(Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;)V
    //   74: aload_3
    //   75: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	76	0	this	SimpleCache
    //   0	76	1	paramCacheSpan	CacheSpan
    //   0	76	2	paramBoolean	boolean
    //   11	36	3	localCachedContent	CachedContent
    //   68	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   42	60	68	finally
  }
  
  private void removeStaleSpansAndCachedContents()
    throws Cache.CacheException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = this.index.getAll().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((CachedContent)localIterator1.next()).getSpans().iterator();
      while (localIterator2.hasNext())
      {
        CacheSpan localCacheSpan = (CacheSpan)localIterator2.next();
        if (!localCacheSpan.file.exists()) {
          localArrayList.add(localCacheSpan);
        }
      }
    }
    for (int i = 0; i < localArrayList.size(); i++) {
      removeSpan((CacheSpan)localArrayList.get(i), false);
    }
    this.index.removeEmpty();
    this.index.store();
  }
  
  public NavigableSet<CacheSpan> addListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      ArrayList localArrayList1 = (ArrayList)this.listeners.get(paramString);
      ArrayList localArrayList2 = localArrayList1;
      if (localArrayList1 == null)
      {
        localArrayList2 = new java/util/ArrayList;
        localArrayList2.<init>();
        this.listeners.put(paramString, localArrayList2);
      }
      localArrayList2.add(paramListener);
      paramString = getCachedSpans(paramString);
      return paramString;
    }
    finally {}
  }
  
  public void commitFile(File paramFile)
    throws Cache.CacheException
  {
    boolean bool1 = true;
    SimpleCacheSpan localSimpleCacheSpan;
    try
    {
      localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(paramFile, this.index);
      CachedContent localCachedContent;
      if (localSimpleCacheSpan != null)
      {
        bool2 = true;
        Assertions.checkState(bool2);
        localCachedContent = this.index.get(localSimpleCacheSpan.key);
        Assertions.checkNotNull(localCachedContent);
        Assertions.checkState(localCachedContent.isLocked());
        bool2 = paramFile.exists();
        if (bool2) {
          break label72;
        }
      }
      for (;;)
      {
        return;
        bool2 = false;
        break;
        label72:
        if (paramFile.length() != 0L) {
          break label94;
        }
        paramFile.delete();
      }
      paramFile = Long.valueOf(localCachedContent.getLength());
    }
    finally {}
    label94:
    if (paramFile.longValue() != -1L) {
      if (localSimpleCacheSpan.position + localSimpleCacheSpan.length > paramFile.longValue()) {
        break label158;
      }
    }
    label158:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      Assertions.checkState(bool2);
      addSpan(localSimpleCacheSpan);
      this.index.store();
      notifyAll();
      break;
    }
  }
  
  public long getCacheSpace()
  {
    try
    {
      long l = this.totalSpace;
      return l;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public long getCachedLength(String paramString, long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   6: aload_1
    //   7: invokevirtual 103	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;
    //   10: astore_1
    //   11: aload_1
    //   12: ifnull +15 -> 27
    //   15: aload_1
    //   16: lload_2
    //   17: lload 4
    //   19: invokevirtual 309	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:getCachedBytesLength	(JJ)J
    //   22: lstore_2
    //   23: aload_0
    //   24: monitorexit
    //   25: lload_2
    //   26: lreturn
    //   27: lload 4
    //   29: lneg
    //   30: lstore_2
    //   31: goto -8 -> 23
    //   34: astore_1
    //   35: aload_0
    //   36: monitorexit
    //   37: aload_1
    //   38: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	39	0	this	SimpleCache
    //   0	39	1	paramString	String
    //   0	39	2	paramLong1	long
    //   0	39	4	paramLong2	long
    // Exception table:
    //   from	to	target	type
    //   2	11	34	finally
    //   15	23	34	finally
  }
  
  /* Error */
  public NavigableSet<CacheSpan> getCachedSpans(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   6: aload_1
    //   7: invokevirtual 103	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;
    //   10: astore_1
    //   11: aload_1
    //   12: ifnull +10 -> 22
    //   15: aload_1
    //   16: invokevirtual 312	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:isEmpty	()Z
    //   19: ifeq +15 -> 34
    //   22: new 249	java/util/TreeSet
    //   25: dup
    //   26: invokespecial 313	java/util/TreeSet:<init>	()V
    //   29: astore_1
    //   30: aload_0
    //   31: monitorexit
    //   32: aload_1
    //   33: areturn
    //   34: new 249	java/util/TreeSet
    //   37: dup
    //   38: aload_1
    //   39: invokevirtual 247	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:getSpans	()Ljava/util/TreeSet;
    //   42: invokespecial 316	java/util/TreeSet:<init>	(Ljava/util/Collection;)V
    //   45: astore_1
    //   46: goto -16 -> 30
    //   49: astore_1
    //   50: aload_0
    //   51: monitorexit
    //   52: aload_1
    //   53: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	54	0	this	SimpleCache
    //   0	54	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   2	11	49	finally
    //   15	22	49	finally
    //   22	30	49	finally
    //   34	46	49	finally
  }
  
  public long getContentLength(String paramString)
  {
    try
    {
      long l = this.index.getContentLength(paramString);
      return l;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public Set<String> getKeys()
  {
    try
    {
      HashSet localHashSet = new HashSet(this.index.getKeys());
      return localHashSet;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public boolean isCached(String paramString, long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   6: aload_1
    //   7: invokevirtual 103	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;
    //   10: astore_1
    //   11: aload_1
    //   12: ifnull +26 -> 38
    //   15: aload_1
    //   16: lload_2
    //   17: lload 4
    //   19: invokevirtual 309	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:getCachedBytesLength	(JJ)J
    //   22: lstore_2
    //   23: lload_2
    //   24: lload 4
    //   26: lcmp
    //   27: iflt +11 -> 38
    //   30: iconst_1
    //   31: istore 6
    //   33: aload_0
    //   34: monitorexit
    //   35: iload 6
    //   37: ireturn
    //   38: iconst_0
    //   39: istore 6
    //   41: goto -8 -> 33
    //   44: astore_1
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_1
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	SimpleCache
    //   0	49	1	paramString	String
    //   0	49	2	paramLong1	long
    //   0	49	4	paramLong2	long
    //   31	9	6	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	11	44	finally
    //   15	23	44	finally
  }
  
  public void releaseHoleSpan(CacheSpan paramCacheSpan)
  {
    try
    {
      paramCacheSpan = this.index.get(paramCacheSpan.key);
      Assertions.checkNotNull(paramCacheSpan);
      Assertions.checkState(paramCacheSpan.isLocked());
      paramCacheSpan.setLocked(false);
      notifyAll();
      return;
    }
    finally
    {
      paramCacheSpan = finally;
      throw paramCacheSpan;
    }
  }
  
  public void removeListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      ArrayList localArrayList = (ArrayList)this.listeners.get(paramString);
      if (localArrayList != null)
      {
        localArrayList.remove(paramListener);
        if (localArrayList.isEmpty()) {
          this.listeners.remove(paramString);
        }
      }
      return;
    }
    finally {}
  }
  
  public void removeSpan(CacheSpan paramCacheSpan)
    throws Cache.CacheException
  {
    try
    {
      removeSpan(paramCacheSpan, true);
      return;
    }
    finally
    {
      paramCacheSpan = finally;
      throw paramCacheSpan;
    }
  }
  
  public void setContentLength(String paramString, long paramLong)
    throws Cache.CacheException
  {
    try
    {
      this.index.setContentLength(paramString, paramLong);
      this.index.store();
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public File startFile(String paramString, long paramLong1, long paramLong2)
    throws Cache.CacheException
  {
    try
    {
      CachedContent localCachedContent = this.index.get(paramString);
      Assertions.checkNotNull(localCachedContent);
      Assertions.checkState(localCachedContent.isLocked());
      if (!this.cacheDir.exists())
      {
        removeStaleSpansAndCachedContents();
        this.cacheDir.mkdirs();
      }
      this.evictor.onStartFile(this, paramString, paramLong1, paramLong2);
      paramString = SimpleCacheSpan.getCacheFile(this.cacheDir, localCachedContent.id, paramLong1, System.currentTimeMillis());
      return paramString;
    }
    finally {}
  }
  
  /* Error */
  public SimpleCacheSpan startReadWrite(String paramString, long paramLong)
    throws java.lang.InterruptedException, Cache.CacheException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: lload_2
    //   5: invokevirtual 374	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:startReadWriteNonBlocking	(Ljava/lang/String;J)Lorg/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan;
    //   8: astore 4
    //   10: aload 4
    //   12: ifnull +8 -> 20
    //   15: aload_0
    //   16: monitorexit
    //   17: aload 4
    //   19: areturn
    //   20: aload_0
    //   21: invokevirtual 377	java/lang/Object:wait	()V
    //   24: goto -22 -> 2
    //   27: astore_1
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_1
    //   31: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	SimpleCache
    //   0	32	1	paramString	String
    //   0	32	2	paramLong	long
    //   8	10	4	localSimpleCacheSpan	SimpleCacheSpan
    // Exception table:
    //   from	to	target	type
    //   2	10	27	finally
    //   20	24	27	finally
  }
  
  /* Error */
  public SimpleCacheSpan startReadWriteNonBlocking(String paramString, long paramLong)
    throws Cache.CacheException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: lload_2
    //   5: invokespecial 379	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:getSpan	(Ljava/lang/String;J)Lorg/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan;
    //   8: astore 4
    //   10: aload 4
    //   12: getfield 113	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan:isCached	Z
    //   15: ifeq +28 -> 43
    //   18: aload_0
    //   19: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   22: aload_1
    //   23: invokevirtual 103	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:get	(Ljava/lang/String;)Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;
    //   26: aload 4
    //   28: invokevirtual 383	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:touch	(Lorg/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan;)Lorg/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan;
    //   31: astore_1
    //   32: aload_0
    //   33: aload 4
    //   35: aload_1
    //   36: invokespecial 385	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:notifySpanTouched	(Lorg/telegram/messenger/exoplayer2/upstream/cache/SimpleCacheSpan;Lorg/telegram/messenger/exoplayer2/upstream/cache/CacheSpan;)V
    //   39: aload_0
    //   40: monitorexit
    //   41: aload_1
    //   42: areturn
    //   43: aload_0
    //   44: getfield 41	org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache:index	Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex;
    //   47: aload_1
    //   48: invokevirtual 86	org/telegram/messenger/exoplayer2/upstream/cache/CachedContentIndex:getOrAdd	(Ljava/lang/String;)Lorg/telegram/messenger/exoplayer2/upstream/cache/CachedContent;
    //   51: astore_1
    //   52: aload_1
    //   53: invokevirtual 282	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:isLocked	()Z
    //   56: ifne +14 -> 70
    //   59: aload_1
    //   60: iconst_1
    //   61: invokevirtual 334	org/telegram/messenger/exoplayer2/upstream/cache/CachedContent:setLocked	(Z)V
    //   64: aload 4
    //   66: astore_1
    //   67: goto -28 -> 39
    //   70: aconst_null
    //   71: astore_1
    //   72: goto -33 -> 39
    //   75: astore_1
    //   76: aload_0
    //   77: monitorexit
    //   78: aload_1
    //   79: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	SimpleCache
    //   0	80	1	paramString	String
    //   0	80	2	paramLong	long
    //   8	57	4	localSimpleCacheSpan	SimpleCacheSpan
    // Exception table:
    //   from	to	target	type
    //   2	39	75	finally
    //   43	64	75	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/cache/SimpleCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */