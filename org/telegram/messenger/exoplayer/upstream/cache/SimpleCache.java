package org.telegram.messenger.exoplayer.upstream.cache;

import android.os.ConditionVariable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class SimpleCache
  implements Cache
{
  private final File cacheDir;
  private final HashMap<String, TreeSet<CacheSpan>> cachedSpans;
  private final CacheEvictor evictor;
  private final HashMap<String, ArrayList<Cache.Listener>> listeners;
  private final HashMap<String, CacheSpan> lockedSpans;
  private long totalSpace = 0L;
  
  public SimpleCache(final File paramFile, CacheEvictor paramCacheEvictor)
  {
    this.cacheDir = paramFile;
    this.evictor = paramCacheEvictor;
    this.lockedSpans = new HashMap();
    this.cachedSpans = new HashMap();
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
          return;
        }
      }
    }.start();
    paramFile.block();
  }
  
  private void addSpan(CacheSpan paramCacheSpan)
  {
    TreeSet localTreeSet2 = (TreeSet)this.cachedSpans.get(paramCacheSpan.key);
    TreeSet localTreeSet1 = localTreeSet2;
    if (localTreeSet2 == null)
    {
      localTreeSet1 = new TreeSet();
      this.cachedSpans.put(paramCacheSpan.key, localTreeSet1);
    }
    localTreeSet1.add(paramCacheSpan);
    this.totalSpace += paramCacheSpan.length;
    notifySpanAdded(paramCacheSpan);
  }
  
  private CacheSpan getSpan(CacheSpan paramCacheSpan)
  {
    Object localObject = paramCacheSpan.key;
    long l = paramCacheSpan.position;
    TreeSet localTreeSet = (TreeSet)this.cachedSpans.get(localObject);
    if (localTreeSet == null) {
      localObject = CacheSpan.createOpenHole((String)localObject, paramCacheSpan.position);
    }
    do
    {
      return (CacheSpan)localObject;
      localCacheSpan = (CacheSpan)localTreeSet.floor(paramCacheSpan);
      if ((localCacheSpan == null) || (localCacheSpan.position > l) || (l >= localCacheSpan.position + localCacheSpan.length)) {
        break;
      }
      localObject = localCacheSpan;
    } while (localCacheSpan.file.exists());
    removeStaleSpans();
    return getSpan(paramCacheSpan);
    CacheSpan localCacheSpan = (CacheSpan)localTreeSet.ceiling(paramCacheSpan);
    if (localCacheSpan == null) {}
    for (paramCacheSpan = CacheSpan.createOpenHole((String)localObject, paramCacheSpan.position);; paramCacheSpan = CacheSpan.createClosedHole((String)localObject, paramCacheSpan.position, localCacheSpan.position - paramCacheSpan.position)) {
      return paramCacheSpan;
    }
  }
  
  private void initialize()
  {
    if (!this.cacheDir.exists()) {
      this.cacheDir.mkdirs();
    }
    File[] arrayOfFile = this.cacheDir.listFiles();
    if (arrayOfFile == null) {
      return;
    }
    int i = 0;
    if (i < arrayOfFile.length)
    {
      File localFile = arrayOfFile[i];
      if (localFile.length() == 0L) {
        localFile.delete();
      }
      for (;;)
      {
        i += 1;
        break;
        localFile = CacheSpan.upgradeIfNeeded(localFile);
        CacheSpan localCacheSpan = CacheSpan.createCacheEntry(localFile);
        if (localCacheSpan == null) {
          localFile.delete();
        } else {
          addSpan(localCacheSpan);
        }
      }
    }
    this.evictor.onCacheInitialized();
  }
  
  private void notifySpanAdded(CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanAdded(this, paramCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanAdded(this, paramCacheSpan);
  }
  
  private void notifySpanRemoved(CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanRemoved(this, paramCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanRemoved(this, paramCacheSpan);
  }
  
  private void notifySpanTouched(CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramCacheSpan1.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanTouched(this, paramCacheSpan1, paramCacheSpan2);
        i -= 1;
      }
    }
    this.evictor.onSpanTouched(this, paramCacheSpan1, paramCacheSpan2);
  }
  
  private void removeStaleSpans()
  {
    Iterator localIterator1 = this.cachedSpans.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((TreeSet)((Map.Entry)localIterator1.next()).getValue()).iterator();
      int i = 1;
      while (localIterator2.hasNext())
      {
        CacheSpan localCacheSpan = (CacheSpan)localIterator2.next();
        if (!localCacheSpan.file.exists())
        {
          localIterator2.remove();
          if (localCacheSpan.isCached) {
            this.totalSpace -= localCacheSpan.length;
          }
          notifySpanRemoved(localCacheSpan);
        }
        else
        {
          i = 0;
        }
      }
      if (i != 0) {
        localIterator1.remove();
      }
    }
  }
  
  /* Error */
  private CacheSpan startReadWriteNonBlocking(CacheSpan paramCacheSpan)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: invokespecial 116	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:getSpan	(Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;)Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;
    //   7: astore_2
    //   8: aload_2
    //   9: getfield 211	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:isCached	Z
    //   12: ifeq +47 -> 59
    //   15: aload_0
    //   16: getfield 39	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:cachedSpans	Ljava/util/HashMap;
    //   19: aload_2
    //   20: getfield 69	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:key	Ljava/lang/String;
    //   23: invokevirtual 73	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   26: checkcast 75	java/util/TreeSet
    //   29: astore_3
    //   30: aload_3
    //   31: aload_2
    //   32: invokevirtual 216	java/util/TreeSet:remove	(Ljava/lang/Object;)Z
    //   35: invokestatic 222	org/telegram/messenger/exoplayer/util/Assertions:checkState	(Z)V
    //   38: aload_2
    //   39: invokevirtual 226	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:touch	()Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;
    //   42: astore_1
    //   43: aload_3
    //   44: aload_1
    //   45: invokevirtual 84	java/util/TreeSet:add	(Ljava/lang/Object;)Z
    //   48: pop
    //   49: aload_0
    //   50: aload_2
    //   51: aload_1
    //   52: invokespecial 228	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:notifySpanTouched	(Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;)V
    //   55: aload_0
    //   56: monitorexit
    //   57: aload_1
    //   58: areturn
    //   59: aload_0
    //   60: getfield 37	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:lockedSpans	Ljava/util/HashMap;
    //   63: aload_1
    //   64: getfield 69	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:key	Ljava/lang/String;
    //   67: invokevirtual 231	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   70: ifne +21 -> 91
    //   73: aload_0
    //   74: getfield 37	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:lockedSpans	Ljava/util/HashMap;
    //   77: aload_1
    //   78: getfield 69	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:key	Ljava/lang/String;
    //   81: aload_2
    //   82: invokevirtual 80	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   85: pop
    //   86: aload_2
    //   87: astore_1
    //   88: goto -33 -> 55
    //   91: aconst_null
    //   92: astore_1
    //   93: goto -38 -> 55
    //   96: astore_1
    //   97: aload_0
    //   98: monitorexit
    //   99: aload_1
    //   100: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	101	0	this	SimpleCache
    //   0	101	1	paramCacheSpan	CacheSpan
    //   7	80	2	localCacheSpan	CacheSpan
    //   29	15	3	localTreeSet	TreeSet
    // Exception table:
    //   from	to	target	type
    //   2	55	96	finally
    //   59	86	96	finally
  }
  
  public NavigableSet<CacheSpan> addListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      ArrayList localArrayList2 = (ArrayList)this.listeners.get(paramString);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.listeners.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramListener);
      paramString = getCachedSpans(paramString);
      return paramString;
    }
    finally {}
  }
  
  public void commitFile(File paramFile)
  {
    for (;;)
    {
      try
      {
        CacheSpan localCacheSpan = CacheSpan.createCacheEntry(paramFile);
        boolean bool;
        if (localCacheSpan != null)
        {
          bool = true;
          Assertions.checkState(bool);
          Assertions.checkState(this.lockedSpans.containsKey(localCacheSpan.key));
          bool = paramFile.exists();
          if (bool) {}
        }
        else
        {
          bool = false;
          continue;
        }
        if (paramFile.length() == 0L)
        {
          paramFile.delete();
          continue;
        }
        addSpan(localCacheSpan);
      }
      finally {}
      notifyAll();
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
  public NavigableSet<CacheSpan> getCachedSpans(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 39	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:cachedSpans	Ljava/util/HashMap;
    //   6: aload_1
    //   7: invokevirtual 73	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   10: checkcast 75	java/util/TreeSet
    //   13: astore_1
    //   14: aload_1
    //   15: ifnonnull +9 -> 24
    //   18: aconst_null
    //   19: astore_1
    //   20: aload_0
    //   21: monitorexit
    //   22: aload_1
    //   23: areturn
    //   24: new 75	java/util/TreeSet
    //   27: dup
    //   28: aload_1
    //   29: invokespecial 250	java/util/TreeSet:<init>	(Ljava/util/SortedSet;)V
    //   32: astore_1
    //   33: goto -13 -> 20
    //   36: astore_1
    //   37: aload_0
    //   38: monitorexit
    //   39: aload_1
    //   40: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	SimpleCache
    //   0	41	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   2	14	36	finally
    //   24	33	36	finally
  }
  
  public Set<String> getKeys()
  {
    try
    {
      HashSet localHashSet = new HashSet(this.cachedSpans.keySet());
      return localHashSet;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isCached(String paramString, long paramLong1, long paramLong2)
  {
    for (;;)
    {
      try
      {
        Object localObject = (TreeSet)this.cachedSpans.get(paramString);
        boolean bool;
        if (localObject == null)
        {
          bool = false;
          return bool;
        }
        paramString = (CacheSpan)((TreeSet)localObject).floor(CacheSpan.createLookup(paramString, paramLong1));
        if ((paramString != null) && (paramString.position + paramString.length > paramLong1))
        {
          long l = paramLong1 + paramLong2;
          paramLong1 = paramString.position + paramString.length;
          if (paramLong1 >= l)
          {
            bool = true;
          }
          else
          {
            paramString = ((TreeSet)localObject).tailSet(paramString, false).iterator();
            if (paramString.hasNext())
            {
              localObject = (CacheSpan)paramString.next();
              if (((CacheSpan)localObject).position > paramLong1)
              {
                bool = false;
              }
              else
              {
                paramLong2 = Math.max(paramLong1, ((CacheSpan)localObject).position + ((CacheSpan)localObject).length);
                paramLong1 = paramLong2;
                if (paramLong2 >= l) {
                  bool = true;
                }
              }
            }
            else
            {
              bool = false;
            }
          }
        }
        else
        {
          bool = false;
        }
      }
      finally {}
    }
  }
  
  /* Error */
  public void releaseHoleSpan(CacheSpan paramCacheSpan)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: aload_0
    //   4: getfield 37	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:lockedSpans	Ljava/util/HashMap;
    //   7: aload_1
    //   8: getfield 69	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:key	Ljava/lang/String;
    //   11: invokevirtual 281	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   14: if_acmpne +16 -> 30
    //   17: iconst_1
    //   18: istore_2
    //   19: iload_2
    //   20: invokestatic 222	org/telegram/messenger/exoplayer/util/Assertions:checkState	(Z)V
    //   23: aload_0
    //   24: invokevirtual 246	java/lang/Object:notifyAll	()V
    //   27: aload_0
    //   28: monitorexit
    //   29: return
    //   30: iconst_0
    //   31: istore_2
    //   32: goto -13 -> 19
    //   35: astore_1
    //   36: aload_0
    //   37: monitorexit
    //   38: aload_1
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	SimpleCache
    //   0	40	1	paramCacheSpan	CacheSpan
    //   18	14	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   2	17	35	finally
    //   19	27	35	finally
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
  {
    try
    {
      TreeSet localTreeSet = (TreeSet)this.cachedSpans.get(paramCacheSpan.key);
      this.totalSpace -= paramCacheSpan.length;
      Assertions.checkState(localTreeSet.remove(paramCacheSpan));
      paramCacheSpan.file.delete();
      if (localTreeSet.isEmpty()) {
        this.cachedSpans.remove(paramCacheSpan.key);
      }
      notifySpanRemoved(paramCacheSpan);
      return;
    }
    finally {}
  }
  
  public File startFile(String paramString, long paramLong1, long paramLong2)
  {
    try
    {
      Assertions.checkState(this.lockedSpans.containsKey(paramString));
      if (!this.cacheDir.exists())
      {
        removeStaleSpans();
        this.cacheDir.mkdirs();
      }
      this.evictor.onStartFile(this, paramString, paramLong1, paramLong2);
      paramString = CacheSpan.getCacheFileName(this.cacheDir, paramString, paramLong1, System.currentTimeMillis());
      return paramString;
    }
    finally {}
  }
  
  /* Error */
  public CacheSpan startReadWrite(String paramString, long paramLong)
    throws java.lang.InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: lload_2
    //   4: invokestatic 265	org/telegram/messenger/exoplayer/upstream/cache/CacheSpan:createLookup	(Ljava/lang/String;J)Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;
    //   7: astore_1
    //   8: aload_0
    //   9: aload_1
    //   10: invokespecial 309	org/telegram/messenger/exoplayer/upstream/cache/SimpleCache:startReadWriteNonBlocking	(Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;)Lorg/telegram/messenger/exoplayer/upstream/cache/CacheSpan;
    //   13: astore 4
    //   15: aload 4
    //   17: ifnull +8 -> 25
    //   20: aload_0
    //   21: monitorexit
    //   22: aload 4
    //   24: areturn
    //   25: aload_0
    //   26: invokevirtual 312	java/lang/Object:wait	()V
    //   29: goto -21 -> 8
    //   32: astore_1
    //   33: aload_0
    //   34: monitorexit
    //   35: aload_1
    //   36: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	37	0	this	SimpleCache
    //   0	37	1	paramString	String
    //   0	37	2	paramLong	long
    //   13	10	4	localCacheSpan	CacheSpan
    // Exception table:
    //   from	to	target	type
    //   2	8	32	finally
    //   8	15	32	finally
    //   25	29	32	finally
  }
  
  public CacheSpan startReadWriteNonBlocking(String paramString, long paramLong)
  {
    try
    {
      paramString = startReadWriteNonBlocking(CacheSpan.createLookup(paramString, paramLong));
      return paramString;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/cache/SimpleCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */